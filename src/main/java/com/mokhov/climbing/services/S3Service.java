package com.mokhov.climbing.services;

import com.mokhov.climbing.config.DoConfig;
import com.mokhov.climbing.models.User;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final DoConfig doConfig;

    public String generatePresignedS3UploadUrl(String path) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(doConfig.getBucket(), path)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(DateUtils.addMinutes(new Date(), 5));
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toExternalForm();
    }

    public void deleteFile(String path) {
        amazonS3.deleteObject(new DeleteObjectRequest(doConfig.getBucket(), path));
    }

    public boolean doesObjectExist(String objectKey) {
        return amazonS3.doesObjectExist(doConfig.getBucket(), objectKey);
    }

    public String getSubdomainEndpointURL(String path) {
        return String.format("%s/%s", doConfig.getSubdomainEndpointWithProtocol(), path);
    }

    public String getUserPhotoKey(User user, String fileName) {
        return String.format("users/%s/%s", user.getId(), fileName);
    }

    public void setPublicAccess(String path) {
        amazonS3.setObjectAcl(doConfig.getBucket(), path, CannedAccessControlList.PublicRead);
    }

}
