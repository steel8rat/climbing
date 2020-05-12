package com.mokhov.climbing.utils;

public class StringUtils {

    public static String getNicknameFromEmail(String email) {
        if (email == null) return null;
        int i = email.indexOf("@");
        if (i != -1) {
            return email.substring(0, i);
        }
        return email;
    }

    public static boolean nullOrEmpty(String str){
        return str == null || str.length() == 0;
    }
}
