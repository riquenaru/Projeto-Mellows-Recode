package br.com.mellows.api.signup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "community_signups")
public class CommunitySignup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @Column(nullable = false)
    private boolean consent;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CommunitySignup() { }

    public CommunitySignup(String name, String email, boolean consent) {
        this.name = name;
        this.email = email;
        this.consent = consent;
    }

    @PrePersist
    void setCreatedAt() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean hasConsent() { return consent; }
    public Instant getCreatedAt() { return createdAt; }
}
