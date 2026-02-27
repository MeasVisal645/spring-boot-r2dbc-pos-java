package backend.Repository;

import backend.Entities.PurchaseOrderDetail;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseOrderDetailRepository extends R2dbcRepository<PurchaseOrderDetail, Long> {
    Mono<Void> deleteByPurchaseId(Long purchaseId);
}
