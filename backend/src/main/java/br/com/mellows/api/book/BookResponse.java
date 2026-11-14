package br.com.mellows.api.book;

public record BookResponse(
        Long id, String title, String author, String summary, String category,
        String readingLevel, String readingLevelLabel, String coverImage
) {
    static BookResponse from(Book book) {
        String label = switch (book.getReadingLevel()) {
            case "INICIANTE" -> "Iniciante";
            case "INTERMEDIARIO" -> "Intermediário";
            case "AVANCADO" -> "Avançado";
            default -> book.getReadingLevel();
        };
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getSummary(),
                book.getCategory(), book.getReadingLevel(), label, book.getCoverImage());
    }
}
