package com.reverse.Hack.service;

import com.reverse.Hack.dto.ProfileUpdateDTO;
import com.reverse.Hack.dto.RegisterDTO;
import com.reverse.Hack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User register(RegisterDTO dto);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User updateProfile(User user, ProfileUpdateDTO dto);

    String uploadProfilePicture(User user, MultipartFile file);

    void followUser(User follower, User target);

    void unfollowUser(User follower, User target);

    boolean isFollowing(User follower, User target);

    List<User> getFollowers(User user);

    List<User> getFollowing(User user);

    Page<User> searchUsers(String query, Pageable pageable);

    List<User> getSuggestedUsers(User currentUser);
}
