package backend.Controller;

import backend.Dto.CategoryDto;
import backend.Entities.Category;
import backend.Service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/category")
@CrossOrigin("*")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    public Flux<CategoryDto> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Category> findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PostMapping("/create")
    public Mono<Category> create(@RequestBody Category category) {
        return categoryService.create(category);
    }

    @PutMapping("/update")
    public Mono<Category> update(@RequestBody Category category) {
        return categoryService.update(category);
    }
}
