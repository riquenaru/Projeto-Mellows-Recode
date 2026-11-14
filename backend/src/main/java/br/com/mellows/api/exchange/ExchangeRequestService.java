package br.com.mellows.api.exchange;

import br.com.mellows.api.book.Book;
import br.com.mellows.api.book.BookRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExchangeRequestService {
    private final BookRepository books;
    private final ExchangeRequestRepository requests;

    public ExchangeRequestService(BookRepository books, ExchangeRequestRepository requests) {
        this.books = books;
        this.requests = requests;
    }

    @Transactional
    public ExchangeRequestResponse create(ExchangeRequestPayload payload) {
        Book book = books.findByIdAndActiveTrue(payload.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
        String email = payload.email().trim().toLowerCase(Locale.ROOT);
        ExchangeRequest request = new ExchangeRequest(book, payload.name().trim(), email,
                payload.message() == null || payload.message().isBlank() ? null : payload.message().trim());
        return ExchangeRequestResponse.from(requests.save(request));
    }
}
