package com.mokhov.imagesearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageSearchApplication {

    public static void main(String[] args) {
        //System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        SpringApplication.run(ImageSearchApplication.class, args);
    }

}
