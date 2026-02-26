package backend.Service;

import backend.Dto.CategoryDto;
import backend.Dto.CategoryProduct;
import backend.Entities.Category;
import backend.Utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface CategoryService {

    Flux<CategoryDto> findAll();
    Mono<Category> findById(Long id);
    Mono<Category> create(Category category);
    Mono<Category> update(Category category);
    Mono<Long> delete(Long id);

    Mono<PageResponse<CategoryDto>> findPagination(Integer pageNumber, Integer pageSize);
}
