package com.studypod.studypod_backend.user;

import com.studypod.studypod_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            return ResponseEntity.badRequest().body("User ID already taken");
        }

        User user = new User();
        user.setName(request.getName());
        user.setUserId(request.getUserId());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Registering counts as day 1 of the login streak.
        user.setLastLoginDate(LocalDate.now());
        user.setLoginStreak(1);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUserId(), user.getRole());

        AuthResponse response = new AuthResponse(
                token, user.getId(), user.getName(), user.getUserId(), user.getAvatarIndex()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid user ID or password");
        }

        if ("SUSPENDED".equals(user.getStatus())) {
            return ResponseEntity.status(403).body("This account has been suspended. Contact an administrator.");
        }

        updateLoginStreak(user);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUserId(), user.getRole());

        AuthResponse response = new AuthResponse(
                token, user.getId(), user.getName(), user.getUserId(), user.getAvatarIndex()
        );
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // ADMIN LOGIN
    // Same credential check as /auth/login, but rejects any account
    // whose role isn't ADMIN.
    // =========================================================

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid user ID or password");
        }

        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("This account does not have admin access");
        }

        if ("SUSPENDED".equals(user.getStatus())) {
            return ResponseEntity.status(403).body("This admin account has been suspended.");
        }

        updateLoginStreak(user);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUserId(), user.getRole());

        AuthResponse response = new AuthResponse(
                token, user.getId(), user.getName(), user.getUserId(), user.getAvatarIndex()
        );
        return ResponseEntity.ok(response);
    }
    // =========================================================
    // LOGIN STREAK
    // =========================================================

    /*
     * Rules:
     *
     * - Never logged in before        -> streak = 1
     * - Last login was today already  -> streak unchanged
     *   (logging in twice in one day doesn't count twice)
     * - Last login was yesterday      -> streak + 1
     * - Last login was earlier than
     *   yesterday (a gap)             -> streak resets to 1
     */
    private void updateLoginStreak(User user) {

        LocalDate today = LocalDate.now();
        LocalDate lastLogin = user.getLastLoginDate();

        if (lastLogin == null) {
            user.setLoginStreak(1);
        } else if (lastLogin.equals(today)) {
            // Already logged in today — leave streak as is.
        } else if (lastLogin.equals(today.minusDays(1))) {
            user.setLoginStreak(user.getLoginStreak() + 1);
        } else {
            user.setLoginStreak(1);
        }

        user.setLastLoginDate(today);
    }

    // =========================================================
    // UPDATE AVATAR
    // =========================================================

    @PutMapping("/avatar")
    public ResponseEntity<?> updateAvatar(
            @RequestBody Map<String, Integer> body,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        Integer avatarIndex = body.get("avatarIndex");
        if (avatarIndex == null || avatarIndex < 0) {
            return ResponseEntity.badRequest().body("Invalid avatarIndex");
        }

        // `userId` here is the authenticated principal, i.e. the internal
        // primary key (users.id), since that's what the JWT subject holds —
        // not the human-chosen login handle. Use findById, not findByUserId.
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setAvatarIndex(avatarIndex);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("avatarIndex", user.getAvatarIndex()));
    }
}