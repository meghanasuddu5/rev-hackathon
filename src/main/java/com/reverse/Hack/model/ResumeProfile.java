package com.reverse.Hack.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resume_profiles")
public class ResumeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 200)
    private String headline;  // "Full Stack Developer | 3 years exp"

    @Column(name = "profile_summary", length = 500)
    private String summary;

    @Column(name = "current_role_title", length = 200)
    private String currentRole;

    @Column(length = 200)
    private String currentCompany;

    @Column(length = 100)
    private String yearsOfExperience; // "1-2 years", "3-5 years" etc.

    @ElementCollection
    @CollectionTable(name = "resume_skills", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "resume_education", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "education_entry", length = 300)
    private List<String> education = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "resume_experience", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "experience_entry", length = 500)
    private List<String> experience = new ArrayList<>();

    @Column(length = 255)
    private String linkedinUrl;

    @Column(length = 255)
    private String githubUrl;

    @Column(length = 255)
    private String portfolioUrl;

    // AI analysis fields
    @Column(length = 2000)
    private String aiAnalysis;       // Generated analysis text

    @Column(length = 1000)
    private String strengthsSummary;

    @Column(length = 1000)
    private String improvementTips;

    private int profileStrengthScore; // 0-100

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ResumeProfile() {}

    // ── Getters / Setters ────────────────────────────────────
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<String> getEducation() { return education; }
    public void setEducation(List<String> education) { this.education = education; }
    public List<String> getExperience() { return experience; }
    public void setExperience(List<String> experience) { this.experience = experience; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public String getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }
    public String getStrengthsSummary() { return strengthsSummary; }
    public void setStrengthsSummary(String strengthsSummary) { this.strengthsSummary = strengthsSummary; }
    public String getImprovementTips() { return improvementTips; }
    public void setImprovementTips(String improvementTips) { this.improvementTips = improvementTips; }
    public int getProfileStrengthScore() { return profileStrengthScore; }
    public void setProfileStrengthScore(int profileStrengthScore) { this.profileStrengthScore = profileStrengthScore; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
