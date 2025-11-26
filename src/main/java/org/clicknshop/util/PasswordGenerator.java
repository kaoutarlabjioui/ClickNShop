package org.clicknshop.util;

import java.security.SecureRandom;

public final class PasswordGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private PasswordGenerator() {}

    public static String genNumeric6() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    public static String genAlphanumeric(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
}