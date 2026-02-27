package backend.ServiceImpl;

import backend.Dto.OrderDetails;
import backend.Dto.PurchaseOrderDetails;
import backend.Dto.PurchaseOrderRequest;
import backend.Dto.PurchaseOrderStatus;
import backend.Entities.*;
import backend.Repository.OrderDetailRepository;
import backend.Repository.PurchaseOrderDetailRepository;
import backend.Repository.PurchaseOrderRepository;
import backend.Repository.SupplierRepository;
import backend.Service.PurchaseOrderService;
import backend.Utils.FilteredWithNestedPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Override
    public Flux<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Mono<PurchaseOrder> findById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    public Mono<List<PurchaseOrderDetail>> create(PurchaseOrderRequest req) {
        if (req.items() == null || req.items().isEmpty()) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Items list cannot be empty"));
        }
        // Check exist
        Mono<Void> supplierExists =
                r2dbcEntityTemplate.select(Supplier.class)
                        .matching(Query.query(Criteria.where(Supplier.ID_COLUMN).is(req.supplierId())))
                        .one()
                        .switchIfEmpty(Mono.error(
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found")))
                        .then();

        // Save PurchaseOrder first
        Mono<PurchaseOrder> savedOrder =
                supplierExists.then(
                        purchaseOrderRepository.save(
                                PurchaseOrder.builder()
                                        .supplierId(req.supplierId())
                                        .status(req.status())
                                        .orderDate(req.orderDate())
                                        .createdDate(LocalDateTime.now())
                                        .build()
                        )
                );

        // Save PurchaseOrderDetail
        return savedOrder.flatMapMany(order ->
                        Flux.fromIterable(req.items())
                                .map(item -> {
                                    if (item.quantity() <= 0) {
                                        throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST, "Quantity must be > 0");
                                    }
                                    if (item.unitPrice() == null || item.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
                                        throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST, "Unit price must be >= 0");
                                    }

                                    BigDecimal total =
                                            item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));

                                    return PurchaseOrderDetail.builder()
                                            .purchaseId(order.getId())
                                            .productId(item.productId())
                                            .quantity(item.quantity())
                                            .unitPrice(item.unitPrice())
                                            .totalPrice(total)
                                            .build();
                                })
                                .flatMap(purchaseOrderDetailRepository::save)
                )
                .collectList();
    }

    @Override
    public Mono<PurchaseOrder> update(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.findById(purchaseOrder.getId())
                .flatMap(existing -> {
                    PurchaseOrder.update(existing, purchaseOrder);
                    existing.setUpdatedDate(LocalDateTime.now());
                    return purchaseOrderRepository.save(existing);
                });
    }

    @Override
    public Mono<PurchaseOrder> updateStatus(Long id, Status status) {
        return r2dbcEntityTemplate.update(PurchaseOrder.class)
                .matching(Query.query(Criteria.where(PurchaseOrder.ID_COLUMN).is(id)))
                .apply(Update.update(PurchaseOrder.STATUS_COLUMN, status.getValue()))
                .flatMap(rows -> {
                    if (rows == 0) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order Not Found"));
                    }
                    return r2dbcEntityTemplate.select(PurchaseOrder.class)
                            .matching(Query.query(Criteria.where(PurchaseOrder.ID_COLUMN).is(id)))
                            .one();
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return purchaseOrderRepository.deleteById(id)
                .then(purchaseOrderDetailRepository.deleteByPurchaseId(id));
    }

    @Override
    public Mono<PageResponse<PurchaseOrderDetails>> findPagination(Integer pageNumber, Integer pageSize, LocalDate startDate, LocalDate endDate, String status) {
        Criteria criteria = Criteria.empty();

        if (status != null) {
            criteria = criteria.and(PurchaseOrder.STATUS_COLUMN).is(status);
        }

        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
            criteria = criteria.and(PurchaseOrder.ORDER_DATE_COLUMN)
                    .between(startDateTime, endDateTime);
        }

        return FilteredWithNestedPaginationUtils.fetch(
                r2dbcEntityTemplate,
                PurchaseOrder.class,
                criteria,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(Sort.Order.desc(PurchaseOrder.ORDER_DATE_COLUMN)),
                purchaseOrder -> r2dbcEntityTemplate.select(PurchaseOrderDetail.class)
                        .matching(Query.query(
                                Criteria.where(PurchaseOrderDetail.PURCHASE_ID_COLUMN)
                                        .is(purchaseOrder.getId())
                        ))
                        .all()
                        .collectList(),
                PurchaseOrderDetails::new
        );
    }
}
