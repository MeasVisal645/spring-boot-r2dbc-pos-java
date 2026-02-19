package backend.Repository;

import backend.Entities.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends R2dbcRepository<Customer, Long> {

    @Query(value = "SELECT COUNT(*) FROM customer")
    Mono<Long> countCustomers();
}
