package com.mokhov.climbing.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
public class DoConfig {

    @Value("${do.space.key}")
    private String doSpaceKey;

    @Value("${do.space.secret}")
    private String doSpaceSecret;

    @Value("${do.space.endpoint}")
    private String doSpaceEndpoint;

    @Value("${do.space.region}")
    private String doSpaceRegion;

    @Value("${do.space.bucket}")
    private String bucket;

    @Value("#{'${do.space.upload-formats}'.split(',')}")
    private List<String> uploadFormats;


    @Bean
    public AmazonS3 getCredentials() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(doSpaceKey, doSpaceSecret);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(doSpaceEndpoint, doSpaceRegion))
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }

}
