package ee.smit.inventory.record;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository interface for {@link VinylRecord} entity.
 * Provides CRUD operations and custom query methods.
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RecordRepository extends CrudRepository<VinylRecord, Long> {

    Page<VinylRecord> findAll(Pageable pageable);

    List<VinylRecord> findByGenre(Genre genre);

    List<VinylRecord> findByCondition(RecordCondition condition);

    List<VinylRecord> findByArtistContainsIgnoreCase(String artist);

    List<VinylRecord> findByTitleContainsIgnoreCase(String title);

    @Query("SELECT * FROM vinyl_records WHERE LOWER(title) LIKE LOWER('%' || :query || '%') ESCAPE '\\' OR LOWER(artist) LIKE LOWER('%' || :query || '%') ESCAPE '\\'")
    List<VinylRecord> searchByTitleOrArtist(String query);

    long countByGenre(Genre genre);
}
