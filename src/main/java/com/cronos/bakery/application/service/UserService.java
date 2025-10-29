package com.cronos.bakary.application.service;

import com.cronos.bakary.application.dto.*;
import com.cronos.bakary.domain.entity.Role;
import com.cronos.bakary.domain.entity.User;
import com.cronos.bakary.domain.service.PasswordValidationService;
import com.cronos.bakary.domain.service.TwoFactorAuthenticationService;
import com.cronos.bakary.infrastructure.exception.*;
import com.cronos.bakary.infrastructure.persistence.RoleRepository;
import com.cronos.bakary.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidationService passwordValidationService;
    private final TwoFactorAuthenticationService twoFactorService;
    private final UserMapper userMapper;

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse createUser(CreateUserRequest request) {
        // Validar que el usuario no exista
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Validar contraseña
        List<String> passwordErrors = passwordValidationService.validatePassword(request.getPassword());
        if (!passwordErrors.isEmpty()) {
            throw new ValidationException("Password validation failed", passwordErrors);
        }

        // Obtener roles
        Set<Role> roles = getRolesByNames(request.getRoles());
        if (roles.isEmpty()) {
            throw new ValidationException("At least one role must be specified");
        }

        // Crear usuario
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .twoFactorEnabled(false)
                .failedLoginAttempts(0)
                .passwordChangedAt(LocalDateTime.now())
                .roles(roles)
                .build();

        user = userRepository.save(user);

        // Guardar historial de contraseña
        passwordValidationService.savePasswordHistory(user, user.getPassword());

        log.info("User created successfully: {}", user.getUsername());

        return userMapper.toUserResponse(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validar username único
        if (request.getUsername() != null &&
                !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        // Validar email único
        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Actualizar campos opcionales
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        // Actualizar roles si se proporcionan
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> newRoles = getRolesByNames(request.getRoles());
            user.getRoles().clear();
            user.getRoles().addAll(newRoles);
        }

        // Actualizar estado de cuenta
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        user = userRepository.save(user);

        log.info("User updated successfully: {}", user.getUsername());

        return userMapper.toUserResponse(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        // Validar nueva contraseña
        List<String> passwordErrors = passwordValidationService
                .validatePassword(request.getNewPassword());
        if (!passwordErrors.isEmpty()) {
            throw new ValidationException("Password validation failed", passwordErrors);
        }

        // Verificar que no sea una contraseña reutilizada
        if (passwordValidationService.isPasswordReused(user, request.getNewPassword())) {
            throw new ValidationException("Password has been used recently. Choose a different password.");
        }

        // Actualizar contraseña
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        user.setPasswordChangedAt(LocalDateTime.now());

        userRepository.save(user);

        // Guardar en historial
        passwordValidationService.savePasswordHistory(user, encodedPassword);

        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#userId")
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public TwoFactorSetupResponse setupTwoFactor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            throw new ValidationException("Two-factor authentication is already enabled");
        }

        String secret = twoFactorService.generateSecretKey();
        String qrCodeUrl = twoFactorService.generateQRCodeUrl(user, secret);

        // Guardar temporalmente (se confirmará al verificar el código)
        user.setTwoFactorSecret(secret);
        userRepository.save(user);

        return TwoFactorSetupResponse.builder()
                .secret(secret)
                .qrCodeUrl(qrCodeUrl)
                .message("Scan the QR code with your authenticator app")
                .build();
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void enableTwoFactor(Long userId, VerifyTwoFactorRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getTwoFactorSecret() == null) {
            throw new ValidationException("Two-factor setup not initiated");
        }

        if (!twoFactorService.validateCode(user.getTwoFactorSecret(), request.getCode())) {
            throw new ValidationException("Invalid verification code");
        }

        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        log.info("Two-factor authentication enabled for user: {}", user.getUsername());
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void disableTwoFactor(Long userId, VerifyTwoFactorRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.FALSE.equals(user.getTwoFactorEnabled())) {
            throw new ValidationException("Two-factor authentication is not enabled");
        }

        if (!twoFactorService.isCodeValid(user, request.getCode())) {
            throw new ValidationException("Invalid verification code");
        }

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);

        log.info("Two-factor authentication disabled for user: {}", user.getUsername());
    }

    private Set<Role> getRolesByNames(Set<String> roleNames) {
        Set<Role> roles = roleRepository.findByNameIn(roleNames);

        if (roles.size() != roleNames.size()) {
            Set<String> foundNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            Set<String> notFound = roleNames.stream()
                    .filter(name -> !foundNames.contains(name))
                    .collect(Collectors.toSet());
            throw new ResourceNotFoundException("Roles not found: " + notFound);
        }

        return roles;
    }
}
