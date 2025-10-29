package com.cronos.bakery.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    // Personal Information
    private LocalDate dateOfBirth;
    private String gender;
    private String bio;
    private String profilePictureUrl;
    private String coverPictureUrl;

    // Address Information
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Business Information
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private String businessCity;
    private String businessState;
    private String businessPostalCode;
    private String businessCountry;
    private String businessPhone;
    private String businessEmail;
    private String businessWebsite;

    // Social Links
    private String linkedinUrl;
    private String twitterUrl;
    private String facebookUrl;
    private String instagramUrl;

    // Preferences
    private String language;
    private String timezone;
    private String currency;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
