package com.studypod.studypod_backend.admin;

import com.studypod.studypod_backend.user.User;
import com.studypod.studypod_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.countByRole("ADMIN") > 0;
        if (adminExists) {
            return;
        }

        if (userRepository.existsByUserId("admin")) {
            System.out.println("StudyPod: userId \"admin\" is already taken by a non-admin account. " +
                    "Promote an existing user to ADMIN manually.");
            return;
        }

        User admin = new User();
        admin.setName("Administrator");
        admin.setUserId("admin");
        admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");
        admin.setLastLoginDate(LocalDate.now());
        admin.setLoginStreak(1);

        userRepository.save(admin);

        System.out.println("=========================================================");
        System.out.println("  StudyPod: created default admin account");
        System.out.println("  userId:   admin");
        System.out.println("  password: Admin@123");
        System.out.println("  Please log in and change this as soon as possible.");
        System.out.println("=========================================================");
    }
}