package com.reverse.Hack.controller;

import com.reverse.Hack.dto.CircleDTO;
import com.reverse.Hack.model.User;
import com.reverse.Hack.service.*;
import com.reverse.Hack.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/circles")
public class CircleController {

    private final AuthUtil authUtil;
    private final CircleService circleService;
    private final UserService userService;
    private final NotificationService notificationService;

    public CircleController(AuthUtil authUtil, CircleService circleService,
                            UserService userService, NotificationService notificationService) {
        this.authUtil = authUtil;
        this.circleService = circleService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listCircles(Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        model.addAttribute("circles", circleService.getCirclesForUser(cu));
        model.addAttribute("circleDTO", new CircleDTO());
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "circles";
    }

    @PostMapping("/create")
    public String createCircle(@Valid @ModelAttribute("circleDTO") CircleDTO dto,
                               BindingResult result, RedirectAttributes ra, Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        if (result.hasErrors()) {
            model.addAttribute("circles", circleService.getCirclesForUser(cu));
            model.addAttribute("currentUser", cu);
            return "circles";
        }
        try {
            circleService.createCircle(cu, dto);
            ra.addFlashAttribute("success", "Circle created!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/circles";
    }

    @PostMapping("/{id}/delete")
    public String deleteCircle(@PathVariable Long id, RedirectAttributes ra) {
        try {
            circleService.deleteCircle(authUtil.getCurrentUserOrThrow(), id);
            ra.addFlashAttribute("success", "Circle deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/circles";
    }

    @PostMapping("/{id}/add-member")
    public String addMember(@PathVariable Long id, @RequestParam String username, RedirectAttributes ra) {
        User cu = authUtil.getCurrentUserOrThrow();
        try {
            User target = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            circleService.addMember(cu, id, target.getId());
            ra.addFlashAttribute("success", target.getDisplayName() + " added.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/circles";
    }

    @PostMapping("/{id}/remove-member/{userId}")
    public String removeMember(@PathVariable Long id, @PathVariable Long userId, RedirectAttributes ra) {
        try {
            circleService.removeMember(authUtil.getCurrentUserOrThrow(), id, userId);
            ra.addFlashAttribute("success", "Member removed.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/circles";
    }
}
