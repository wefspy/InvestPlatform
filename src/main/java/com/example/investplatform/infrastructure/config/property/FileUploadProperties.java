package com.example.investplatform.infrastructure.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.file-upload")
public class FileUploadProperties {
    private DataSize maxSize;
    private List<String> allowedContentTypes;
    private List<String> allowedExtensions;
}
