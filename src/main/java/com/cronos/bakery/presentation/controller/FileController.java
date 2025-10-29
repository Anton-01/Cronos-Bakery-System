package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.FileUploadResponse;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.cronos.bakery.domain.entity.recipes.RecipeFile;
import com.cronos.bakery.domain.service.FileStorageService;
import com.cronos.bakery.infrastructure.persistence.RecipeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Files", description = "File management endpoints")
public class FileController {

    private final FileStorageService fileStorageService;
    private final RecipeRepository recipeRepository;

    @PostMapping("/recipes/{recipeId}")
    @Operation(summary = "Upload file to recipe")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(@PathVariable Long recipeId, @RequestParam("file") MultipartFile file, @RequestParam(required = false, defaultValue = "false") boolean isPrimary, Authentication authentication) {

        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("Recipe not found"));

            // Validate ownership
            if (!recipe.getUser().getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            RecipeFile recipeFile = fileStorageService.storeFile(file, recipe, isPrimary);

            FileUploadResponse response = FileUploadResponse.builder()
                    .fileId(recipeFile.getId())
                    .fileName(recipeFile.getFileName())
                    .fileUrl("/api/files/" + recipeFile.getId())
                    .thumbnailUrl(recipeFile.getThumbnailPath() != null ?
                            "/api/files/" + recipeFile.getId() + "/thumbnail" : null)
                    .fileSize(recipeFile.getFileSize())
                    .fileType(recipeFile.getFileType().name())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("File uploaded successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "Download file")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) {
        try {
            byte[] data = fileStorageService.getFileContent(fileId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                    .body(new ByteArrayResource(data));

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long fileId, Authentication authentication) {
        try {
            fileStorageService.deleteFile(fileId);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete file: " + e.getMessage()));
        }
    }
}
