package backend.Repository;

import backend.Entities.OrderItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {

    @Query( value = """
        SELECT COUNT(*)
        FROM orderItem
        WHERE createdDate >= :start AND createdDate < :end
    """)
    Mono<Long> countOrderItemByDate(LocalDate start, LocalDate end);

}
