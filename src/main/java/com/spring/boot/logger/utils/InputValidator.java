package com.spring.boot.logger.utils;

public class InputValidator {

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    public static boolean isNull(String str) {
        return str == null;
    }

    public static boolean isBlankWithNull(String str) {
        return str == null || str.isBlank();
    }

    public static boolean isEmptyArray(Object[] objects) {
        return objects.length == 0;
    }
}
