package com.cronos.bakery.domain.entity;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Personal Information
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender;

    @Column(length = 500)
    private String bio;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "cover_picture_url", length = 500)
    private String coverPictureUrl;

    // Address Information
    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    // Business Information
    @Column(name = "business_name", length = 200)
    private String businessName;

    @Column(name = "business_type", length = 100)
    private String businessType;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "business_address", length = 500)
    private String businessAddress;

    @Column(name = "business_city", length = 100)
    private String businessCity;

    @Column(name = "business_state", length = 100)
    private String businessState;

    @Column(name = "business_postal_code", length = 20)
    private String businessPostalCode;

    @Column(name = "business_country", length = 100)
    private String businessCountry;

    @Column(name = "business_phone", length = 20)
    private String businessPhone;

    @Column(name = "business_email", length = 255)
    private String businessEmail;

    @Column(name = "business_website", length = 255)
    private String businessWebsite;

    // Social Links
    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Column(name = "twitter_url", length = 255)
    private String twitterUrl;

    @Column(name = "facebook_url", length = 255)
    private String facebookUrl;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;

    // Preferences
    @Column(length = 10)
    private String language;

    @Column(length = 50)
    private String timezone;

    @Column(length = 10)
    private String currency;

    @Builder.Default
    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;

    @Builder.Default
    @Column(name = "sms_notifications", nullable = false)
    private Boolean smsNotifications = false;

    @Builder.Default
    @Column(name = "push_notifications", nullable = false)
    private Boolean pushNotifications = true;
}
