package com.mokhov.climbing.utils;

public class Utils {

    public static String getNicknameFromEmail(String email) {
        if (email == null) return null;
        int i = email.indexOf('@');
        if (i != -1) {
            return email.substring(0, i);
        }
        return email;
    }

    private Utils() {
        throw new IllegalStateException("Utility class");
    }
}
