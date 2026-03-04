package backend.Controller;

import backend.Entities.PurchaseOrderDetail;
import backend.Service.PurchaseOrderDetailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/purchase/detail")
@CrossOrigin("*")
public class PurchaseOrderDetailController {

    private final PurchaseOrderDetailService purchaseOrderDetailService;

    @GetMapping("/all")
    public Flux<PurchaseOrderDetail> findAll() {
        return purchaseOrderDetailService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<PurchaseOrderDetail> findById(@PathVariable Long id) {
        return purchaseOrderDetailService.findById(id);
    }

    @PostMapping("/create")
    public Mono<PurchaseOrderDetail> create(@RequestBody PurchaseOrderDetail purchaseOrderDetail) {
        return purchaseOrderDetailService.create(purchaseOrderDetail);
    }

    @PutMapping("/update")
    public Mono<PurchaseOrderDetail> update(@RequestBody PurchaseOrderDetail purchaseOrderDetail) {
        return purchaseOrderDetailService.update(purchaseOrderDetail);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return purchaseOrderDetailService.delete(id);
    }
}
