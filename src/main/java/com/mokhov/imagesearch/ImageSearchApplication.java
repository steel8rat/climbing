package com.mokhov.imagesearch;

import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.ORB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class ImageSearchApplication {

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();
        System.out.println("Test01");
        SpringApplication.run(ImageSearchApplication.class, args);
        System.out.println("Test2");
        ORB detector = ORB.create();
        System.out.println("Test3");
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        System.out.println("Test4");
        Mat descriptors = new Mat();
        System.out.println("Test5");
        Mat mask = new Mat();
        System.out.println("It works1 !!!!!!!!");
        System.out.println(mask.toString());

    }

}
