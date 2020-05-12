package com.mokhov.climbing.models;

import lombok.Data;

@Data
public class FileMatch {
    private String path;
    private float score;

    public FileMatch(String path, float score) {
        this.path = path;
        this.score = score;
    }

}
