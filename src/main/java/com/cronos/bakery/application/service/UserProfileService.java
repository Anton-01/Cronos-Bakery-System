package com.cronos.bakery.application.service;

import com.cronos.bakery.domain.entity.SecurityNotification;
import com.cronos.bakery.domain.entity.UserProfile;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.SecurityNotificationRepository;
import com.cronos.bakery.infrastructure.persistence.UserProfileRepository;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileImageService imageService;
    private final SecurityNotificationRepository notificationRepository;

    /**
     * Gets user profile by user ID
     */
    public Optional<UserProfile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    /**
     * Gets or creates user profile
     */
    @Transactional
    public UserProfile getOrCreateProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));

                    UserProfile profile = UserProfile.builder()
                            .user(user)
                            .language("en")
                            .timezone("UTC")
                            .currency("USD")
                            .emailNotifications(true)
                            .smsNotifications(false)
                            .pushNotifications(true)
                            .build();

                    return profileRepository.save(profile);
                });
    }

    /**
     * Updates personal information
     */
    @Transactional
    public UserProfile updatePersonalInfo(Long userId, UserProfile updatedProfile) {
        UserProfile profile = getOrCreateProfile(userId);

        // Update personal fields
        if (updatedProfile.getDateOfBirth() != null) {
            profile.setDateOfBirth(updatedProfile.getDateOfBirth());
        }
        if (updatedProfile.getGender() != null) {
            profile.setGender(updatedProfile.getGender());
        }
        if (updatedProfile.getBio() != null) {
            profile.setBio(updatedProfile.getBio());
        }

        // Update address
        if (updatedProfile.getAddress() != null) {
            profile.setAddress(updatedProfile.getAddress());
        }
        if (updatedProfile.getCity() != null) {
            profile.setCity(updatedProfile.getCity());
        }
        if (updatedProfile.getState() != null) {
            profile.setState(updatedProfile.getState());
        }
        if (updatedProfile.getPostalCode() != null) {
            profile.setPostalCode(updatedProfile.getPostalCode());
        }
        if (updatedProfile.getCountry() != null) {
            profile.setCountry(updatedProfile.getCountry());
        }

        // Update social links
        if (updatedProfile.getLinkedinUrl() != null) {
            profile.setLinkedinUrl(updatedProfile.getLinkedinUrl());
        }
        if (updatedProfile.getTwitterUrl() != null) {
            profile.setTwitterUrl(updatedProfile.getTwitterUrl());
        }
        if (updatedProfile.getFacebookUrl() != null) {
            profile.setFacebookUrl(updatedProfile.getFacebookUrl());
        }
        if (updatedProfile.getInstagramUrl() != null) {
            profile.setInstagramUrl(updatedProfile.getInstagramUrl());
        }

        profile = profileRepository.save(profile);

        // Send notification
        sendProfileUpdatedNotification(userId);

        log.info("Updated personal info for user: {}", userId);
        return profile;
    }

    /**
     * Updates business information
     */
    @Transactional
    public UserProfile updateBusinessInfo(Long userId, UserProfile updatedProfile) {
        UserProfile profile = getOrCreateProfile(userId);

        // Update business fields
        if (updatedProfile.getBusinessName() != null) {
            profile.setBusinessName(updatedProfile.getBusinessName());
        }
        if (updatedProfile.getBusinessType() != null) {
            profile.setBusinessType(updatedProfile.getBusinessType());
        }
        if (updatedProfile.getTaxId() != null) {
            profile.setTaxId(updatedProfile.getTaxId());
        }
        if (updatedProfile.getBusinessAddress() != null) {
            profile.setBusinessAddress(updatedProfile.getBusinessAddress());
        }
        if (updatedProfile.getBusinessCity() != null) {
            profile.setBusinessCity(updatedProfile.getBusinessCity());
        }
        if (updatedProfile.getBusinessState() != null) {
            profile.setBusinessState(updatedProfile.getBusinessState());
        }
        if (updatedProfile.getBusinessPostalCode() != null) {
            profile.setBusinessPostalCode(updatedProfile.getBusinessPostalCode());
        }
        if (updatedProfile.getBusinessCountry() != null) {
            profile.setBusinessCountry(updatedProfile.getBusinessCountry());
        }
        if (updatedProfile.getBusinessPhone() != null) {
            profile.setBusinessPhone(updatedProfile.getBusinessPhone());
        }
        if (updatedProfile.getBusinessEmail() != null) {
            profile.setBusinessEmail(updatedProfile.getBusinessEmail());
        }
        if (updatedProfile.getBusinessWebsite() != null) {
            profile.setBusinessWebsite(updatedProfile.getBusinessWebsite());
        }

        profile = profileRepository.save(profile);

        // Send notification
        sendProfileUpdatedNotification(userId);

        log.info("Updated business info for user: {}", userId);
        return profile;
    }

    /**
     * Updates user preferences
     */
    @Transactional
    public UserProfile updatePreferences(Long userId, UserProfile updatedProfile) {
        UserProfile profile = getOrCreateProfile(userId);

        if (updatedProfile.getLanguage() != null) {
            profile.setLanguage(updatedProfile.getLanguage());
        }
        if (updatedProfile.getTimezone() != null) {
            profile.setTimezone(updatedProfile.getTimezone());
        }
        if (updatedProfile.getCurrency() != null) {
            profile.setCurrency(updatedProfile.getCurrency());
        }
        if (updatedProfile.getEmailNotifications() != null) {
            profile.setEmailNotifications(updatedProfile.getEmailNotifications());
        }
        if (updatedProfile.getSmsNotifications() != null) {
            profile.setSmsNotifications(updatedProfile.getSmsNotifications());
        }
        if (updatedProfile.getPushNotifications() != null) {
            profile.setPushNotifications(updatedProfile.getPushNotifications());
        }

        profile = profileRepository.save(profile);
        log.info("Updated preferences for user: {}", userId);
        return profile;
    }

    /**
     * Uploads profile picture
     */
    @Transactional
    public UserProfile uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        UserProfile profile = getOrCreateProfile(userId);

        // Delete old picture if exists
        if (profile.getProfilePictureUrl() != null) {
            try {
                imageService.deleteImage(profile.getProfilePictureUrl());
            } catch (IOException e) {
                log.warn("Failed to delete old profile picture: {}", e.getMessage());
            }
        }

        // Upload new picture
        String imageUrl = imageService.uploadProfilePicture(file, userId);
        profile.setProfilePictureUrl(imageUrl);

        profile = profileRepository.save(profile);
        log.info("Uploaded profile picture for user: {}", userId);
        return profile;
    }

    /**
     * Uploads cover picture
     */
    @Transactional
    public UserProfile uploadCoverPicture(Long userId, MultipartFile file) throws IOException {
        UserProfile profile = getOrCreateProfile(userId);

        // Delete old picture if exists
        if (profile.getCoverPictureUrl() != null) {
            try {
                imageService.deleteImage(profile.getCoverPictureUrl());
            } catch (IOException e) {
                log.warn("Failed to delete old cover picture: {}", e.getMessage());
            }
        }

        // Upload new picture
        String imageUrl = imageService.uploadCoverPicture(file, userId);
        profile.setCoverPictureUrl(imageUrl);

        profile = profileRepository.save(profile);
        log.info("Uploaded cover picture for user: {}", userId);
        return profile;
    }

    /**
     * Deletes profile picture
     */
    @Transactional
    public void deleteProfilePicture(Long userId) throws IOException {
        UserProfile profile = getOrCreateProfile(userId);

        if (profile.getProfilePictureUrl() != null) {
            imageService.deleteImage(profile.getProfilePictureUrl());
            profile.setProfilePictureUrl(null);
            profileRepository.save(profile);
            log.info("Deleted profile picture for user: {}", userId);
        }
    }

    /**
     * Deletes cover picture
     */
    @Transactional
    public void deleteCoverPicture(Long userId) throws IOException {
        UserProfile profile = getOrCreateProfile(userId);

        if (profile.getCoverPictureUrl() != null) {
            imageService.deleteImage(profile.getCoverPictureUrl());
            profile.setCoverPictureUrl(null);
            profileRepository.save(profile);
            log.info("Deleted cover picture for user: {}", userId);
        }
    }

    /**
     * Sends profile updated notification
     */
    private void sendProfileUpdatedNotification(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        SecurityNotification notification = SecurityNotification.builder()
                .user(user)
                .type(SecurityNotification.NotificationType.PROFILE_UPDATED)
                .title("Profile Updated")
                .message("Your profile information has been updated successfully.")
                .severity(SecurityNotification.NotificationSeverity.INFO)
                .build();

        notificationRepository.save(notification);
    }
}
