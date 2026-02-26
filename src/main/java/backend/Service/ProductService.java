package backend.Service;

import backend.Dto.AddQuantity;
import backend.Dto.EmployeeDto;
import backend.Dto.ProductDto;
import backend.Entities.Product;
import backend.Utils.PageResponse;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface ProductService {

    Flux<ProductDto> findAll();
    Mono<Product> findById(Long id);
    Mono<Product> create(Product product);
    Mono<Product> update(Product product);
    Mono<Void> delete(Long id);

    //check existing for generating
    Mono<Boolean> existsByProductCode(String productCode);
    Mono<PageResponse<ProductDto>> findPagination(Integer pageNumber, Integer pageSize);
    Mono<Product> addQuantity(Long id, AddQuantity dto);
    Mono<Product> createWithImage(Product product, Mono<FilePart> file);
    Mono<String> updateImage(Long id, FilePart file);
}
