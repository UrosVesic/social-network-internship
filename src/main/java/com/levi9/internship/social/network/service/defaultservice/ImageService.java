package com.levi9.internship.social.network.service.defaultservice;

import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {

    private final S3Template s3Template;

    private final String bucket;

    public ImageService(S3Template s3Template,
                        @Value("${spring.cloud.aws.s3.bucket-name}") String bucket) {
        this.s3Template = s3Template;
        this.bucket = bucket;
    }

    public S3Resource save(InputStream image) {
        return s3Template.upload(bucket, UUID.randomUUID() + ".jpg", image);
    }

    public InputStream download(String imageUrl) {
        try {
            URLConnection connection = new URL(imageUrl).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            return connection.getInputStream();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.ERROR_DOWNLOADING_IMAGE, e.getMessage());
        }

    }

    public String createPresignedUrl(String filename) {
        return s3Template.createSignedGetURL("java-internship", Objects.requireNonNull(filename), Duration.ofHours(1)).toString();
    }
}
