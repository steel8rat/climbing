package com.mokhov.climbing;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClimbingApplication {
    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();
        System.out.println(Core.VERSION);
        System.out.println(Core.VERSION_MAJOR);
        System.out.println(Core.VERSION_REVISION);
        System.out.println(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.getBuildInformation());
        SpringApplication.run(ClimbingApplication.class, args);
    }
}
