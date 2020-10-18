package com.cht.easygrpc.enums;

import java.util.Arrays;

/**
 * @author : chenhaitao934
 */
public enum FileType {

    UNKNOWN(0, new String[0]),
    PROPERTIES(1, new String[]{"properties"}),
    YAML(2, new String[]{"yaml", "yml"}),
    XML(3, new String[]{"xml"}),
    JSON(4, new String[]{"json"}),
    CLASS(4, new String[]{"class"});

    private int value;
    private String[] extensions;

    private FileType(int value, String[] extensions) {
        this.value = value;
        this.extensions = extensions;
    }

    public int value() {
        return this.value;
    }

    public String info() {
        return String.join(",", this.extensions);
    }

    public static FileType parse(int value) {
        FileType[] enums = values();
        FileType[] var2 = enums;
        int var3 = enums.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            FileType currentEnum = var2[var4];
            if (currentEnum.value == value) {
                return currentEnum;
            }
        }

        return UNKNOWN;
    }

    public static FileType parse(String extendsion) {
        FileType[] enums = values();
        FileType[] var2 = enums;
        int var3 = enums.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            FileType currentEnum = var2[var4];
            if (Arrays.asList(currentEnum.extensions).contains(extendsion)) {
                return currentEnum;
            }
        }

        return UNKNOWN;
    }
}
