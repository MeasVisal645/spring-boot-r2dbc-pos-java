package backend.Repository;

import backend.Entities.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    Mono<Boolean> existsByCode(String code);

    @Query(value = "SELECT COUNT(*) FROM product")
    Mono<Long> countProducts();
}
