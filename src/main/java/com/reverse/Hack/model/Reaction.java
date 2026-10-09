package com.reverse.Hack.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "reactions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class Reaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp private LocalDateTime createdAt;

    public Reaction() {}

    public Long getId() { return id; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Reaction r = new Reaction();
        public Builder post(Post v) { r.post = v; return this; }
        public Builder user(User v) { r.user = v; return this; }
        public Reaction build() { return r; }
    }
}
