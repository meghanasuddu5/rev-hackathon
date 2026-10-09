package com.reverse.Hack.service.impl;

import com.reverse.Hack.dto.RecommendationDTO;
import com.reverse.Hack.dto.ResumeProfileDTO;
import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.model.*;
import com.reverse.Hack.repository.*;
import com.reverse.Hack.service.CareerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CareerServiceImpl implements CareerService {

    private final JobListingRepository jobRepo;
    private final ResumeProfileRepository resumeRepo;
    private final UserSkillRepository skillRepo;

    public CareerServiceImpl(JobListingRepository jobRepo,
                             ResumeProfileRepository resumeRepo,
                             UserSkillRepository skillRepo) {
        this.jobRepo = jobRepo;
        this.resumeRepo = resumeRepo;
        this.skillRepo = skillRepo;
    }

    // ── JOBS ─────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<JobListing> getAllJobs(Pageable pageable) {
        return jobRepo.findByActiveTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobListing> searchJobs(String query, Pageable pageable) {
        return jobRepo.search(query, pageable);
    }

    /**
     * Explainable job recommendation engine.
     * Factors: user interests, user skills from resume, experience level.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDTO<JobListing>> getRecommendedJobs(User user) {
        Set<InterestCategory> interests = user.getInterests();
        List<RecommendationDTO<JobListing>> results = new ArrayList<>();

        // Get user's resume skills for skill-matching
        Optional<ResumeProfile> resume = resumeRepo.findByUser(user);
        List<String> userSkills = resume.map(ResumeProfile::getSkills).orElse(Collections.emptyList());
        List<String> userSkillsLower = userSkills.stream()
                .map(String::toLowerCase).toList();

        if (interests != null && !interests.isEmpty()) {
            List<InterestCategory> interestList = new ArrayList<>(interests);
            List<JobListing> matched = jobRepo.findByCategoryInAndActiveTrue(interestList);

            for (JobListing job : matched) {
                int score = 50;
                List<String> matchedSkills = new ArrayList<>();

                // Check skill overlap
                for (String reqSkill : job.getRequiredSkills()) {
                    if (userSkillsLower.contains(reqSkill.toLowerCase())) {
                        matchedSkills.add(reqSkill);
                        score += 10;
                    }
                }

                String reason;
                if (!matchedSkills.isEmpty()) {
                    reason = "Your skills (" + String.join(", ", matchedSkills) + ") match this "
                            + job.getCategory().name().toLowerCase().replace("_", " ") + " role";
                } else {
                    reason = "Matches your interest in "
                            + job.getCategory().name().toLowerCase().replace("_", " ");
                }

                results.add(new RecommendationDTO<>(job, reason,
                        Math.min(score, 100), matchedSkills.isEmpty() ? "INTEREST_MATCH" : "SKILL_MATCH"));
            }
        }

        // Fill remaining slots with any active jobs
        if (results.size() < 6) {
            Page<JobListing> all = jobRepo.findByActiveTrue(
                    org.springframework.data.domain.PageRequest.of(0, 20));
            Set<Long> added = new HashSet<>();
            results.forEach(r -> added.add(r.getItem().getId()));
            for (JobListing j : all.getContent()) {
                if (!added.contains(j.getId())) {
                    results.add(new RecommendationDTO<>(j,
                            "Popular job opening that may interest you", 30, "POPULAR"));
                    if (results.size() >= 9) break;
                }
            }
        }

        results.sort(Comparator.<RecommendationDTO<JobListing>>comparingInt(RecommendationDTO::getScore).reversed());
        return results.size() > 9 ? results.subList(0, 9) : results;
    }

    // ── RESUME ───────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Optional<ResumeProfile> getResumeProfile(User user) {
        return resumeRepo.findByUser(user);
    }

    @Override
    public ResumeProfile saveResumeProfile(User user, ResumeProfileDTO dto) {
        ResumeProfile profile = resumeRepo.findByUser(user).orElse(new ResumeProfile());
        profile.setUser(user);
        profile.setHeadline(dto.getHeadline());
        profile.setSummary(dto.getSummary());
        profile.setCurrentRole(dto.getCurrentRole());
        profile.setCurrentCompany(dto.getCurrentCompany());
        profile.setYearsOfExperience(dto.getYearsOfExperience());
        profile.setSkills(dto.parsedSkills());
        profile.setEducation(dto.parsedEducation());
        profile.setExperience(dto.parsedExperience());
        profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setGithubUrl(dto.getGithubUrl());
        profile.setPortfolioUrl(dto.getPortfolioUrl());

        ResumeProfile saved = resumeRepo.save(profile);
        return analyzeResume(user);
    }

    /**
     * Rule-based AI resume analysis that produces an explainable score and feedback.
     */
    @Override
    public ResumeProfile analyzeResume(User user) {
        ResumeProfile profile = resumeRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("No resume profile found"));

        int score = 0;
        List<String> strengths = new ArrayList<>();
        List<String> tips = new ArrayList<>();

        // Headline
        if (profile.getHeadline() != null && !profile.getHeadline().isBlank()) {
            score += 15;
            strengths.add("Clear professional headline");
        } else {
            tips.add("Add a headline summarizing your professional identity");
        }

        // Summary
        if (profile.getSummary() != null && profile.getSummary().length() > 100) {
            score += 15;
            strengths.add("Detailed professional summary");
        } else if (profile.getSummary() != null && !profile.getSummary().isBlank()) {
            score += 8;
            tips.add("Expand your summary to at least 2-3 sentences (aim for 100+ characters)");
        } else {
            tips.add("Add a professional summary to introduce yourself to recruiters");
        }

        // Skills
        int skillCount = profile.getSkills().size();
        if (skillCount >= 8) {
            score += 20;
            strengths.add("Strong skills section with " + skillCount + " skills listed");
        } else if (skillCount >= 4) {
            score += 12;
            tips.add("Add more skills — aim for at least 8 relevant skills");
        } else if (skillCount > 0) {
            score += 6;
            tips.add("Your skills section is thin. List all technical and soft skills you have");
        } else {
            tips.add("Add a skills section — it's the first thing recruiters look for");
        }

        // Experience
        int expCount = profile.getExperience().size();
        if (expCount >= 3) {
            score += 20;
            strengths.add("Rich work experience with " + expCount + " entries");
        } else if (expCount >= 1) {
            score += 12;
            tips.add("Add more experience entries to strengthen your profile");
        } else {
            tips.add("Add your work experience, internships, or projects");
        }

        // Education
        if (!profile.getEducation().isEmpty()) {
            score += 10;
            strengths.add("Education section present");
        } else {
            tips.add("Add your education background");
        }

        // Current role & company
        if (profile.getCurrentRole() != null && !profile.getCurrentRole().isBlank()) {
            score += 5;
            strengths.add("Current role specified");
        } else {
            tips.add("Specify your current role or desired role");
        }

        // Links
        int linkCount = 0;
        if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isBlank()) linkCount++;
        if (profile.getGithubUrl() != null && !profile.getGithubUrl().isBlank()) linkCount++;
        if (profile.getPortfolioUrl() != null && !profile.getPortfolioUrl().isBlank()) linkCount++;
        if (linkCount >= 2) {
            score += 10;
            strengths.add("Multiple professional links added (LinkedIn, GitHub, Portfolio)");
        } else if (linkCount == 1) {
            score += 5;
            tips.add("Add more professional links (LinkedIn, GitHub, or Portfolio) to stand out");
        } else {
            tips.add("Add links to your LinkedIn, GitHub, or portfolio");
        }

        // Experience years
        if (profile.getYearsOfExperience() != null && !profile.getYearsOfExperience().isBlank()) {
            score += 5;
        }

        // Build AI analysis narrative
        String analysis = buildAnalysisNarrative(profile, score, strengths, tips);

        profile.setProfileStrengthScore(Math.min(score, 100));
        profile.setStrengthsSummary(String.join(" | ", strengths));
        profile.setImprovementTips(String.join(" | ", tips));
        profile.setAiAnalysis(analysis);

        return resumeRepo.save(profile);
    }

    private String buildAnalysisNarrative(ResumeProfile p, int score, List<String> strengths, List<String> tips) {
        String level = score >= 80 ? "excellent" : score >= 60 ? "good" : score >= 40 ? "moderate" : "early-stage";
        StringBuilder sb = new StringBuilder();
        sb.append("Your profile has a ").append(level).append(" strength score of ").append(Math.min(score, 100)).append("/100. ");

        if (!strengths.isEmpty()) {
            sb.append("Key strengths: ").append(String.join(", ", strengths)).append(". ");
        }
        if (!tips.isEmpty()) {
            sb.append("To improve, consider: ").append(tips.get(0));
            if (tips.size() > 1) sb.append(", and ").append(tips.get(1));
            sb.append(".");
        }
        if (score >= 80) {
            sb.append(" You have a compelling profile that is well-positioned for recruiter attention.");
        } else if (score >= 60) {
            sb.append(" With a few additions, your profile will be highly competitive.");
        }
        return sb.toString();
    }

    // ── SKILLS ───────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserSkill> getSkillsForUser(User user) {
        return skillRepo.findByUserOrderByEndorsementsDesc(user);
    }

    @Override
    public UserSkill addOrUpdateSkill(User user, String skillName, String level, int progress) {
        UserSkill skill = skillRepo.findByUserAndSkillNameIgnoreCase(user, skillName)
                .orElse(UserSkill.builder().user(user).skillName(skillName).build());
        skill.setLevel(level != null && !level.isBlank() ? level : "BEGINNER");
        skill.setProgressPercent(Math.max(0, Math.min(100, progress)));
        return skillRepo.save(skill);
    }

    @Override
    public void deleteSkill(User user, Long skillId) {
        UserSkill skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        if (!skill.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Not authorized");
        }
        skillRepo.delete(skill);
    }

    @Override
    public void endorseSkill(Long skillId) {
        UserSkill skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        skill.setEndorsements(skill.getEndorsements() + 1);
        skillRepo.save(skill);
    }
}
