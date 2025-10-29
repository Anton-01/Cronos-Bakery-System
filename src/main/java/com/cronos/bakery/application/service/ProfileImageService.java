package com.cronos.bakery.application.service;

import com.cronos.bakery.domain.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageService {

    private final FileStorageService fileStorageService;

    @Value("${app.upload.profile-pictures:uploads/profile-pictures}")
    private String profilePicturesDir;

    @Value("${app.upload.cover-pictures:uploads/cover-pictures}")
    private String coverPicturesDir;

    @Value("${app.upload.max-profile-picture-size:5242880}") // 5MB
    private long maxProfilePictureSize;

    @Value("${app.upload.max-cover-picture-size:10485760}") // 10MB
    private long maxCoverPictureSize;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final int PROFILE_PICTURE_WIDTH = 400;
    private static final int PROFILE_PICTURE_HEIGHT = 400;
    private static final int COVER_PICTURE_WIDTH = 1200;
    private static final int COVER_PICTURE_HEIGHT = 400;

    /**
     * Uploads and processes a profile picture
     */
    public String uploadProfilePicture(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file, maxProfilePictureSize);

        // Process and resize image
        byte[] processedImage = processImage(file, PROFILE_PICTURE_WIDTH, PROFILE_PICTURE_HEIGHT, true);

        // Generate unique filename
        String filename = generateFilename(file.getOriginalFilename());
        String filePath = profilePicturesDir + "/" + userId + "/" + filename;

        // Save file
        Path targetPath = Paths.get(filePath);
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, processedImage);

        log.info("Profile picture uploaded successfully for user: {} at path: {}", userId, filePath);
        return filePath;
    }

    /**
     * Uploads and processes a cover picture
     */
    public String uploadCoverPicture(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file, maxCoverPictureSize);

        // Process and resize image
        byte[] processedImage = processImage(file, COVER_PICTURE_WIDTH, COVER_PICTURE_HEIGHT, false);

        // Generate unique filename
        String filename = generateFilename(file.getOriginalFilename());
        String filePath = coverPicturesDir + "/" + userId + "/" + filename;

        // Save file
        Path targetPath = Paths.get(filePath);
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, processedImage);

        log.info("Cover picture uploaded successfully for user: {} at path: {}", userId, filePath);
        return filePath;
    }

    /**
     * Deletes a profile or cover picture
     */
    public void deleteImage(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("Image deleted successfully: {}", filePath);
        }
    }

    /**
     * Validates image file
     */
    private void validateImageFile(MultipartFile file, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size: " + maxSize + " bytes");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: " + ALLOWED_CONTENT_TYPES);
        }
    }

    /**
     * Processes and resizes an image
     */
    private byte[] processImage(MultipartFile file, int width, int height, boolean crop) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image file");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (crop) {
            // Crop to square for profile pictures
            Thumbnails.of(originalImage)
                    .size(width, height)
                    .crop(net.coobird.thumbnailator.geometry.Positions.CENTER)
                    .outputFormat("jpg")
                    .outputQuality(0.9)
                    .toOutputStream(outputStream);
        } else {
            // Resize maintaining aspect ratio for cover pictures
            Thumbnails.of(originalImage)
                    .size(width, height)
                    .outputFormat("jpg")
                    .outputQuality(0.9)
                    .toOutputStream(outputStream);
        }

        return outputStream.toByteArray();
    }

    /**
     * Generates a unique filename
     */
    private String generateFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}
