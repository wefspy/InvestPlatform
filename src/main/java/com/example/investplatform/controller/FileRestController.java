package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.FileResponseDto;
import com.example.investplatform.application.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Файлы", description = "CRUD операции с файлами в MinIO")
public class FileRestController {
    private final FileStorageService fileStorageService;

    public FileRestController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Загрузить файл в хранилище")
    @ApiResponse(responseCode = "200", description = "Файл успешно загружен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = FileResponseDto.class))
    })
    @ApiResponse(responseCode = "500", description = "Ошибка загрузки файла", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> upload(@RequestParam("file") MultipartFile file) {
        FileResponseDto response = fileStorageService.upload(file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Скачать файл из хранилища")
    @ApiResponse(responseCode = "200", description = "Файл успешно получен")
    @ApiResponse(responseCode = "404", description = "Файл не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{*filePath}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String filePath, HttpServletRequest request) {
        // Remove leading slash added by {*filePath} capture
        String fileName = filePath.startsWith("/") ? filePath.substring(1) : filePath;

        FileStorageService.FileDownloadResult result = fileStorageService.download(fileName);
        InputStreamResource resource = new InputStreamResource(result.stream()) {
            @Override
            public String getDescription() {
                return "MinIO object: " + fileName;
            }
        };

        // Use only the last segment for Content-Disposition filename
        String displayName = fileName.contains("/")
                ? fileName.substring(fileName.lastIndexOf('/') + 1)
                : fileName;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .contentLength(result.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + displayName + "\"")
                .body(resource);
    }

    @Operation(summary = "Получить список всех файлов")
    @ApiResponse(responseCode = "200", description = "Список файлов получен")
    @GetMapping
    public ResponseEntity<List<FileResponseDto>> listFiles() {
        return ResponseEntity.ok(fileStorageService.listFiles());
    }

    @Operation(summary = "Удалить файл из хранилища")
    @ApiResponse(responseCode = "204", description = "Файл успешно удалён")
    @ApiResponse(responseCode = "404", description = "Файл не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @DeleteMapping("/{*filePath}")
    public ResponseEntity<Void> delete(@PathVariable String filePath) {
        String fileName = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        fileStorageService.delete(fileName);
        return ResponseEntity.noContent().build();
    }
}
