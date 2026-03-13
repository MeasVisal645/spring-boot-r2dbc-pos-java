package backend.Service;

import backend.Dto.CategoryDto;
import backend.Dto.CategoryProduct;
import backend.Entities.Category;
import backend.Utils.PageResponse;
import backend.Utils.ResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface CategoryService {

    Flux<CategoryDto> findAll();
    Mono<ResponseDTO<Category>> findById(Long id);
    Mono<ResponseDTO<Category>> create(Category category);
    Mono<ResponseDTO<Category>> update(Category category);
    Mono<ResponseDTO<Long>> delete(Long id);

    Mono<PageResponse<CategoryDto>> findPagination(Integer pageNumber, Integer pageSize, String search, Boolean isActive);
}
