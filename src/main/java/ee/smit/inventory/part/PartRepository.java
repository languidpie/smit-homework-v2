package ee.smit.inventory.part;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository interface for {@link Part} entity.
 * Provides CRUD operations and custom query methods.
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PartRepository extends CrudRepository<Part, Long> {

    Page<Part> findAll(Pageable pageable);

    List<Part> findByType(PartType type);

    List<Part> findByCondition(PartCondition condition);

    @Query("SELECT * FROM parts WHERE LOWER(name) LIKE LOWER('%' || :query || '%') ESCAPE '\\' OR LOWER(description) LIKE LOWER('%' || :query || '%') ESCAPE '\\'")
    List<Part> searchByNameOrDescription(String query);

    long countByType(PartType type);
}
