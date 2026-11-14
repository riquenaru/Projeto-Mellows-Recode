package br.com.mellows.api.googlebooks;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google-books")
@Validated
public class GoogleBooksController {
    private final GoogleBooksService service;

    public GoogleBooksController(GoogleBooksService service) { this.service = service; }

    @GetMapping
    public List<GoogleBooksResponse> search(@RequestParam @NotBlank @Size(min = 2, max = 100) String q) {
        return service.search(q.trim());
    }
}
