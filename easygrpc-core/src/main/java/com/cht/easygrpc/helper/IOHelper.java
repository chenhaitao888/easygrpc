package com.cht.easygrpc.helper;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : chenhaitao934
 */
public interface IOHelper {

    static void writeFile(String fileName, byte[] bytes) throws IOException {
        if (fileName != null) {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }


    static byte[] readStreamBytes(InputStream inputStream) throws IOException {
        byte[] cache = new byte[2048];
        int len;
        byte[] bytes = new byte[0];
        while ((len = inputStream.read(cache)) > 0) {
            byte[] temp = bytes;
            bytes = new byte[bytes.length + len];
            System.arraycopy(temp, 0, bytes, 0, temp.length);
            System.arraycopy(cache, 0, bytes, temp.length, len);
        }
        if (bytes.length == 0) {
            return null;
        }
        return bytes;
    }

    static byte[] readStreamBytesAndClose(InputStream inputStream) throws IOException {
        byte[] bytes = readStreamBytes(inputStream);
        inputStream.close();
        return bytes;
    }

    static Map<String, Object> readUrlStream(URL url) throws UnsupportedEncodingException, IOException{
        Map<String, Object> map  = new HashMap<>();
        BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        while (true) {
            String line = r.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }

            int i = line.indexOf('=');
            if (i > 0) {
                String name = line.substring(0, i).trim();
                String clazz = line.substring(i + 1).trim();
                map.put(name, clazz);
            }
        }
        return map;
    }
}
