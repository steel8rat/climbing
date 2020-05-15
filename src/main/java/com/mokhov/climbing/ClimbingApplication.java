package com.mokhov.climbing;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.ORB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClimbingApplication {

    public static void main(String[] args) {
//        nu.pattern.OpenCV.loadShared();
//        System.out.println("Test4321");
        SpringApplication.run(ClimbingApplication.class, args);
//        System.out.println("Test2");
//        ORB detector = ORB.create();
//        System.out.println("Test3");
//        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
//        System.out.println("Test4");
//        Mat descriptors = new Mat();
//        System.out.println("Test5");
//        Mat mask = new Mat();
//        System.out.println("It works1 !!!!!!!!");
//        System.out.println(mask.toString());
//        System.out.println("It works again");
    }

}
