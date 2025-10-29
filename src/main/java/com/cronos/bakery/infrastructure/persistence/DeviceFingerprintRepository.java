package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.DeviceFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DeviceFingerprintRepository extends JpaRepository<DeviceFingerprint, Long> {

    @Query("SELECT df FROM DeviceFingerprint df WHERE df.user.id = :userId AND df.fingerprintHash = :fingerprintHash")
    Optional<DeviceFingerprint> findByUserIdAndFingerprintHash(@Param("userId") Long userId, @Param("fingerprintHash") String fingerprintHash);

    @Query("SELECT df FROM DeviceFingerprint df WHERE df.user.id = :userId ORDER BY df.lastSeenAt DESC")
    List<DeviceFingerprint> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT df FROM DeviceFingerprint df WHERE df.user.id = :userId AND df.isTrusted = true")
    List<DeviceFingerprint> findTrustedDevicesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(df) > 0 FROM DeviceFingerprint df WHERE df.user.id = :userId AND df.fingerprintHash = :fingerprintHash AND df.isTrusted = true")
    boolean isDeviceTrusted(@Param("userId") Long userId, @Param("fingerprintHash") String fingerprintHash);
}
