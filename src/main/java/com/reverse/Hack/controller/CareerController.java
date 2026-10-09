package com.reverse.Hack.controller;

import com.reverse.Hack.dto.ResumeProfileDTO;
import com.reverse.Hack.model.ResumeProfile;
import com.reverse.Hack.model.User;
import com.reverse.Hack.service.CareerService;
import com.reverse.Hack.service.NotificationService;
import com.reverse.Hack.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/career")
public class CareerController {

    private final CareerService careerService;
    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    public CareerController(CareerService careerService,
                            NotificationService notificationService,
                            AuthUtil authUtil) {
        this.careerService = careerService;
        this.notificationService = notificationService;
        this.authUtil = authUtil;
    }

    // ── Jobs ─────────────────────────────────────────────────

    @GetMapping("/jobs")
    public String jobs(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String q,
                       Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        var pageable = PageRequest.of(page, 10, Sort.by("postedAt").descending());

        var jobs = (q != null && !q.isBlank())
                ? careerService.searchJobs(q, pageable)
                : careerService.getAllJobs(pageable);

        model.addAttribute("jobs", jobs);
        model.addAttribute("recommendedJobs", careerService.getRecommendedJobs(cu));
        model.addAttribute("searchQuery", q);
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "career-jobs";
    }

    // ── Resume ───────────────────────────────────────────────

    @GetMapping("/resume")
    public String resume(Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        var existing = careerService.getResumeProfile(cu);
        ResumeProfileDTO dto = new ResumeProfileDTO();

        // Pre-populate DTO from existing profile for edit mode
        existing.ifPresent(p -> {
            dto.setHeadline(p.getHeadline());
            dto.setSummary(p.getSummary());
            dto.setCurrentRole(p.getCurrentRole());
            dto.setCurrentCompany(p.getCurrentCompany());
            dto.setYearsOfExperience(p.getYearsOfExperience());
            dto.setSkillsRaw(String.join(", ", p.getSkills()));
            dto.setEducationRaw(String.join("\n", p.getEducation()));
            dto.setExperienceRaw(String.join("\n", p.getExperience()));
            dto.setLinkedinUrl(p.getLinkedinUrl());
            dto.setGithubUrl(p.getGithubUrl());
            dto.setPortfolioUrl(p.getPortfolioUrl());
        });

        model.addAttribute("resumeDTO", dto);
        model.addAttribute("resumeProfile", existing.orElse(null));
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "career-resume";
    }

    @PostMapping("/resume/save")
    public String saveResume(@Valid @ModelAttribute("resumeDTO") ResumeProfileDTO dto,
                             BindingResult result,
                             RedirectAttributes ra,
                             Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        if (result.hasErrors()) {
            model.addAttribute("resumeProfile", careerService.getResumeProfile(cu).orElse(null));
            model.addAttribute("currentUser", cu);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
            return "career-resume";
        }
        try {
            ResumeProfile saved = careerService.saveResumeProfile(cu, dto);
            ra.addFlashAttribute("success",
                "Resume saved! Your profile strength score: " + saved.getProfileStrengthScore() + "/100");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to save resume: " + e.getMessage());
        }
        return "redirect:/career/resume";
    }

    // ── Skills ───────────────────────────────────────────────

    @GetMapping("/skills")
    public String skills(Model model) {
        User cu = authUtil.getCurrentUserOrThrow();
        model.addAttribute("skills", careerService.getSkillsForUser(cu));
        model.addAttribute("currentUser", cu);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(cu));
        return "career-skills";
    }

    @PostMapping("/skills/add")
    public String addSkill(@RequestParam String skillName,
                           @RequestParam(defaultValue = "BEGINNER") String level,
                           @RequestParam(defaultValue = "0") int progress,
                           RedirectAttributes ra) {
        User cu = authUtil.getCurrentUserOrThrow();
        if (skillName == null || skillName.isBlank()) {
            ra.addFlashAttribute("error", "Skill name is required.");
            return "redirect:/career/skills";
        }
        careerService.addOrUpdateSkill(cu, skillName.trim(), level, progress);
        ra.addFlashAttribute("success", "Skill \"" + skillName.trim() + "\" added!");
        return "redirect:/career/skills";
    }

    @PostMapping("/skills/{id}/delete")
    public String deleteSkill(@PathVariable Long id, RedirectAttributes ra) {
        User cu = authUtil.getCurrentUserOrThrow();
        try {
            careerService.deleteSkill(cu, id);
            ra.addFlashAttribute("success", "Skill removed.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/career/skills";
    }

    @PostMapping("/skills/{id}/endorse")
    public String endorseSkill(@PathVariable Long id,
                                @RequestHeader(value = "Referer", defaultValue = "/career/skills") String ref) {
        careerService.endorseSkill(id);
        return "redirect:" + ref;
    }
}
