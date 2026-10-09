package com.reverse.Hack.controller;

import com.reverse.Hack.model.User;
import com.reverse.Hack.service.NotificationService;
import com.reverse.Hack.util.AuthUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final AuthUtil authUtil;
    private final NotificationService notificationService;

    public NotificationController(AuthUtil authUtil, NotificationService notificationService) {
        this.authUtil = authUtil;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String notifications(@RequestParam(defaultValue = "0") int page, Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        model.addAttribute("notifications",
            notificationService.getNotifications(cu, PageRequest.of(page, 20)));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        model.addAttribute("currentUser", cu);
        return "notifications";
    }

    @PostMapping("/mark-read")
    public String markAllRead(RedirectAttributes ra) {
        notificationService.markAllRead(authUtil.getCurrentUserOrThrow());
        ra.addFlashAttribute("success", "All notifications marked as read.");
        return "redirect:/notifications";
    }
}
