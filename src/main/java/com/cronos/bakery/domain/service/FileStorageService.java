package com.cronos.bakery.domain.service;

import com.cronos.bakery.application.service.enums.FileType;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.cronos.bakery.domain.entity.recipes.RecipeFile;
import com.cronos.bakery.infrastructure.persistence.RecipeFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${app.file-storage.base-path:uploads}")
    private String basePath;

    @Value("${app.file-storage.max-image-width:1920}")
    private int maxImageWidth;

    @Value("${app.file-storage.max-image-height:1080}")
    private int maxImageHeight;

    @Value("${app.file-storage.thumbnail-size:300}")
    private int thumbnailSize;

    private final RecipeFileRepository recipeFileRepository;

    /**
     * Stores a file for a recipe
     */
    @Transactional
    public RecipeFile storeFile(MultipartFile file, Recipe recipe, boolean isPrimary) throws IOException {

        validateFile(file);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "." + extension;

        // Determine file type
        FileType fileType = determineFileType(file.getContentType());

        // Create directory structure
        Path recipePath = createRecipeDirectory(recipe);
        Path filePath = recipePath.resolve(filename);

        // Process and save file
        if (fileType == FileType.IMAGE) {
            processAndSaveImage(file, filePath);
        } else {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Create RecipeFile entity
        RecipeFile recipeFile = RecipeFile.builder()
                .recipe(recipe)
                .fileName(filename)
                .originalFileName(originalFilename)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .fileType(fileType)
                .mimeType(file.getContentType())
                .isPrimary(isPrimary)
                .build();

        // Create thumbnail for images
        if (fileType == FileType.IMAGE) {
            String thumbnailPath = createThumbnail(filePath, recipePath);
            recipeFile.setThumbnailPath(thumbnailPath);
        }

        return recipeFileRepository.save(recipeFile);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (!isAllowedFileType(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }

        // Max 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds maximum allowed (10MB)");
        }
    }

    private boolean isAllowedFileType(String contentType) {
        return contentType != null && (
                contentType.startsWith("image/") ||
                        contentType.equals("application/pdf")
        );
    }

    private FileType determineFileType(String contentType) {
        if (contentType == null) {
            return FileType.OTHER;
        }

        if (contentType.startsWith("image/")) {
            return FileType.IMAGE;
        } else if (contentType.equals("application/pdf")) {
            return FileType.PDF;
        }

        return FileType.OTHER;
    }

    private Path createRecipeDirectory(Recipe recipe) throws IOException {
        Path userPath = Paths.get(basePath, "users", recipe.getUser().getId().toString());
        Path recipePath = userPath.resolve("recipes").resolve(recipe.getId().toString());

        Files.createDirectories(recipePath);

        return recipePath;
    }

    private void processAndSaveImage(MultipartFile file, Path destination) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        if (originalImage == null) {
            throw new IOException("Invalid image file");
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Resize if necessary
        if (width > maxImageWidth || height > maxImageHeight) {
            Thumbnails.of(originalImage)
                    .size(maxImageWidth, maxImageHeight)
                    .toFile(destination.toFile());
        } else {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String createThumbnail(Path originalPath, Path recipePath) throws IOException {
        String thumbnailFilename = "thumb_" + originalPath.getFileName().toString();
        Path thumbnailPath = recipePath.resolve(thumbnailFilename);

        Thumbnails.of(originalPath.toFile())
                .size(thumbnailSize, thumbnailSize)
                .toFile(thumbnailPath.toFile());

        return thumbnailPath.toString();
    }

    /**
     * Deletes a file
     */
    @Transactional
    public void deleteFile(Long fileId) throws IOException {
        RecipeFile recipeFile = recipeFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Delete physical files
        Files.deleteIfExists(Paths.get(recipeFile.getFilePath()));

        if (recipeFile.getThumbnailPath() != null) {
            Files.deleteIfExists(Paths.get(recipeFile.getThumbnailPath()));
        }

        // Delete database record
        recipeFileRepository.delete(recipeFile);
    }

    /**
     * Gets file as byte array
     */
    public byte[] getFileContent(Long fileId) throws IOException {
        RecipeFile recipeFile = recipeFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        return Files.readAllBytes(Paths.get(recipeFile.getFilePath()));
    }
}
