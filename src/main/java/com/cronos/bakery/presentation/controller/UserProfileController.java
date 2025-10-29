package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.request.UserProfileRequest;
import com.cronos.bakery.application.dto.response.UserProfileResponse;
import com.cronos.bakery.application.service.UserProfileService;
import com.cronos.bakery.domain.entity.UserProfile;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserProfileController {

    private final UserProfileService profileService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get user profile", description = "Gets the authenticated user's profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        UserProfile profile = profileService.getOrCreateProfile(user.getId());
        return ResponseEntity.ok(mapToResponse(profile, user));
    }

    @PutMapping("/personal")
    @Operation(summary = "Update personal information", description = "Updates user's personal information")
    public ResponseEntity<UserProfileResponse> updatePersonalInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileRequest request) {

        User user = getUserFromDetails(userDetails);
        UserProfile updatedProfile = mapToEntity(request);
        UserProfile profile = profileService.updatePersonalInfo(user.getId(), updatedProfile);

        return ResponseEntity.ok(mapToResponse(profile, user));
    }

    @PutMapping("/business")
    @Operation(summary = "Update business information", description = "Updates user's business information")
    public ResponseEntity<UserProfileResponse> updateBusinessInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileRequest request) {

        User user = getUserFromDetails(userDetails);
        UserProfile updatedProfile = mapToEntity(request);
        UserProfile profile = profileService.updateBusinessInfo(user.getId(), updatedProfile);

        return ResponseEntity.ok(mapToResponse(profile, user));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update preferences", description = "Updates user's preferences")
    public ResponseEntity<UserProfileResponse> updatePreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileRequest request) {

        User user = getUserFromDetails(userDetails);
        UserProfile updatedProfile = mapToEntity(request);
        UserProfile profile = profileService.updatePreferences(user.getId(), updatedProfile);

        return ResponseEntity.ok(mapToResponse(profile, user));
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile picture", description = "Uploads user's profile picture")
    public ResponseEntity<UserProfileResponse> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        try {
            User user = getUserFromDetails(userDetails);
            UserProfile profile = profileService.uploadProfilePicture(user.getId(), file);
            return ResponseEntity.ok(mapToResponse(profile, user));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload cover picture", description = "Uploads user's cover picture")
    public ResponseEntity<UserProfileResponse> uploadCoverPicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        try {
            User user = getUserFromDetails(userDetails);
            UserProfile profile = profileService.uploadCoverPicture(user.getId(), file);
            return ResponseEntity.ok(mapToResponse(profile, user));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/picture")
    @Operation(summary = "Delete profile picture", description = "Deletes user's profile picture")
    public ResponseEntity<Void> deleteProfilePicture(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = getUserFromDetails(userDetails);
            profileService.deleteProfilePicture(user.getId());
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/cover")
    @Operation(summary = "Delete cover picture", description = "Deletes user's cover picture")
    public ResponseEntity<Void> deleteCoverPicture(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = getUserFromDetails(userDetails);
            profileService.deleteCoverPicture(user.getId());
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserProfile mapToEntity(UserProfileRequest request) {
        return UserProfile.builder()
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bio(request.getBio())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .businessName(request.getBusinessName())
                .businessType(request.getBusinessType())
                .taxId(request.getTaxId())
                .businessAddress(request.getBusinessAddress())
                .businessCity(request.getBusinessCity())
                .businessState(request.getBusinessState())
                .businessPostalCode(request.getBusinessPostalCode())
                .businessCountry(request.getBusinessCountry())
                .businessPhone(request.getBusinessPhone())
                .businessEmail(request.getBusinessEmail())
                .businessWebsite(request.getBusinessWebsite())
                .linkedinUrl(request.getLinkedinUrl())
                .twitterUrl(request.getTwitterUrl())
                .facebookUrl(request.getFacebookUrl())
                .instagramUrl(request.getInstagramUrl())
                .language(request.getLanguage())
                .timezone(request.getTimezone())
                .currency(request.getCurrency())
                .emailNotifications(request.getEmailNotifications())
                .smsNotifications(request.getSmsNotifications())
                .pushNotifications(request.getPushNotifications())
                .build();
    }

    private UserProfileResponse mapToResponse(UserProfile profile, User user) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .bio(profile.getBio())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .coverPictureUrl(profile.getCoverPictureUrl())
                .address(profile.getAddress())
                .city(profile.getCity())
                .state(profile.getState())
                .postalCode(profile.getPostalCode())
                .country(profile.getCountry())
                .businessName(profile.getBusinessName())
                .businessType(profile.getBusinessType())
                .taxId(profile.getTaxId())
                .businessAddress(profile.getBusinessAddress())
                .businessCity(profile.getBusinessCity())
                .businessState(profile.getBusinessState())
                .businessPostalCode(profile.getBusinessPostalCode())
                .businessCountry(profile.getBusinessCountry())
                .businessPhone(profile.getBusinessPhone())
                .businessEmail(profile.getBusinessEmail())
                .businessWebsite(profile.getBusinessWebsite())
                .linkedinUrl(profile.getLinkedinUrl())
                .twitterUrl(profile.getTwitterUrl())
                .facebookUrl(profile.getFacebookUrl())
                .instagramUrl(profile.getInstagramUrl())
                .language(profile.getLanguage())
                .timezone(profile.getTimezone())
                .currency(profile.getCurrency())
                .emailNotifications(profile.getEmailNotifications())
                .smsNotifications(profile.getSmsNotifications())
                .pushNotifications(profile.getPushNotifications())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
