package backend.Controller;

import backend.Dto.AddQuantity;
import backend.Dto.EmployeeDto;
import backend.Dto.ProductDto;
import backend.Entities.Product;
import backend.Repository.ProductRepository;
import backend.Service.ProductService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/product")
@CrossOrigin("*")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public Flux<ProductDto> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Product> findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping("/create")
    public Mono<Product> create(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/update")
    public Mono<Product> update(@RequestBody Product product) {
        return productService.update(product);
    }

    @DeleteMapping("/delete")
    public Mono<Void> delete(@RequestParam Long id) {
        return productService.delete(id);
    }


    // Additional Method
    //Check existing for generating
    @GetMapping("/check-code/{code}")
    public Mono<Boolean> checkCodeExists(@PathVariable String code) {
        return productService.existsByProductCode(code);
    }

    //Pagination
    @GetMapping
    public Mono<PageResponse<ProductDto>> findPagination(
            @RequestParam("pageNumber") Integer pageNumber, Integer pageSize) {
        return productService.findPagination(pageNumber, pageSize);

    }

    @PutMapping("/add-quantity/{id}")
    public Mono<Product> addQuantity(@PathVariable Long id, @RequestBody AddQuantity quantity) {
        return productService.addQuantity(id, quantity);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Product> createWithImage(
            @RequestPart("data") Mono<Product> data,
            @RequestPart(value = "image", required = false) Mono<FilePart> image
    ) {
        return data.flatMap(dto -> productService.createWithImage(dto, image));
    }

    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> updateImage(@PathVariable Long id, @RequestPart("file")FilePart file) {
        return productService.updateImage(id, file);
    }
}
