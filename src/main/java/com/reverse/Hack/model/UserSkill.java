package com.reverse.Hack.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_skills")
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String skillName;

    @Column(length = 50)
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    private int progressPercent; // 0-100

    private int endorsements;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserSkill() {}

    // ── Getters / Setters ────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
    public int getEndorsements() { return endorsements; }
    public void setEndorsements(int endorsements) { this.endorsements = endorsements; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ── Builder ──────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final UserSkill s = new UserSkill();
        public Builder user(User v) { s.user = v; return this; }
        public Builder skillName(String v) { s.skillName = v; return this; }
        public Builder level(String v) { s.level = v; return this; }
        public Builder progressPercent(int v) { s.progressPercent = v; return this; }
        public Builder endorsements(int v) { s.endorsements = v; return this; }
        public UserSkill build() { return s; }
    }
}
