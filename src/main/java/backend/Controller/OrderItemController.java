package backend.Controller;

import backend.Dto.OrderDetails;
import backend.Dto.OrderRequest;
import backend.Dto.SalesData;
import backend.Entities.OrderDetail;
import backend.Entities.OrderItem;
import backend.Service.OrderItemService;
import backend.Utils.PageResponse;
import backend.Utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/order")
@CrossOrigin("*")
public class OrderItemController {

    private final OrderItemService orderItemService;


    @GetMapping("/all")
    public Flux<OrderDetails> findAll() {
        return orderItemService.findAll();
    }

//    @GetMapping("/{orderNo}")
//    public Flux<OrderDetails> findByOrderNo(@RequestParam String orderNo) {
//        return orderItemService.findByOrderNo(orderNo);
//    }

    @GetMapping("/date")
    public Flux<SalesData> findAllSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return orderItemService.findAllSales(startDate, endDate);
    }


    @GetMapping
    public Mono<PageResponse<OrderDetails>> findPagination(@RequestParam Integer pageNumber, Integer pageSize) {
        return orderItemService.findPagination(pageNumber, pageSize);
    }

    @PostMapping("/create")
    public Mono<List<OrderDetail>> create(@RequestBody List<OrderRequest> orderRequest) {
        return orderItemService.createOrder(orderRequest);
    }

}
