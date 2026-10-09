package com.reverse.Hack.model;

import com.reverse.Hack.enums.InterestCategory;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_listings")
public class JobListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String company;

    @Column(length = 200)
    private String location;

    @Column(length = 2000)
    private String description;

    @Column(length = 50)
    private String jobType;  // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE

    @Column(length = 100)
    private String salaryRange;

    @Enumerated(EnumType.STRING)
    private InterestCategory category;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> requiredSkills = new ArrayList<>();

    @Column(length = 50)
    private String experienceLevel; // ENTRY, MID, SENIOR, LEAD

    @Column(length = 255)
    private String applyUrl;

    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime postedAt;

    public JobListing() {}

    // ── Getters / Setters ────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public InterestCategory getCategory() { return category; }
    public void setCategory(InterestCategory category) { this.category = category; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getApplyUrl() { return applyUrl; }
    public void setApplyUrl(String applyUrl) { this.applyUrl = applyUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }

    // ── Builder ──────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final JobListing j = new JobListing();
        public Builder title(String v) { j.title = v; return this; }
        public Builder company(String v) { j.company = v; return this; }
        public Builder location(String v) { j.location = v; return this; }
        public Builder description(String v) { j.description = v; return this; }
        public Builder jobType(String v) { j.jobType = v; return this; }
        public Builder salaryRange(String v) { j.salaryRange = v; return this; }
        public Builder category(InterestCategory v) { j.category = v; return this; }
        public Builder requiredSkills(List<String> v) { j.requiredSkills = v; return this; }
        public Builder experienceLevel(String v) { j.experienceLevel = v; return this; }
        public Builder applyUrl(String v) { j.applyUrl = v; return this; }
        public Builder active(boolean v) { j.active = v; return this; }
        public JobListing build() { return j; }
    }
}
