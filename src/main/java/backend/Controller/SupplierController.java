package backend.Controller;

import backend.Entities.Supplier;
import backend.Service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/supplier")
@CrossOrigin("*")
@AllArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping("/create")
    public Mono<Supplier> create(@RequestBody Supplier supplier) {
        return supplierService.create(supplier);
    }
}
