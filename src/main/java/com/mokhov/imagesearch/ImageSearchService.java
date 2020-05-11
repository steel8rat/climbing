package com.mokhov.imagesearch;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.ORB;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ImageSearchService {

    @PostConstruct
    public void init() {
        nu.pattern.OpenCV.loadShared();
        ORB detector = ORB.create();
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Mat descriptors = new Mat();
        Mat mask = new Mat();
    }
}
