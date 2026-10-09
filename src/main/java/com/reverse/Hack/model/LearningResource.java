package com.reverse.Hack.model;

import com.reverse.Hack.enums.InterestCategory;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_resources")
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 255)
    private String url;

    @Column(length = 100)
    private String provider;  // e.g. "Coursera", "YouTube", "Article"

    @Column(length = 50)
    private String resourceType; // COURSE, ARTICLE, VIDEO, BOOK, PODCAST

    @Column(length = 50)
    private String difficulty;   // BEGINNER, INTERMEDIATE, ADVANCED

    @Enumerated(EnumType.STRING)
    private InterestCategory category;

    @ElementCollection
    @CollectionTable(name = "resource_skills", joinColumns = @JoinColumn(name = "resource_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    private int durationMinutes;
    private double rating; // 0.0 – 5.0
    private boolean free;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public LearningResource() {}

    // ── Getters / Setters ────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public InterestCategory getCategory() { return category; }
    public void setCategory(InterestCategory category) { this.category = category; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public boolean isFree() { return free; }
    public void setFree(boolean free) { this.free = free; }
    public User getAddedBy() { return addedBy; }
    public void setAddedBy(User addedBy) { this.addedBy = addedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ── Builder ──────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final LearningResource r = new LearningResource();
        public Builder title(String v) { r.title = v; return this; }
        public Builder description(String v) { r.description = v; return this; }
        public Builder url(String v) { r.url = v; return this; }
        public Builder provider(String v) { r.provider = v; return this; }
        public Builder resourceType(String v) { r.resourceType = v; return this; }
        public Builder difficulty(String v) { r.difficulty = v; return this; }
        public Builder category(InterestCategory v) { r.category = v; return this; }
        public Builder skills(List<String> v) { r.skills = v; return this; }
        public Builder durationMinutes(int v) { r.durationMinutes = v; return this; }
        public Builder rating(double v) { r.rating = v; return this; }
        public Builder free(boolean v) { r.free = v; return this; }
        public Builder addedBy(User v) { r.addedBy = v; return this; }
        public LearningResource build() { return r; }
    }
}
