package com.mokhov.climbing.config;

public class AppConfig {
    public static final String V_1 = "v1";
    public static final String API_ROOT_PATH = "/api/";
    public static final String API_ROOT_PATH_WITH_V_1 = API_ROOT_PATH + V_1;

    private AppConfig() {
        throw new IllegalStateException("Utility class");
    }
}
