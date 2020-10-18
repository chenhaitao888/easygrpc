package com.cht.easygrpc.helper;

import com.cht.easygrpc.domain.KeyValuePair;
import com.cht.easygrpc.enums.FileType;
import com.cht.easygrpc.exception.ResourceNotFoundException;
import com.cht.easygrpc.exception.ResourceProcessException;
import com.google.common.base.VerifyException;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * @author : chenhaitao934
 */
public class FileHelper {

    public static final String CLASS_PATH_PRIFIX = "classpath:";
    public static final String FILE_PATH_PRIFIX = "file:";
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public FileHelper() {
    }

    public static Map<String, Path> split(String srcPath, String destPath, long sizePerSplit, long maxLineSize, char lineSeparator, ExecutorService executor) throws IOException {
        VerifyHelper.hasText(srcPath);
        VerifyHelper.hasText(destPath);
        VerifyHelper.largerThan(sizePerSplit, 0, "sizePerSplit must be more than zero");
        VerifyHelper.largerThan(maxLineSize, 0);
        File srcFile = getFirstExistFile(srcPath);
        VerifyHelper.notNull(srcFile, "source file not exists");
        Map<String, Path> partFiles = new HashMap();
        long sourceSize = Files.size(srcFile.toPath());
        createMissingDirectories(new File(destPath));
        if (sizePerSplit >= sourceSize) {
            KeyValuePair<String, Path> target = partFilePath(destPath, 0);
            Files.copy(Paths.get(srcPath), (Path)target.getValue(), StandardCopyOption.REPLACE_EXISTING);
            partFiles.put(target.getKey(), target.getValue());
            return partFiles;
        } else {
            List<Callable<KeyValuePair<String, Path>>> tasks = new LinkedList();
            List<FileHelper.FilePart> fileParts = partFile(sizePerSplit, maxLineSize, lineSeparator, srcFile, sourceSize);
            Iterator var14 = fileParts.iterator();

            while(var14.hasNext()) {
                FileHelper.FilePart filePart = (FileHelper.FilePart)var14.next();
                tasks.add(() -> {
                    KeyValuePair<String, Path> dest = partFilePath(destPath, filePart.part);
                    RandomAccessFile src = new RandomAccessFile(srcFile, "r");
                    Throwable var5 = null;

                    try {
                        FileChannel fc = src.getChannel();
                        Throwable var7 = null;

                        try {
                            writePartToFile(filePart.endOfPart - filePart.startOfPart, filePart.startOfPart, fc, (Path)dest.getValue());
                        } catch (Throwable var30) {
                            var7 = var30;
                            throw var30;
                        } finally {
                            if (fc != null) {
                                if (var7 != null) {
                                    try {
                                        fc.close();
                                    } catch (Throwable var29) {
                                        var7.addSuppressed(var29);
                                    }
                                } else {
                                    fc.close();
                                }
                            }

                        }
                    } catch (Throwable var32) {
                        var5 = var32;
                        throw var32;
                    } finally {
                        if (src != null) {
                            if (var5 != null) {
                                try {
                                    src.close();
                                } catch (Throwable var28) {
                                    var5.addSuppressed(var28);
                                }
                            } else {
                                src.close();
                            }
                        }

                    }

                    return dest;
                });
            }

            executeCallsStream(tasks, executor).forEach((dest) -> {
                Path var10000 = (Path)partFiles.put(dest.getKey(), dest.getValue());
            });
            return partFiles;
        }
    }

    private static KeyValuePair<String, Path> partFilePath(String destPath, int part) {
        String suffix = ".".concat(SecurityHelper.baseStringFixed(part, 16, 2, '0'));
        String shardName = (new File(destPath)).getName().concat(suffix);
        return new KeyValuePair(shardName, Paths.get(destPath.concat(suffix)));
    }

    private static long findNext(long begin, long end, RandomAccessFile file, char seekChar) throws IOException {
        file.seek(begin);

        do {
            if (file.getFilePointer() >= end) {
                if (file.getFilePointer() == file.length()) {
                    return file.getFilePointer();
                }

                return -1L;
            }
        } while(file.readByte() != seekChar);

        return file.getFilePointer();
    }

    private static void writePartToFile(long byteSize, long position, FileChannel sourceChannel, Path dest) throws IOException {
        RandomAccessFile toFile = new RandomAccessFile(dest.toFile(), "rw");
        Throwable var7 = null;

        try {
            FileChannel toChannel = toFile.getChannel();
            Throwable var9 = null;

            try {
                sourceChannel.position(position);
                toChannel.transferFrom(sourceChannel, 0L, byteSize);
            } catch (Throwable var32) {
                var9 = var32;
                throw var32;
            } finally {
                if (toChannel != null) {
                    if (var9 != null) {
                        try {
                            toChannel.close();
                        } catch (Throwable var31) {
                            var9.addSuppressed(var31);
                        }
                    } else {
                        toChannel.close();
                    }
                }

            }
        } catch (Throwable var34) {
            var7 = var34;
            throw var34;
        } finally {
            if (toFile != null) {
                if (var7 != null) {
                    try {
                        toFile.close();
                    } catch (Throwable var30) {
                        var7.addSuppressed(var30);
                    }
                } else {
                    toFile.close();
                }
            }

        }

    }

    public static void saveToFile(File file, InputStream input) throws IOException {
        saveToFile(file, input, false);
    }

    public static void saveToFile(File file, InputStream input, boolean overwrite) throws IOException {
        VerifyHelper.notNull(file);
        VerifyHelper.notNull(input);
        createMissingDirectories(file);
        if (overwrite && file.exists()) {
            Files.delete(file.toPath());
        }

        Files.copy(input, file.toPath(), new CopyOption[0]);
    }

    public static void createMissingDirectories(File file) {
        VerifyHelper.notNull(file);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

    }

    public static String getExtension(File file) {
        VerifyHelper.notNull(file);
        int index = file.getName().lastIndexOf(46);
        return index >= 0 && !file.getName().endsWith(".") ? file.getName().substring(index + 1) : "";
    }

    public static FileType getFileType(File file) {
        return FileType.parse(getExtension(file));
    }

    public static List<String> splitFile(String filepath, String splitor, Charset charset) throws IOException {
        return splitFile(new File(filepath), splitor, charset);
    }

    public static List<String> splitFile(File file, String splitor, Charset charset) throws IOException {
        List<String> files = new ArrayList();
        StringBuilder buf = new StringBuilder();
        Stream<String> fileStream = Files.lines(file.toPath(), charset);
        Throwable var6 = null;

        try {
            String line;
            try {
                for(Iterator var7 = ((List)fileStream.collect(Collectors.toList())).iterator(); var7.hasNext(); buf.append(line).append(StringHelper.NEW_LINE_STRING)) {
                    line = (String)var7.next();
                    if (line.trim().equals(splitor)) {
                        files.add(buf.toString());
                        buf = new StringBuilder();
                    }
                }
            } catch (Throwable var16) {
                var6 = var16;
                throw var16;
            }
        } finally {
            if (fileStream != null) {
                if (var6 != null) {
                    try {
                        fileStream.close();
                    } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                    }
                } else {
                    fileStream.close();
                }
            }

        }

        files.add(buf.toString());
        return files;
    }

    public static String readToEnd(String filepath, Charset charset) throws IOException {
        StringBuilder buf = new StringBuilder();
        Stream<String> fileStream = Files.lines(Paths.get(filepath), charset);
        Throwable var4 = null;

        try {
            fileStream.forEach((line) -> {
                buf.append(line).append(StringHelper.NEW_LINE_STRING);
            });
        } catch (Throwable var13) {
            var4 = var13;
            throw var13;
        } finally {
            if (fileStream != null) {
                if (var4 != null) {
                    try {
                        fileStream.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    fileStream.close();
                }
            }

        }

        return buf.toString();
    }

    public static String readToEnd(InputStream is) throws IOException {
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Throwable var4 = null;

        try {
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    buf.append(line).append(System.getProperty("line.separator"));
                }
            } catch (Throwable var13) {
                var4 = var13;
                throw var13;
            }
        } finally {
            if (reader != null) {
                if (var4 != null) {
                    try {
                        reader.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    reader.close();
                }
            }

        }

        return buf.toString();
    }

    public static List<String> ls(InputStream is) throws IOException {
        List<String> files = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Throwable var4 = null;

        try {
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    files.add(line);
                }
            } catch (Throwable var13) {
                var4 = var13;
                throw var13;
            }
        } finally {
            if (reader != null) {
                if (var4 != null) {
                    try {
                        reader.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    reader.close();
                }
            }

        }

        return files;
    }

    public static List<String> ls(String path) throws IOException {
        return ls(getFirstExistResource(path));
    }

    public static List<String> ls(InputStream is, Predicate<String> predicate) throws IOException {
        return (List)ls(is).stream().filter(predicate).collect(Collectors.toList());
    }

    public static List<String> ls(String path, Predicate<String> predicate) throws IOException {
        return ls(getFirstExistResource(path), predicate);
    }

    public static Path mapLocalFullPath(String path) {
        String pathStr = FileHelper.class.getResource("/").getPath().concat(path);
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            pathStr = pathStr.substring(1);
        }

        return Paths.get(pathStr);
    }

    public static String mapResource(String path) {
        return Resources.getResource(path).getPath();
    }

    public static BufferedReader readGZIPFile(String path, String encoding) throws IOException {
        InputStream fileStream = new FileInputStream(path);
        InputStream gzipStream = new GZIPInputStream(fileStream);
        Reader decoder = new InputStreamReader(gzipStream, encoding);
        return new BufferedReader(decoder);
    }

    public static String getFullFilePath(String path) {
        VerifyHelper.hasText(path);
        if (path.startsWith("classpath:")) {
            return mapLocalFullPath(path.replaceFirst("classpath:", "")).toString();
        } else if (path.startsWith("file:")) {
            return (new File(path.replaceFirst("file:", ""))).getAbsolutePath();
        } else {
            File classpath = mapLocalFullPath(path).toFile();
            File filepath = new File(path);
            return filepath.exists() ? filepath.getAbsolutePath() : classpath.getAbsolutePath();
        }
    }

    public static File getFirstExistFile(String... paths) {
        String[] var1 = paths;
        int var2 = paths.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String path = var1[var3];
            File file = new File(getFullFilePath(path));
            if (file.exists()) {
                return file;
            }
        }

        return null;
    }

    public static InputStream getFirstExistResource(String... paths) {
        File file = getFirstExistFile(paths);
        if (file != null && file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException var7) {
                throw new ResourceNotFoundException(var7);
            }
        } else {
            InputStream is = null;
            String[] var3 = paths;
            int var4 = paths.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String path = var3[var5];
                is = FileHelper.class.getResourceAsStream(path);
                if (is != null) {
                    return is;
                }
            }

            throw new ResourceNotFoundException("resource not found.");
        }
    }

    public static String getFirstExistResourcePath(String... paths) {
        File file = getFirstExistFile(paths);
        if (file != null && file.exists()) {
            return file.getAbsolutePath();
        } else {
            InputStream is = null;
            String[] var3 = paths;
            int var4 = paths.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String path = var3[var5];
                is = FileHelper.class.getResourceAsStream(path);
                if (is != null) {
                    return path;
                }
            }

            throw new ResourceNotFoundException("resource not found.");
        }
    }

    public static void delete(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory() && ((String[])Optional.ofNullable(file.list()).orElse(new String[0])).length > 0) {
                File[] var1 = (File[])Optional.ofNullable(file.listFiles()).orElse(new File[0]);
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    File subFile = var1[var3];
                    delete(subFile);
                }
            }

            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException var5) {
                throw new ResourceProcessException(var5);
            }
        }
    }

    public static void deduplication(File in, File out, String workspace, int shardNum, long maxShardSize, long maxLineSize, int batchSize, ExecutorService executor) {
        if (!in.exists()) {
            throw new ResourceNotFoundException("Input file not found.");
        } else if (out.exists() && out.isDirectory()) {
            throw new VerifyException("Output file is a directory.");
        } else {
            int actualShardNum = MathHelper.powerOfTwoFor(shardNum);
            if (workspace.endsWith(File.separator)) {
                workspace = workspace.substring(0, workspace.length() - 1);
            }

            String taskWorkspace = String.join(File.separator, workspace, StringHelper.getUUIDNoLine());
            File ws = new File(taskWorkspace);
            if (ws.exists() && !ws.isDirectory()) {
                throw new VerifyException("Workspace is not a directory.");
            } else {
                if (!ws.exists()) {
                    ws.mkdirs();
                }

                List fileParts;
                try {
                    long sourceSize = Files.size(in.toPath());
                    fileParts = partFile(maxShardSize, maxLineSize, '\n', in, sourceSize);
                } catch (IOException var39) {
                    throw new ResourceProcessException(var39);
                }

                List<Callable<Set<File>>> shardJobs = new LinkedList();
                Iterator var15 = fileParts.iterator();

                while(var15.hasNext()) {
                    FileHelper.FilePart filePart = (FileHelper.FilePart)var15.next();
                    shardJobs.add(() -> {
                        return hashShard(in, filePart.startOfPart, filePart.endOfPart, taskWorkspace, actualShardNum, batchSize);
                    });
                }

                Set shardFiles = (Set)executeCallsStream(shardJobs, executor).flatMap(Collection::stream).collect(Collectors.toSet());

                try {
                    OutputStream os = new FileOutputStream(out);
                    Throwable var17 = null;

                    try {
                        List<Callable<Boolean>> calls = new LinkedList();
                        Iterator var19 = shardFiles.iterator();

                        while(var19.hasNext()) {
                            File shardFile = (File)var19.next();
                            calls.add(() -> {
                                return deduplication(maxShardSize, maxLineSize, os, shardFile);
                            });
                        }

                        if (executeCallsStream(calls, executor).anyMatch((r) -> {
                            return !r;
                        })) {
                            logger.warn("Deduplication task not all successed.");
                        }
                    } catch (Throwable var40) {
                        var17 = var40;
                        throw var40;
                    } finally {
                        if (os != null) {
                            if (var17 != null) {
                                try {
                                    os.close();
                                } catch (Throwable var38) {
                                    var17.addSuppressed(var38);
                                }
                            } else {
                                os.close();
                            }
                        }

                    }
                } catch (IOException var42) {
                    throw new ResourceProcessException(var42);
                } finally {
                    delete(ws);
                }

            }
        }
    }

    private static boolean deduplication(long maxShardSize, long maxLineSize, OutputStream os, File shardFile) throws IOException {
        long shardSize = Files.size(shardFile.toPath());
        if (shardSize > maxShardSize) {
            Map<String, Path> shardSplits = split(shardFile.getAbsolutePath(), Paths.get(shardFile.getParent(), shardFile.getName().concat("_split")).toString(), maxShardSize, maxLineSize, '\n', (ExecutorService)null);
            File mergeFile = new File(shardFile.getAbsolutePath().concat("_merge"));
            OutputStream shardOs = new FileOutputStream(mergeFile);
            Throwable var11 = null;

            try {
                Iterator var12 = shardSplits.values().iterator();

                while(var12.hasNext()) {
                    Path shardSplit = (Path)var12.next();
                    deduplication(shardSplit.toFile(), shardOs);
                }
            } catch (Throwable var21) {
                var11 = var21;
                throw var21;
            } finally {
                if (shardOs != null) {
                    if (var11 != null) {
                        try {
                            shardOs.close();
                        } catch (Throwable var20) {
                            var11.addSuppressed(var20);
                        }
                    } else {
                        shardOs.close();
                    }
                }

            }

            return deduplication(mergeFile, os);
        } else {
            return deduplication(shardFile, os);
        }
    }

    private static boolean deduplication(File src, OutputStream out) {
        try {
            Set<String> contentSet = new HashSet();
            Stream<String> lines = Files.lines(src.toPath());
            Throwable var4 = null;

            try {
                lines.forEach(contentSet::add);
            } catch (Throwable var14) {
                var4 = var14;
                throw var14;
            } finally {
                if (lines != null) {
                    if (var4 != null) {
                        try {
                            lines.close();
                        } catch (Throwable var13) {
                            var4.addSuppressed(var13);
                        }
                    } else {
                        lines.close();
                    }
                }

            }

            Iterator var17 = contentSet.iterator();

            while(var17.hasNext()) {
                String line = (String)var17.next();
                out.write(line.concat(System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }

            return true;
        } catch (IOException var16) {
            logger.error("Fail to deduplication", var16);
            return false;
        }
    }

    private static Set<File> hashShard(File in, long startPointer, long endPointer, String workspace, int shardNum, int batchSize) {
        Set<File> shardFiles = new HashSet();
        OutputStream[] shardStreams = new OutputStream[shardNum];
        Map<Integer, List<String>> contentMap = new HashMap(shardNum);

        for(int i = 0; i < shardNum; ++i) {
            try {
                contentMap.put(i, new LinkedList());
                File shardFile = new File(String.join("/", workspace, String.valueOf(i)));
                shardFiles.add(shardFile);
                createMissingDirectories(shardFile);
                shardStreams[i] = new FileOutputStream(shardFile, true);
            } catch (IOException var26) {
                throw new ResourceProcessException(var26);
            }
        }

        try {
            RandomAccessFile inFile = new RandomAccessFile(in, "r");
            Throwable var31 = null;

            try {
                logger.debug("Reading from {} to {}", startPointer, endPointer);
                inFile.seek(startPointer);

                String line;
                int shard;
                while(inFile.getFilePointer() < endPointer && (line = inFile.readLine()) != null) {
                    if (!StringHelper.isEmpty(line)) {
                        shard = shardNum - 1 & MathHelper.hash(line);
                        List<String> lines = (List)contentMap.get(shard);
                        lines.add(line);
                        if (lines.size() > batchSize) {
                            OutputStream os = shardStreams[shard];
                            os.write(String.join(System.lineSeparator(), lines).concat(System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                            lines.clear();
                        }
                    }
                }

                for(shard = 0; shard < shardNum; ++shard) {
                    OutputStream os = shardStreams[shard];
                    List<String> lines = (List)contentMap.get(shard);
                    if (os != null && !lines.isEmpty()) {
                        os.write(String.join(System.lineSeparator(), lines).concat(System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                    }

                    if (os != null) {
                        os.flush();
                        os.close();
                    }
                }
            } catch (Throwable var27) {
                var31 = var27;
                throw var27;
            } finally {
                if (inFile != null) {
                    if (var31 != null) {
                        try {
                            inFile.close();
                        } catch (Throwable var25) {
                            var31.addSuppressed(var25);
                        }
                    } else {
                        inFile.close();
                    }
                }

            }

            return shardFiles;
        } catch (IOException var29) {
            throw new ResourceProcessException(var29);
        }
    }

    private static <T> Stream<T> executeCallsStream(List<Callable<T>> calls, ExecutorService executor) {
        if (CollectionHelper.isEmpty(calls)) {
            return Stream.empty();
        } else if (executor == null) {
            return calls.parallelStream().map((call) -> {
                try {
                    return call.call();
                } catch (Exception var2) {
                    logger.error(var2.getMessage(), var2);
                    throw new ResourceProcessException(var2);
                }
            });
        } else {
            try {
                return executor.invokeAll(calls).stream().map((future) -> {
                    try {
                        return future.get();
                    } catch (ExecutionException | InterruptedException var2) {
                        logger.error(var2.getMessage(), var2);
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }).filter(Objects::nonNull);
            } catch (InterruptedException var3) {
                logger.error(var3.getMessage(), var3);
                Thread.currentThread().interrupt();
                return Stream.empty();
            }
        }
    }

    private static List<FileHelper.FilePart> partFile(long sizePerSplit, long maxLineSize, char lineSeparator, File srcFile,
                                             long sourceSize) throws IOException {
        AtomicInteger partCounter = new AtomicInteger();
        List<FileHelper.FilePart> fileParts = new LinkedList();
        RandomAccessFile sourceFile = new RandomAccessFile(srcFile, "r");
        Throwable var11 = null;

        try {
            long pointer = 0L;

            while(pointer < sourceSize) {
                long startOfPart = pointer;
                pointer += sizePerSplit;
                long next = Math.min(findNext(pointer, Math.min(pointer + maxLineSize, sourceSize), sourceFile, lineSeparator) - 1L, sourceSize);
                long endOfPart = next <= 0L ? sourceSize : next;
                int part = partCounter.getAndIncrement();
                pointer = endOfPart + 1L;
                fileParts.add(new FileHelper.FilePart(part, startOfPart, endOfPart));
            }
        } catch (Throwable var28) {
            var11 = var28;
            throw var28;
        } finally {
            if (sourceFile != null) {
                if (var11 != null) {
                    try {
                        sourceFile.close();
                    } catch (Throwable var27) {
                        var11.addSuppressed(var27);
                    }
                } else {
                    sourceFile.close();
                }
            }

        }

        return fileParts;
    }

    private static class FilePart {
        private int part;
        private long startOfPart;
        private long endOfPart;

        public FilePart(int part, long startOfPart, long endOfPart) {
            this.part = part;
            this.startOfPart = startOfPart;
            this.endOfPart = endOfPart;
        }
    }
}
