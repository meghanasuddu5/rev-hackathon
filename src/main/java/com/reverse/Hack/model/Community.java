package com.reverse.Hack.model;

import com.reverse.Hack.enums.CommunityVisibility;
import com.reverse.Hack.enums.InterestCategory;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "communities")
public class Community {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private CommunityVisibility visibility = CommunityVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    private InterestCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityMembership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @CreationTimestamp private LocalDateTime createdAt;

    public Community() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public CommunityVisibility getVisibility() { return visibility; }
    public void setVisibility(CommunityVisibility visibility) { this.visibility = visibility; }
    public InterestCategory getCategory() { return category; }
    public void setCategory(InterestCategory category) { this.category = category; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public List<CommunityMembership> getMemberships() { return memberships; }
    public List<Post> getPosts() { return posts; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Community c = new Community();
        public Builder name(String v) { c.name = v; return this; }
        public Builder description(String v) { c.description = v; return this; }
        public Builder imageUrl(String v) { c.imageUrl = v; return this; }
        public Builder visibility(CommunityVisibility v) { c.visibility = v; return this; }
        public Builder category(InterestCategory v) { c.category = v; return this; }
        public Builder creator(User v) { c.creator = v; return this; }
        public Community build() { return c; }
    }
}
