package com.reverse.Hack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class ResumeProfileDTO {

    @NotBlank(message = "Headline is required")
    @Size(max = 200)
    private String headline;

    @Size(max = 500)
    private String summary;

    @Size(max = 200)
    private String currentRole;

    @Size(max = 200)
    private String currentCompany;

    private String yearsOfExperience;

    // Comma-separated string from form; split in service
    private String skillsRaw;

    private String educationRaw;  // one entry per textarea line

    private String experienceRaw; // one entry per textarea line

    @Size(max = 255)
    private String linkedinUrl;

    @Size(max = 255)
    private String githubUrl;

    @Size(max = 255)
    private String portfolioUrl;

    // ── Getters / Setters ────────────────────────────────────
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }
    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }
    public String getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(String yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getSkillsRaw() { return skillsRaw; }
    public void setSkillsRaw(String skillsRaw) { this.skillsRaw = skillsRaw; }
    public String getEducationRaw() { return educationRaw; }
    public void setEducationRaw(String educationRaw) { this.educationRaw = educationRaw; }
    public String getExperienceRaw() { return experienceRaw; }
    public void setExperienceRaw(String experienceRaw) { this.experienceRaw = experienceRaw; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    /** Splits skillsRaw by comma into trimmed list */
    public List<String> parsedSkills() {
        if (skillsRaw == null || skillsRaw.isBlank()) return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String s : skillsRaw.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }

    /** Splits educationRaw by newline */
    public List<String> parsedEducation() {
        return parseLines(educationRaw);
    }

    /** Splits experienceRaw by newline */
    public List<String> parsedExperience() {
        return parseLines(experienceRaw);
    }

    private List<String> parseLines(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String line : raw.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }
}
