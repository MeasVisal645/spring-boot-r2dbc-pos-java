package backend.ServiceImpl;

import backend.Entities.PurchaseOrderDetail;
import backend.Repository.PurchaseOrderDetailRepository;
import backend.Repository.PurchaseOrderRepository;
import backend.Service.PurchaseOrderDetailService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PurchaseOrderDetailServiceImpl implements PurchaseOrderDetailService {

    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<PurchaseOrderDetail> findAll() {
        return purchaseOrderDetailRepository.findAll();
    }

    @Override
    public Mono<PurchaseOrderDetail> findById(Long id) {
        return purchaseOrderDetailRepository.findById(id);
    }

    @Override
    public Mono<PurchaseOrderDetail> create(PurchaseOrderDetail purchaseOrderDetail) {
        return purchaseOrderDetailRepository
                .save(PurchaseOrderDetail.from(purchaseOrderDetail)
                .build());
    }

    @Override
    public Mono<PurchaseOrderDetail> update(PurchaseOrderDetail purchaseOrderDetail) {
        return purchaseOrderDetailRepository.findById(purchaseOrderDetail.getId())
                .flatMap(existing -> {
                    PurchaseOrderDetail.update(existing, purchaseOrderDetail);
                    return purchaseOrderDetailRepository.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return purchaseOrderDetailRepository.deleteById(id);
    }

}
