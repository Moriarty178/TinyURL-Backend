package tiny_url.app.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tiny_url.app.backend.service.UrlService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tiny-url")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // API tạo shortURL
    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> request) {
        String longUrl = request.get("long_url");
        if (longUrl == null || longUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("Error", "Invalid URL"));
        } else {
            String shortUrl = urlService.shortenUrl(longUrl);
            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Success", "shortUrl", shortUrl));
        }
    }

    // API get shortURL đã tạo
    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> getShortUrl(@PathVariable String shortUrl, HttpServletRequest request) {
        String longUrl = urlService.getLongUrl(shortUrl);
        if (longUrl != null) {
            // ghi log click
            urlService.logClick(shortUrl, request);

            // Điều hướng đến đường dẫn gốc (longUrl)
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                    .header("Location", longUrl)
                    .build();
        }

        return ResponseEntity.notFound().build();
    }


}
