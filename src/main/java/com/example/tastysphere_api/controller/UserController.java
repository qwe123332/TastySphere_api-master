package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.service.UserService;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.dto.NotificationDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = Optional.ofNullable(userService.getUserById(id));
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile( @AuthenticationPrincipal CustomUserDetails user) {
            return ResponseEntity.ok(userService.getUserProfile(user.getUser().getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @Valid @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(userService.updateProfile(user.getUser().getId(), userDTO));
    }



    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user) {
        userService.followUser(user.getUser().getId(), userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user) {
        userService.unfollowUser(user.getUser().getId(), userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getNotifications(user.getUser().getId(), PageRequest.of(page, size)));
    }

    @PutMapping("/notifications/{notificationId}")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails user) {
        userService.markNotificationAsRead(notificationId, user.getUser().getId());
        return ResponseEntity.ok().build();
    }
}