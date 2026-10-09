package com.reverse.Hack.model;

import com.reverse.Hack.enums.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(length = 500)
    private String message;

    @Column(length = 255)
    private String link;

    private boolean read = false;

    @CreationTimestamp private LocalDateTime createdAt;

    public Notification() {}

    public Long getId() { return id; }
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public User getActor() { return actor; }
    public void setActor(User actor) { this.actor = actor; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Notification n = new Notification();
        public Builder recipient(User v) { n.recipient = v; return this; }
        public Builder actor(User v) { n.actor = v; return this; }
        public Builder type(NotificationType v) { n.type = v; return this; }
        public Builder message(String v) { n.message = v; return this; }
        public Builder link(String v) { n.link = v; return this; }
        public Notification build() { return n; }
    }
}
