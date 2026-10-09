package com.reverse.Hack.controller;

import com.reverse.Hack.dto.PostDTO;
import com.reverse.Hack.enums.PostAudience;
import com.reverse.Hack.model.*;
import com.reverse.Hack.service.*;
import com.reverse.Hack.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final AuthUtil authUtil;
    private final PostService postService;
    private final UserService userService;
    private final CommunityService communityService;
    private final CircleService circleService;
    private final NotificationService notificationService;
    private final CommentService commentService;

    public HomeController(AuthUtil authUtil, PostService postService, UserService userService,
                          CommunityService communityService, CircleService circleService,
                          NotificationService notificationService, CommentService commentService) {
        this.authUtil = authUtil;
        this.postService = postService;
        this.userService = userService;
        this.communityService = communityService;
        this.circleService = circleService;
        this.notificationService = notificationService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public String root() {
        return authUtil.getCurrentUser().isPresent() ? "redirect:/feed" : "redirect:/landing";
    }

    @GetMapping("/landing")
    public String landing() {
        return authUtil.getCurrentUser().isPresent() ? "redirect:/feed" : "landing";
    }

    @GetMapping("/feed")
    public String feed(@RequestParam(defaultValue = "0") int page, Model model) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Post> posts = postService.getFeedForUser(currentUser, pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("postDTO", new PostDTO());
        model.addAttribute("audiences", PostAudience.values());
        model.addAttribute("circles", circleService.getCirclesForUser(currentUser));
        model.addAttribute("suggestedUsers", userService.getSuggestedUsers(currentUser));
        model.addAttribute("suggestedCommunities",
            communityService.getPublicCommunities(PageRequest.of(0, 3)).getContent());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "feed";
    }

    @PostMapping("/posts/create")
    public String createPost(@Valid @ModelAttribute("postDTO") PostDTO dto,
                             BindingResult result,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Post content is required.");
            return "redirect:/feed";
        }
        User currentUser = authUtil.getCurrentUserOrThrow();
        postService.createPost(currentUser, dto, image);
        redirectAttributes.addFlashAttribute("success", "Post published!");
        return "redirect:/feed";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        postService.deletePost(currentUser, id);
        redirectAttributes.addFlashAttribute("success", "Post deleted.");
        return "redirect:/feed";
    }

    @PostMapping("/posts/{id}/like")
    public String likePost(@PathVariable Long id,
                           @RequestHeader(value = "Referer", defaultValue = "/feed") String referer) {
        postService.toggleLike(authUtil.getCurrentUserOrThrow(), id);
        return "redirect:" + referer;
    }

    @PostMapping("/posts/{id}/bookmark")
    public String bookmarkPost(@PathVariable Long id,
                               @RequestHeader(value = "Referer", defaultValue = "/feed") String referer) {
        postService.toggleBookmark(authUtil.getCurrentUserOrThrow(), id);
        return "redirect:" + referer;
    }

    @PostMapping("/posts/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             @RequestHeader(value = "Referer", defaultValue = "/feed") String referer,
                             RedirectAttributes redirectAttributes) {
        if (content == null || content.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Comment cannot be empty.");
            return "redirect:" + referer;
        }
        commentService.addComment(authUtil.getCurrentUserOrThrow(), id, content);
        return "redirect:" + referer;
    }

    @GetMapping("/bookmarks")
    public String bookmarks(@RequestParam(defaultValue = "0") int page, Model model) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        Page<Post> posts = postService.getBookmarks(currentUser, PageRequest.of(page, 10));
        model.addAttribute("posts", posts);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "bookmarks";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        User currentUser = authUtil.getCurrentUserOrThrow();
        model.addAttribute("suggestedUsers", userService.getSuggestedUsers(currentUser));
        model.addAttribute("communities",
            communityService.getPublicCommunities(PageRequest.of(0, 12)));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "discover";
    }
}
