package br.com.mellows.api.book;

import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) { this.repository = repository; }

    public List<BookResponse> search(String query, String level) {
        String normalizedQuery = query == null ? "" : query.trim();
        String normalizedLevel = level == null ? "" : level.trim().toUpperCase(Locale.ROOT);
        if (!normalizedLevel.isEmpty() && !List.of("INICIANTE", "INTERMEDIARIO", "AVANCADO").contains(normalizedLevel)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nível de leitura inválido.");
        }
        return repository.searchActive(normalizedQuery, normalizedLevel).stream().map(BookResponse::from).toList();
    }

    public BookResponse findById(Long id) {
        return repository.findByIdAndActiveTrue(id).map(BookResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
    }
}
