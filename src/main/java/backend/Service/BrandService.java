package backend.Service;

import backend.Entities.Brand;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface BrandService {

    Flux<Brand> findAll();
    Mono<Brand> findById(Long id);
    Mono<Brand> create(Brand brand);
    Mono<Brand> update(Brand brand);
    Mono<Void> delete(Long id);
    Mono<PageResponse<Brand>> findPagination(Integer pageNumber, Integer pageSize);
}
