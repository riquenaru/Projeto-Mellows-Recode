package br.com.mellows.api.exchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.mellows.api.book.Book;
import br.com.mellows.api.book.BookRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ExchangeRequestServiceTest {
    @Mock
    private BookRepository books;

    @Mock
    private ExchangeRequestRepository requests;

    @InjectMocks
    private ExchangeRequestService service;

    @Test
    void createsExchangeRequestWithNormalizedContactDetails() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(7L);
        when(books.findByIdAndActiveTrue(7L)).thenReturn(Optional.of(book));
        when(requests.save(any(ExchangeRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExchangeRequestResponse response = service.create(
                new ExchangeRequestPayload(7L, "  Alex  ", " ALEX@EXAMPLE.COM ", "  Quero trocar  "));

        ArgumentCaptor<ExchangeRequest> request = ArgumentCaptor.forClass(ExchangeRequest.class);
        verify(requests).save(request.capture());
        assertEquals("Alex", request.getValue().getName());
        assertEquals("alex@example.com", request.getValue().getEmail());
        assertEquals("Quero trocar", request.getValue().getMessage());
        assertEquals(7L, response.bookId());
        assertEquals("RECEIVED", response.status());
    }

    @Test
    void rejectsRequestsForUnavailableBooks() {
        when(books.findByIdAndActiveTrue(404L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.create(
                new ExchangeRequestPayload(404L, "Alex", "alex@example.com", null)));
        verify(requests, never()).save(any());
    }
}
