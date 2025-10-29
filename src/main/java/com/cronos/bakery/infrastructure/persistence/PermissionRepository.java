package com.cronos.bakery.infrastructure.persistence;


import com.cronos.bakery.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    Optional<Permission> findByResourceAndAction(String resource, String action);
}
