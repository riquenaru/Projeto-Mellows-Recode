package br.com.mellows.api.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookService service;

    @Test
    void normalizesCatalogFiltersBeforeQueryingRepository() {
        when(repository.searchActive("rapunzel", "INICIANTE")).thenReturn(List.of());

        List<BookResponse> result = service.search("  rapunzel  ", "iniciante");

        assertEquals(List.of(), result);
        verify(repository).searchActive("rapunzel", "INICIANTE");
    }

    @Test
    void rejectsUnsupportedReadingLevel() {
        assertThrows(ResponseStatusException.class, () -> service.search(null, "expert"));
        verifyNoInteractions(repository);
    }
}
