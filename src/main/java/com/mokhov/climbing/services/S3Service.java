package com.mokhov.climbing.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.mokhov.climbing.config.DoConfig;
import com.mokhov.climbing.models.Gym;
import com.mokhov.climbing.models.RequestPhotoUploadUrlResponse;
import com.mokhov.climbing.models.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final DoConfig doConfig;
    private final DateTime dateTime;
    private final UuidService uuidService;

    public String generatePresignedUploadUrl(String path) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(doConfig.getBucket(), path)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(DateUtils.addMinutes(dateTime.getDate(), 5));
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toExternalForm();
    }

    public void deleteFile(String path) {
        amazonS3.deleteObject(new DeleteObjectRequest(doConfig.getBucket(), path));
    }

    public boolean doesObjectExist(String objectKey) {
        return amazonS3.doesObjectExist(doConfig.getBucket(), objectKey);
    }

    public String getUserPhotoKey(User user, String fileName) {
        return String.format("users/%s/%s", user.getId(), fileName);
    }

    public String getRoutePhotoKey(Gym gym, String fileName) {
        return String.format("gyms/%s/%s", gym.getYelpId(), fileName);
    }

    public void setPublicAccess(String path) {
        amazonS3.setObjectAcl(doConfig.getBucket(), path, CannedAccessControlList.PublicRead);
    }

    public RequestPhotoUploadUrlResponse generateUploadUrlResponse(@NonNull String path, @NonNull String fileExtension) {
        String fileId = uuidService.generateUuid();
        String objectKey = String.format("%s/%s%s", path, fileId, fileExtension);
        RequestPhotoUploadUrlResponse response = new RequestPhotoUploadUrlResponse(fileId, objectKey);
        response.setUrl(generatePresignedUploadUrl(objectKey));
        return response;
    }

}
