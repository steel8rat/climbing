package com.mokhov.climbing.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestPhotoUploadUrlResponse {
    private String fileId;
    private String url;
}
