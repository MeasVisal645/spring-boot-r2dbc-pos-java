package backend.ServiceImpl;

import backend.Dto.OrderDetails;
import backend.Dto.OrderRequest;
import backend.Dto.SalesData;
import backend.Entities.*;
import backend.Repository.OrderDetailRepository;
import backend.Repository.OrderItemRepository;
import backend.Service.OrderItemService;
import backend.Service.UserService;
import backend.Utils.NestedPaginationUtils;
import backend.Utils.OrderNoGenerator;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserService userService;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<OrderDetails> findAll() {
        return orderItemRepository.findAll()
                .flatMap(orderItem ->
                        orderDetailRepository.findByOrderId(orderItem.getId())
                                .collectList()
                                .map(details -> new OrderDetails(orderItem, details))
                );
    }

//    @Override
//    public Flux<OrderDetails> findByOrderNo(String orderNo) {
//        return orderItemRepository.findByOrderNo(orderNo)
//                .switchIfEmpty(Mono.error(
//                        new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found")
//                ))
//                .flatMap(orderItem ->
//                        orderDetailRepository.findByOrderId(orderItem.getId())
//                                .collectList()
//                                .map(orderDetails -> new OrderDetails(orderItem, orderDetails))
//                );
//    }

    @Override
    public Flux<SalesData> findAllSales(LocalDateTime startDate, LocalDateTime endDate) {

        return r2dbcEntityTemplate.select(OrderItem.class)
                .matching(Query.query(
                        Criteria.where(OrderItem.CREATED_DATE_COLUMN)
                                .between(startDate, endDate)
                                .and(OrderItem.IS_PAID_COLUMN).isTrue()
                ))
                .all()

                .flatMap(orderItem ->
                        r2dbcEntityTemplate.select(OrderDetail.class)
                                .matching(Query.query(
                                        Criteria.where(OrderDetail.ORDER_ID_COLUMN)
                                                .is(orderItem.getId())
                                ))
                                .all()
                                .map(OrderDetail::getTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .map(total ->
                                        new SalesData(total, orderItem.getCreatedDate())
                                )
                );

    }




    @Override
    public Mono<PageResponse<OrderDetails>> findPagination(Integer pageNumber, Integer pageSize) {
        return NestedPaginationUtils.fetchPagination(
                r2dbcEntityTemplate,
                OrderItem.class,
                OrderItem.IS_PAID_COLUMN,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(Sort.Order.desc(OrderItem.CREATED_DATE_COLUMN)),
                orderItem -> r2dbcEntityTemplate.select(OrderDetail.class)
                        .matching(Query.query(
                                Criteria.where(OrderDetail.ORDER_ID_COLUMN)
                                        .is(orderItem.getId())
                        ))
                        .all()
                        .collectList(),
                OrderDetails::new
        );
    }

    @Override
    public Mono<List<OrderDetail>> createOrder(List<OrderRequest> orderRequests) {
        var orderNo = OrderNoGenerator.generateOrderNo();

        // Fetch current user ID
        return userService.currentUser()
            .flatMap(userId ->
                // Create OrderItem
                orderItemRepository.save(
                    OrderItem.builder()
                            .orderNo(orderNo)
                            .userId(userId)
                            .isPaid(true)
                            .createdDate(LocalDateTime.now())
                            .build()
                )
                .flatMap(orderItem ->
                    Flux.fromIterable(orderRequests)
                        .flatMap(request ->
                            r2dbcEntityTemplate.select(Product.class)
                                .matching(Query.query(
                                        Criteria.where(Product.CODE_COLUMN).is(request.code())))
                                .one()
                                .switchIfEmpty(Mono.error(
                                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Product code not found")))
                                .flatMap(product -> {
                                    if (product.getQuantity() < request.quantity()) {
                                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock"));
                                    }

                                    // Calculate total price
                                    BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.quantity()));
                                    // Create OrderDetail
                                    OrderDetail detail = OrderDetail.builder()
                                            .orderId(orderItem.getId())
                                            .productId(product.getId())
                                            .customerId(request.customerId())
                                            .paymentMethod(request.paymentMethod().getValue())
                                            .quantity(request.quantity())
                                            .total(totalPrice)
                                            .build();

                                    return orderDetailRepository.save(detail)
                                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer ID not found")))
                                            .flatMap(saved ->
                                                // Update Product Quantity After OrderDetail is saved
                                                r2dbcEntityTemplate.update(Product.class)
                                                    .matching(Query.query(Criteria.where(Product.CODE_COLUMN).is(request.code())))
                                                    .apply(Update.update(Product.QUANTITY_COLUMN,
                                                        product.getQuantity() - request.quantity()))
                                                    .thenReturn(saved)
                                            );
                                })
                        )
                        .collectList()
                )
            );
    }


}
