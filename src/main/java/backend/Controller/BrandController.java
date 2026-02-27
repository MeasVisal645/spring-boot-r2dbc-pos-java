package backend.Controller;

import backend.Dto.BrandDto;
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
    public Mono<PageResponse<BrandDto>> findPagination(
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive
    ) {
        return brandService.findPagination(
                pageNumber,
                pageSize,
                search,
                isActive
        );
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
