package com.reverse.Hack.controller;

import com.reverse.Hack.model.User;
import com.reverse.Hack.service.*;
import com.reverse.Hack.util.AuthUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SearchController {

    private final AuthUtil authUtil;
    private final UserService userService;
    private final PostService postService;
    private final CommunityService communityService;
    private final NotificationService notificationService;

    public SearchController(AuthUtil authUtil, UserService userService, PostService postService,
                            CommunityService communityService, NotificationService notificationService) {
        this.authUtil = authUtil;
        this.userService = userService;
        this.postService = postService;
        this.communityService = communityService;
        this.notificationService = notificationService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        if (!q.isBlank()) {
            var pageable = PageRequest.of(page, 10);
            model.addAttribute("users", userService.searchUsers(q, pageable));
            model.addAttribute("posts", postService.searchPosts(q, pageable));
            model.addAttribute("communities", communityService.searchCommunities(q, pageable));
        }
        model.addAttribute("query", q);
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "search";
    }
}
