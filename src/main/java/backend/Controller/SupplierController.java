package backend.Controller;

import backend.Dto.SupplierDetails;
import backend.Dto.SupplierDto;
import backend.Entities.Supplier;
import backend.Service.SupplierService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/supplier")
@CrossOrigin("*")
@AllArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/all")
    public Flux<SupplierDto> findAll() {
        return supplierService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Supplier> findById(@PathVariable Long id) {
        return supplierService.findById(id);
    }

    @PostMapping("/create")
    public Mono<Supplier> create(@RequestBody Supplier supplier) {
        return supplierService.create(supplier);
    }

    @PutMapping("/update")
    public Mono<Supplier> update(@RequestBody Supplier supplier) {
        return supplierService.update(supplier);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Long> delete(@PathVariable Long id) {
        return supplierService.delete(id);
    }

    @GetMapping
    public Mono<PageResponse<SupplierDetails>> findPagination(@RequestParam Integer pageNumber, Integer pageSize) {
        return supplierService.findPagination(pageNumber, pageSize);
    }


}
