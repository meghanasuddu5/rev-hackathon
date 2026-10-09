package com.reverse.Hack.controller;

import com.reverse.Hack.dto.CommunityDTO;
import com.reverse.Hack.dto.PostDTO;
import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.enums.PostAudience;
import com.reverse.Hack.model.Community;
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
@RequestMapping("/communities")
public class CommunityController {

    private final AuthUtil authUtil;
    private final CommunityService communityService;
    private final PostService postService;
    private final NotificationService notificationService;

    public CommunityController(AuthUtil authUtil, CommunityService communityService,
                               PostService postService, NotificationService notificationService) {
        this.authUtil = authUtil;
        this.communityService = communityService;
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String q, Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        var pageable = PageRequest.of(page, 12);
        var communities = (q != null && !q.isBlank())
                ? communityService.searchCommunities(q, pageable)
                : communityService.getPublicCommunities(pageable);
        model.addAttribute("communities", communities);
        model.addAttribute("myCommunities", communityService.getCommunitiesForUser(cu));
        model.addAttribute("communityDTO", new CommunityDTO());
        model.addAttribute("categories", InterestCategory.values());
        model.addAttribute("currentUser", cu);
        model.addAttribute("searchQuery", q);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "communities";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("communityDTO") CommunityDTO dto,
                         BindingResult result, RedirectAttributes ra, Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        if (result.hasErrors()) {
            model.addAttribute("communities", communityService.getPublicCommunities(PageRequest.of(0, 12)));
            model.addAttribute("myCommunities", communityService.getCommunitiesForUser(cu));
            model.addAttribute("communityDTO", dto);
            model.addAttribute("categories", InterestCategory.values());
            model.addAttribute("currentUser", cu);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
            return "communities";
        }
        try {
            Community community = communityService.createCommunity(cu, dto);
            ra.addFlashAttribute("success", "Community created!");
            return "redirect:/communities/" + community.getId();
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/communities";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        Community community = communityService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        model.addAttribute("community", community);
        model.addAttribute("isMember", communityService.isMember(cu, community));
        model.addAttribute("memberCount", communityService.getMemberCount(community));
        model.addAttribute("posts", postService.getCommunityPosts(id, PageRequest.of(page, 10)));
        model.addAttribute("postDTO", new PostDTO());
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "community-detail";
    }

    @PostMapping("/{id}/join")
    public String join(@PathVariable Long id, RedirectAttributes ra) {
        try {
            communityService.joinCommunity(authUtil.getCurrentUserOrThrow(), id);
            ra.addFlashAttribute("success", "You joined the community!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/communities/" + id;
    }

    @PostMapping("/{id}/leave")
    public String leave(@PathVariable Long id, RedirectAttributes ra) {
        try {
            communityService.leaveCommunity(authUtil.getCurrentUserOrThrow(), id);
            ra.addFlashAttribute("success", "You left the community.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/communities/" + id;
    }

    @PostMapping("/{id}/post")
    public String createPost(@PathVariable Long id,
                             @Valid @ModelAttribute("postDTO") PostDTO dto,
                             BindingResult result,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             RedirectAttributes ra) {
        User cu = authUtil.getCurrentUserOrThrow();
        Community community = communityService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        if (!communityService.isMember(cu, community)) {
            ra.addFlashAttribute("error", "Join the community to post.");
            return "redirect:/communities/" + id;
        }
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Post content is required.");
            return "redirect:/communities/" + id;
        }
        dto.setCommunityId(id);
        dto.setAudience(PostAudience.COMMUNITY);
        postService.createPost(cu, dto, image);
        ra.addFlashAttribute("success", "Post published!");
        return "redirect:/communities/" + id;
    }
}
