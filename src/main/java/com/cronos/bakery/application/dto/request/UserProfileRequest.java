package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {

    // Personal Information
    private LocalDate dateOfBirth;

    @Size(max = 10)
    private String gender;

    @Size(max = 500)
    private String bio;

    // Address Information
    @Size(max = 500)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;

    // Business Information
    @Size(max = 200)
    private String businessName;

    @Size(max = 100)
    private String businessType;

    @Size(max = 50)
    private String taxId;

    @Size(max = 500)
    private String businessAddress;

    @Size(max = 100)
    private String businessCity;

    @Size(max = 100)
    private String businessState;

    @Size(max = 20)
    private String businessPostalCode;

    @Size(max = 100)
    private String businessCountry;

    @Size(max = 20)
    private String businessPhone;

    @Email
    @Size(max = 255)
    private String businessEmail;

    @Size(max = 255)
    private String businessWebsite;

    // Social Links
    @Size(max = 255)
    private String linkedinUrl;

    @Size(max = 255)
    private String twitterUrl;

    @Size(max = 255)
    private String facebookUrl;

    @Size(max = 255)
    private String instagramUrl;

    // Preferences
    @Size(max = 10)
    private String language;

    @Size(max = 50)
    private String timezone;

    @Size(max = 10)
    private String currency;

    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
}
