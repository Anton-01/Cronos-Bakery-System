package com.cronos.bakery.application.dto.response;

import lombok.*;

@Data
@Builder
public class FileUploadResponse {
    private Long fileId;
    private String fileName;
    private String fileUrl;
    private String thumbnailUrl;
    private Long fileSize;
    private String fileType;
}
