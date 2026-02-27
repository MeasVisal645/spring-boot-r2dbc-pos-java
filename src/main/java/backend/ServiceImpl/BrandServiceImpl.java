package backend.ServiceImpl;

import backend.Dto.BrandDto;
import backend.Entities.Brand;
import backend.Entities.Category;
import backend.Mapper.BrandMapper;
import backend.Repository.BrandRepository;
import backend.Service.BrandService;
import backend.Utils.FilteredWithPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Mono<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public Mono<Brand> create(Brand brand) {
        return brandRepository.save(Brand.from(brand).build());
    }

    @Override
    public Mono<Brand> update(Brand brand) {
        return brandRepository.findById(brand.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found")))
                .flatMap(existingbrand -> {
                    Brand.update(existingbrand, brand);
                    return brandRepository.save(existingbrand);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return brandRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found")))
                .flatMap(brand -> brandRepository.deleteById(id));
    }

    @Override
    public Mono<PageResponse<BrandDto>> findPagination(Integer pageNumber, Integer pageSize, String search, Boolean isActive) {
        Criteria criteria = Criteria.empty();

        if (isActive != null) {
            criteria = criteria.and(Criteria.where(Brand.IS_ACTIVE_COLUMN).is(isActive));
        }

        if (search != null && !search.isEmpty()) {
            criteria = criteria.or(Criteria.where(Brand.NAME_COLUMN).like("%" + search + "%"));
        }

        return FilteredWithPaginationUtils.fetch(
                r2dbcEntityTemplate,
                Brand.class,
                criteria,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(Sort.Order.desc(Brand.NAME_COLUMN)),
                BrandMapper::toDto
        );
    }
}
