package com.studypod.studypod_backend.admin;

import com.studypod.studypod_backend.admin.dto.AdminUserResponse;
import com.studypod.studypod_backend.admin.dto.RoleUpdateRequest;
import com.studypod.studypod_backend.admin.dto.StatusUpdateRequest;
import com.studypod.studypod_backend.user.User;
import com.studypod.studypod_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> listUsers(
            @RequestParam(required = false) String search
    ) {
        List<User> users = (search == null || search.isBlank())
                ? userRepository.findAll()
                : userRepository.findByNameContainingIgnoreCaseOrUserIdContainingIgnoreCase(search, search);

        return ResponseEntity.ok(users.stream().map(AdminUserResponse::from).toList());
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(
            @PathVariable String id,
            @RequestBody RoleUpdateRequest request,
            Authentication authentication
    ) {
        String actingAdminId = (String) authentication.getPrincipal();
        if (id.equals(actingAdminId)) {
            return ResponseEntity.badRequest().body("You cannot change your own role");
        }

        String role = request.getRole();
        if (!"ADMIN".equals(role) && !"STUDENT".equals(role)) {
            return ResponseEntity.badRequest().body("Role must be ADMIN or STUDENT");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(AdminUserResponse.from(user));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateRequest request,
            Authentication authentication
    ) {
        String actingAdminId = (String) authentication.getPrincipal();
        if (id.equals(actingAdminId)) {
            return ResponseEntity.badRequest().body("You cannot suspend your own account");
        }

        String status = request.getStatus();
        if (!"ACTIVE".equals(status) && !"SUSPENDED".equals(status)) {
            return ResponseEntity.badRequest().body("Status must be ACTIVE or SUSPENDED");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setStatus(status);
        userRepository.save(user);

        return ResponseEntity.ok(AdminUserResponse.from(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String id,
            Authentication authentication
    ) {
        String actingAdminId = (String) authentication.getPrincipal();
        if (id.equals(actingAdminId)) {
            return ResponseEntity.badRequest().body("You cannot delete your own account");
        }

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}