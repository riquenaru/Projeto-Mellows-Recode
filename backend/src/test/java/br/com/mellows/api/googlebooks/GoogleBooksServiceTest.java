package br.com.mellows.api.googlebooks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

class GoogleBooksServiceTest {
    @Test
    void mapsPublicBookDataAndUsesGoogleBooksLink() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(org.hamcrest.Matchers.containsString("q=harry%20potter")))
                .andRespond(withSuccess("""
                        {"items":[{"id":"abc123","volumeInfo":{"title":"Harry Potter",
                        "authors":["J. K. Rowling"],"description":"A story","publishedDate":"1997",
                        "categories":["Fantasy"],"imageLinks":{"thumbnail":"http://covers.example/book.jpg"}}}]}
                        """, MediaType.APPLICATION_JSON));

        var books = new GoogleBooksService(builder, "test-key").search("harry potter");

        assertThat(books).hasSize(1);
        assertThat(books.getFirst().title()).isEqualTo("Harry Potter");
        assertThat(books.getFirst().authors()).containsExactly("J. K. Rowling");
        assertThat(books.getFirst().thumbnail()).startsWith("https://");
        assertThat(books.getFirst().infoLink()).isEqualTo("https://books.google.com/books?id=abc123");
        server.verify();
    }

    @Test
    void returnsServiceUnavailableWhenApiKeyIsMissing() {
        assertThatThrownBy(() -> new GoogleBooksService(RestClient.builder(), " ").search("livros"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(((ResponseStatusException) exception).getStatusCode())
                        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
