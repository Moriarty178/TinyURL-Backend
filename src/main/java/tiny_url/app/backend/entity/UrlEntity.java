package tiny_url.app.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "url_table")
public class UrlEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_url", unique = true, nullable = true, length = 10)
    private String shortUrl;

    @Column(name = "long_url", nullable = true, columnDefinition = "TEXT")
    private String longUrl;

    public UrlEntity() {
    }

    public UrlEntity(Long id, String shortUrl, String longUrl) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    //    @PrePersist
//    private void generateShortUrl() {
//        if (this.id == null) {
//            this.id = Instant.now().toEpochMilli(); // Lấy timestamp hiện tại
//        }
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
