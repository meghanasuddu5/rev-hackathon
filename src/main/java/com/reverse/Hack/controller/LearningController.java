package com.reverse.Hack.controller;

import com.reverse.Hack.model.User;
import com.reverse.Hack.service.LearningService;
import com.reverse.Hack.service.NotificationService;
import com.reverse.Hack.util.AuthUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/learn")
public class LearningController {

    private final LearningService learningService;
    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    public LearningController(LearningService learningService,
                              NotificationService notificationService,
                              AuthUtil authUtil) {
        this.learningService = learningService;
        this.notificationService = notificationService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public String hub(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(required = false) String q,
                      Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        var pageable = PageRequest.of(page, 12, Sort.by("rating").descending());

        var resources = (q != null && !q.isBlank())
                ? learningService.search(q, pageable)
                : learningService.getAllResources(pageable);

        model.addAttribute("resources", resources);
        model.addAttribute("recommended", learningService.getRecommendedResources(cu));
        model.addAttribute("searchQuery", q);
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "learn";
    }
}
