package br.com.mellows.api.book;

import java.util.List;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    private final BookService service;

    public BookController(BookService service) { this.service = service; }

    @GetMapping
    public List<BookResponse> search(@RequestParam(required = false) @Size(max = 100) String q,
                                     @RequestParam(required = false) String level) {
        return service.search(q, level);
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable @Positive Long id) { return service.findById(id); }
}
