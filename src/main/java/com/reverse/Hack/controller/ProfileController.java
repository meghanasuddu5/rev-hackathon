package com.reverse.Hack.controller;

import com.reverse.Hack.dto.ProfileUpdateDTO;
import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.enums.NotificationType;
import com.reverse.Hack.model.User;
import com.reverse.Hack.service.*;
import com.reverse.Hack.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final AuthUtil authUtil;
    private final UserService userService;
    private final PostService postService;
    private final NotificationService notificationService;

    public ProfileController(AuthUtil authUtil, UserService userService,
                             PostService postService, NotificationService notificationService) {
        this.authUtil = authUtil;
        this.userService = userService;
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username, Model model) {
        User profileUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User currentUser = authUtil.getCurrentUserOrThrow();
        boolean isOwner = currentUser.getId().equals(profileUser.getId());
        boolean isFollowing = userService.isFollowing(currentUser, profileUser);
        boolean canView = profileUser.isProfilePublic() || isOwner || isFollowing;

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("canView", canView);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        if (canView) {
            model.addAttribute("posts", postService.getPostsByUser(profileUser, PageRequest.of(0, 10)));
            model.addAttribute("followerCount", userService.getFollowers(profileUser).size());
            model.addAttribute("followingCount", userService.getFollowing(profileUser).size());
        }
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(Model model) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        ProfileUpdateDTO dto = new ProfileUpdateDTO();
        dto.setDisplayName(currentUser.getDisplayName());
        dto.setBio(currentUser.getBio());
        dto.setProfilePublic(currentUser.isProfilePublic());
        dto.setInterests(currentUser.getInterests());
        model.addAttribute("profileUpdateDTO", dto);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allInterests", InterestCategory.values());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateDTO") ProfileUpdateDTO dto,
                                BindingResult result,
                                @RequestParam(value = "profilePicFile", required = false) MultipartFile profilePic,
                                RedirectAttributes redirectAttributes, Model model) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        if (result.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("allInterests", InterestCategory.values());
            return "profile-edit";
        }
        userService.updateProfile(currentUser, dto);
        if (profilePic != null && !profilePic.isEmpty()) {
            userService.uploadProfilePicture(currentUser, profilePic);
        }
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/profile/" + currentUser.getUsername();
    }

    @PostMapping("/follow/{username}")
    public String follow(@PathVariable String username,
                         @RequestHeader(value = "Referer", defaultValue = "/feed") String referer,
                         RedirectAttributes redirectAttributes) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        User target = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        try {
            userService.followUser(currentUser, target);
            notificationService.send(target, currentUser, NotificationType.NEW_FOLLOWER,
                currentUser.getDisplayName() + " started following you.",
                "/profile/" + currentUser.getUsername());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + referer;
    }

    @PostMapping("/unfollow/{username}")
    public String unfollow(@PathVariable String username,
                           @RequestHeader(value = "Referer", defaultValue = "/feed") String referer) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        userService.findByUsername(username).ifPresent(target ->
            userService.unfollowUser(currentUser, target));
        return "redirect:" + referer;
    }
}
