package ee.smit.inventory.part;

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

    List<Part> findByNameContainsIgnoreCase(String name);

    List<Part> findByLocationContainsIgnoreCase(String location);

    long countByType(PartType type);
}
