package com.reverse.Hack.model;

import com.reverse.Hack.enums.CommunityRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_memberships",
    uniqueConstraints = @UniqueConstraint(columnNames = {"community_id", "user_id"}))
public class CommunityMembership {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private CommunityRole role = CommunityRole.MEMBER;

    @CreationTimestamp private LocalDateTime joinedAt;

    public CommunityMembership() {}

    public Long getId() { return id; }
    public Community getCommunity() { return community; }
    public void setCommunity(Community community) { this.community = community; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CommunityRole getRole() { return role; }
    public void setRole(CommunityRole role) { this.role = role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final CommunityMembership m = new CommunityMembership();
        public Builder community(Community v) { m.community = v; return this; }
        public Builder user(User v) { m.user = v; return this; }
        public Builder role(CommunityRole v) { m.role = v; return this; }
        public CommunityMembership build() { return m; }
    }
}
