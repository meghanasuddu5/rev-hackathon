package com.reverse.Hack.controller;

import com.reverse.Hack.model.User;
import com.reverse.Hack.service.NotificationService;
import com.reverse.Hack.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final AuthUtil authUtil;
    private final NotificationService notificationService;

    public SettingsController(AuthUtil authUtil, NotificationService notificationService) {
        this.authUtil = authUtil;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String settings(Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "settings";
    }
}
