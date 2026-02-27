package backend.Service;

import backend.Dto.PurchaseOrderDetails;
import backend.Dto.PurchaseOrderRequest;
import backend.Entities.PurchaseOrder;
import backend.Entities.PurchaseOrderDetail;
import backend.Entities.Status;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public interface PurchaseOrderService {

    Flux<PurchaseOrder> findAll();
    Mono<PurchaseOrder> findById(Long id);
    Mono<List<PurchaseOrderDetail>> create(PurchaseOrderRequest req);
    Mono<PurchaseOrder> update(PurchaseOrder purchaseOrder);
    Mono<PurchaseOrder> updateStatus(Long id, Status status);
    Mono<Void> delete(Long id);

    Mono<PageResponse<PurchaseOrderDetails>> findPagination(Integer pageNumber, Integer pageSize, LocalDate startDate, LocalDate endDate, String status);
}


