package backend.Controller;

import backend.Dto.PurchaseOrderDetails;
import backend.Dto.PurchaseOrderRequest;
import backend.Dto.PurchaseOrderStatus;
import backend.Entities.PurchaseOrder;
import backend.Entities.PurchaseOrderDetail;
import backend.Entities.Status;
import backend.Service.PurchaseOrderDetailService;
import backend.Service.PurchaseOrderService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/purchase/order")
@CrossOrigin("*")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/all")
    public Flux<PurchaseOrder> findAll() {
        return purchaseOrderService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<PurchaseOrder> findById(@PathVariable Long id) {
        return purchaseOrderService.findById(id);
    }

    @PostMapping("/create")
    public Mono<List<PurchaseOrderDetail>> createPurchaseOrder(@RequestBody PurchaseOrderRequest request) {
        return purchaseOrderService.create(request);
    }

    @PutMapping("/update")
    public Mono<PurchaseOrder> updatePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        return purchaseOrderService.update(purchaseOrder);
    }

    @PutMapping("/{id}/status")
    public Mono<PurchaseOrder> updateStatus(@PathVariable Long id, @RequestBody PurchaseOrderStatus status) {
        return purchaseOrderService.updateStatus(id, status.status());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deletePurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.delete(id);
    }

    @GetMapping
    public Mono<PageResponse<PurchaseOrderDetails>> findPagination(
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String status) {
        return purchaseOrderService.findPagination(
                pageNumber,
                pageSize,
                startDate,
                endDate,
                status);
    }
}
