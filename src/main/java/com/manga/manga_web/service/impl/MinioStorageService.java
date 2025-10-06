package com.manga.manga_web.service.impl;

import com.manga.manga_web.service.StorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinioStorageService implements StorageService {
    final MinioClient minioClient;

    @Value("${minio.bucket}")
    String bucket;

    @Override
    public String upload(String objectName, InputStream data, long size, String contentType) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created bucket: {}", bucket);
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(data, size, -1)
                            .contentType(contentType)
                            .build());
            log.info("Uploaded file to MinIO: {}/{}", bucket, objectName);
            return String.format("%s/%s", bucket, objectName);
        } catch (Exception e) {
            log.warn("MinIO upload failed for {}: {}", objectName, e.getMessage());
            return null;
        }
    }
}
