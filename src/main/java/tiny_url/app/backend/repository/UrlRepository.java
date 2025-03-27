package tiny_url.app.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tiny_url.app.backend.entity.UrlEntity;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByShortUrl(String shortUrl);

    Optional<UrlEntity> findByLongUrl(String longUrl);
}
