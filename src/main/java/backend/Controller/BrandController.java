package backend.Controller;

import backend.Entities.Brand;
import backend.Service.BrandService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/brand")
@CrossOrigin("*")
@AllArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/all")
    public Flux<Brand> findAll() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Brand> findById(@PathVariable Long id) {
        return brandService.findById(id);
    }

    @GetMapping
    public Mono<PageResponse<Brand>> findPagination(@RequestParam Integer pageNumber, Integer pageSize) {
        return brandService.findPagination(pageNumber, pageSize);
    }

    @PostMapping("/create")
    public Mono<Brand> create(@RequestBody Brand brand) {
        return brandService.create(brand);
    }

    @PutMapping("/update")
    public Mono<Brand> update(@RequestBody Brand brand) {
        return brandService.update(brand);
    }

    @DeleteMapping("/delete")
    public Mono<Void> delete(@PathVariable Long id) {
        return brandService.delete(id);
    }
}
