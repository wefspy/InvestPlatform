package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.FileResponseDto;
import com.example.investplatform.application.exception.FileNotFoundException;
import com.example.investplatform.application.exception.FileStorageException;
import com.example.investplatform.infrastructure.config.property.MinioProperties;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public FileStorageService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public FileResponseDto upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return new FileResponseDto(fileName, file.getContentType(), file.getSize());
        } catch (Exception e) {
            throw new FileStorageException("Ошибка загрузки файла: " + fileName, e);
        }
    }

    public String upload(MultipartFile file, String objectKey) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return objectKey;
        } catch (Exception e) {
            throw new FileStorageException("Ошибка загрузки файла: " + objectKey, e);
        }
    }

    public FileDownloadResult download(String fileName) {
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .build());

            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .build());

            return new FileDownloadResult(stream, stat.contentType(), stat.size());
        } catch (io.minio.errors.ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new FileNotFoundException("Файл не найден: " + fileName);
            }
            throw new FileStorageException("Ошибка скачивания файла: " + fileName, e);
        } catch (Exception e) {
            throw new FileStorageException("Ошибка скачивания файла: " + fileName, e);
        }
    }

    public record FileDownloadResult(InputStream stream, String contentType, long size) {
    }

    public List<FileResponseDto> listFiles() {
        List<FileResponseDto> files = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());

            for (Result<Item> result : results) {
                Item item = result.get();
                files.add(new FileResponseDto(item.objectName(), null, item.size()));
            }
            return files;
        } catch (Exception e) {
            throw new FileStorageException("Ошибка получения списка файлов", e);
        }
    }

    public void delete(String fileName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .build());

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .build());
        } catch (io.minio.errors.ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new FileNotFoundException("Файл не найден: " + fileName);
            }
            throw new FileStorageException("Ошибка удаления файла: " + fileName, e);
        } catch (Exception e) {
            throw new FileStorageException("Ошибка удаления файла: " + fileName, e);
        }
    }
}
