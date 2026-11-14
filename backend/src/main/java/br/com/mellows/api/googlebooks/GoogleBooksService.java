package br.com.mellows.api.googlebooks;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoogleBooksService {
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes";
    private final RestClient restClient;
    private final String apiKey;

    public GoogleBooksService(RestClient.Builder builder,
                              @Value("${google.books.api-key:}") String apiKey) {
        this.restClient = builder.build();
        this.apiKey = apiKey;
    }

    public List<GoogleBooksResponse> search(String query) {
        if (apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Busca Google Books não configurada. Defina GOOGLE_BOOKS_API_KEY no servidor.");
        }
        String uri = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("q", query)
                .queryParam("maxResults", 12)
                .queryParam("printType", "books")
                .queryParam("projection", "lite")
                .queryParam("orderBy", "relevance")
                .queryParam("key", apiKey)
                .build().encode().toUriString();
        try {
            JsonNode response = restClient.get().uri(uri).retrieve().body(JsonNode.class);
            if (response == null || !response.path("items").isArray()) return List.of();
            List<GoogleBooksResponse> books = new ArrayList<>();
            for (JsonNode item : response.path("items")) {
                JsonNode info = item.path("volumeInfo");
                String id = text(item, "id");
                String title = text(info, "title");
                if (id.isBlank() || title.isBlank()) continue;
                books.add(new GoogleBooksResponse(id, title, strings(info.path("authors")),
                        text(info, "description"), text(info, "publishedDate"),
                        strings(info.path("categories")), secureImage(text(info.path("imageLinks"), "thumbnail")),
                        "https://books.google.com/books?id=" + id));
            }
            return List.copyOf(books);
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível consultar o Google Books no momento.", exception);
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isTextual() ? value.asText() : "";
    }

    private static List<String> strings(JsonNode node) {
        if (!node.isArray()) return List.of();
        List<String> values = new ArrayList<>();
        node.forEach(value -> { if (value.isTextual()) values.add(value.asText()); });
        return List.copyOf(values);
    }

    private static String secureImage(String image) {
        if (image.startsWith("http://")) return "https://" + image.substring("http://".length());
        return image.startsWith("https://") ? image : "";
    }
}
