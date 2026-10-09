package com.reverse.Hack.model;

import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(length = 300)
    private String bio;

    @Column(length = 255)
    private String profilePicture;

    @Column(length = 255)
    private String coverImage;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean emailVerified = false;
    private boolean profilePublic = true;
    private boolean onboardingComplete = false;

    @ElementCollection(targetClass = InterestCategory.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private Set<InterestCategory> interests = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_follows",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<User> followers = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Circle> circles = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Default constructor
    public User() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public boolean isProfilePublic() { return profilePublic; }
    public void setProfilePublic(boolean profilePublic) { this.profilePublic = profilePublic; }

    public boolean isOnboardingComplete() { return onboardingComplete; }
    public void setOnboardingComplete(boolean onboardingComplete) { this.onboardingComplete = onboardingComplete; }

    public Set<InterestCategory> getInterests() { return interests; }
    public void setInterests(Set<InterestCategory> interests) { this.interests = interests != null ? new HashSet<>(interests) : new HashSet<>(); }

    public Set<User> getFollowing() { return following; }
    public void setFollowing(Set<User> following) { this.following = following; }

    public Set<User> getFollowers() { return followers; }
    public void setFollowers(Set<User> followers) { this.followers = followers; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public List<Circle> getCircles() { return circles; }
    public void setCircles(List<Circle> circles) { this.circles = circles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User user = new User();
        public Builder id(Long id) { user.id = id; return this; }
        public Builder username(String v) { user.username = v; return this; }
        public Builder email(String v) { user.email = v; return this; }
        public Builder passwordHash(String v) { user.passwordHash = v; return this; }
        public Builder displayName(String v) { user.displayName = v; return this; }
        public Builder bio(String v) { user.bio = v; return this; }
        public Builder profilePicture(String v) { user.profilePicture = v; return this; }
        public Builder coverImage(String v) { user.coverImage = v; return this; }
        public Builder role(Role v) { user.role = v; return this; }
        public Builder emailVerified(boolean v) { user.emailVerified = v; return this; }
        public Builder profilePublic(boolean v) { user.profilePublic = v; return this; }
        public Builder onboardingComplete(boolean v) { user.onboardingComplete = v; return this; }
        public Builder interests(Set<InterestCategory> v) { user.interests = new HashSet<>(v); return this; }
        public User build() { return user; }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return id != null && id.equals(u.id);
    }

    @Override
    public int hashCode() { return id != null ? id.hashCode() : 0; }
}
