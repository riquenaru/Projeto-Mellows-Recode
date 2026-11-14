package br.com.mellows.api.exchange;

import br.com.mellows.api.book.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "exchange_requests")
public class ExchangeRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 254)
    private String email;
    @Column(length = 1000)
    private String message;
    @Column(nullable = false, length = 24)
    private String status = "RECEIVED";
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ExchangeRequest() { }

    public ExchangeRequest(Book book, String name, String email, String message) {
        this.book = book;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    @PrePersist
    void setCreatedAt() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
