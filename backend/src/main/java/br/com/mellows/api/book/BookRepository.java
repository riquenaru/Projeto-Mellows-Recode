package br.com.mellows.api.book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select b from Book b where b.active = true " +
            "and (:query = '' or lower(b.title) like lower(concat('%', :query, '%')) " +
            "or lower(b.author) like lower(concat('%', :query, '%'))) " +
            "and (:level = '' or b.readingLevel = :level) order by b.title asc")
    List<Book> searchActive(@Param("query") String query, @Param("level") String level);

    Optional<Book> findByIdAndActiveTrue(Long id);
}
