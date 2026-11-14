package br.com.mellows.api.googlebooks;

import java.util.List;

public record GoogleBooksResponse(
        String id,
        String title,
        List<String> authors,
        String description,
        String publishedDate,
        List<String> categories,
        String thumbnail,
        String infoLink) { }
