package com.mokhov.climbing.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RequestPhotoUploadUrlResponse {
    private final String fileId;
    private final String url;
}
