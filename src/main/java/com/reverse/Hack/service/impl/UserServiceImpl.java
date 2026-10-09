package com.reverse.Hack.service.impl;

import com.reverse.Hack.dto.ProfileUpdateDTO;
import com.reverse.Hack.dto.RegisterDTO;
import com.reverse.Hack.model.User;
import com.reverse.Hack.repository.UserRepository;
import com.reverse.Hack.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = User.builder()
                .displayName(dto.getDisplayName())
                .username(dto.getUsername().toLowerCase())
                .email(dto.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateProfile(User user, ProfileUpdateDTO dto) {
        if (dto.getDisplayName() != null && !dto.getDisplayName().isBlank()) {
            user.setDisplayName(dto.getDisplayName());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        user.setProfilePublic(dto.isProfilePublic());
        if (dto.getInterests() != null) {
            user.setInterests(dto.getInterests());
        }
        return userRepository.save(user);
    }

    @Override
    public String uploadProfilePicture(User user, MultipartFile file) {
        try {
            String uploadDir = "uploads/profiles/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String url = "/" + uploadDir + filename;
            user.setProfilePicture(url);
            userRepository.save(user);
            return url;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }

    @Override
    public void followUser(User follower, User target) {
        if (follower.getId().equals(target.getId())) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        follower.getFollowing().add(target);
        userRepository.save(follower);
    }

    @Override
    public void unfollowUser(User follower, User target) {
        follower.getFollowing().remove(target);
        userRepository.save(follower);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(User follower, User target) {
        User fresh = userRepository.findById(follower.getId()).orElse(follower);
        return fresh.getFollowing().stream().anyMatch(u -> u.getId().equals(target.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowers(User user) {
        User fresh = userRepository.findById(user.getId()).orElse(user);
        return fresh.getFollowers().stream().toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowing(User user) {
        User fresh = userRepository.findById(user.getId()).orElse(user);
        return fresh.getFollowing().stream().toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.searchUsers(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getSuggestedUsers(User currentUser) {
        return userRepository.findSuggestedUsers(currentUser.getId(), PageRequest.of(0, 5)).getContent();
    }
}
