package com.example.investplatform.infrastructure.config;

import com.example.investplatform.infrastructure.config.property.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        String bucket = minioProperties.getBucket();
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("Создан бакет MinIO: {}", bucket);
        }

        return minioClient;
    }
}
