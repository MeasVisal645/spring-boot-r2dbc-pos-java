package backend.Service;

import backend.Entities.PurchaseOrderDetail;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface PurchaseOrderDetailService {

    Flux<PurchaseOrderDetail> findAll();
    Mono<PurchaseOrderDetail> findById(Long id);
    Mono<PurchaseOrderDetail> update(PurchaseOrderDetail purchaseOrderDetail);
    Mono<Void> delete(Long id);

}
