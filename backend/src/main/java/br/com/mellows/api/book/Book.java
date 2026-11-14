package br.com.mellows.api.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import java.time.Instant;

@Entity
@Table(name = "books")
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 180)
    private String title;
    @Column(nullable = false, length = 180)
    private String author;
    @Column(nullable = false, length = 1200)
    private String summary;
    @Column(nullable = false, length = 80)
    private String category;
    @Column(name = "reading_level", nullable = false, length = 24)
    private String readingLevel;
    @Column(name = "cover_image", nullable = false, length = 300)
    private String coverImage;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Book() { }

    @PrePersist
    void setCreatedAt() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getSummary() { return summary; }
    public String getCategory() { return category; }
    public String getReadingLevel() { return readingLevel; }
    public String getCoverImage() { return coverImage; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}
