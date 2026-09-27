package com.studypod.studypod_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);

    long countByRole(String role);
    long countByLastLoginDate(LocalDate date);

    List<User> findByNameContainingIgnoreCaseOrUserIdContainingIgnoreCase(String name, String userId);
}