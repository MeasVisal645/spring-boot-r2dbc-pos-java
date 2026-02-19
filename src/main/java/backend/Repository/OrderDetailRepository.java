package backend.Repository;

import backend.Entities.OrderDetail;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.Bidi;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends R2dbcRepository<OrderDetail, Long> {
    Flux<OrderDetail> findByOrderId(Long id);

    @Query(value = """
        SELECT COALESCE(SUM(od.total), 0)
        FROM orderDetail od
        JOIN orderItem oi ON od.orderId = oi.id
        WHERE oi.createdDate >= :start AND oi.createdDate < :end
    """)
    Mono<BigDecimal> sumTotalSalesByDate(LocalDate start, LocalDate end);

}
