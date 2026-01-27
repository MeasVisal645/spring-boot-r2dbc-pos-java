package backend.ServiceImpl;

import backend.Dto.CategoryDto;
import backend.Dto.CategoryProduct;
import backend.Entities.Category;
import backend.Entities.Product;
import backend.Mapper.CategoryMapper;
import backend.Repository.CategoryRepository;
import backend.Service.CategoryService;
import backend.Utils.NestedPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<CategoryDto> findAll() {
        return categoryRepository.findAll()
                .map(CategoryMapper::toDto);
    }

    @Override
    public Mono<Category> findById(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Category not found with id: " + id
                        )
                ));
    }

    @Override
    public Mono<Category> create(Category category) {
        return categoryRepository.existsByCode(category.getCode())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Category code already exists!"
                        ));
                    }

                    return categoryRepository.save(
                            Category.from(category)
                                    .createdDate(LocalDateTime.now())
                                    .isActive(true)
                                    .build()
                    );
                });
    }


    @Override
    public Mono<Category> update(Category category) {
        return categoryRepository.findById(category.getId())
                .flatMap(existingCategory -> {
                    Category.update(existingCategory)
                            .setUpdatedDate(LocalDateTime.now());
                    return categoryRepository.save(existingCategory);
                });
    }

    @Override
    public Mono<Long> delete(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")))
                .flatMap(category ->
                        categoryRepository.deleteById(id)
                                .thenReturn(id)
                );
    }

    @Override
    public Mono<PageResponse<CategoryDto>> findPagination(Integer pageNumber, Integer pageSize) {
        return PaginationUtils.fetchPagedResponse(
                r2dbcEntityTemplate,
                Category.class,
                CategoryMapper::toDto,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Category.IS_ACTIVE_COLUMN,
                Sort.by(Sort.Order.desc(Category.CREATED_DATE_COLUMN),
                        Sort.Order.desc(Category.UPDATED_DATE_COLUMN))
        );
    }

    @Override
    public Mono<PageResponse<CategoryProduct>> findNestedPagination(Integer pageNumber, Integer pageSize) {
        return NestedPaginationUtils.fetchPagination(
                r2dbcEntityTemplate,
                Category.class,
                Category.IS_ACTIVE_COLUMN,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                category -> r2dbcEntityTemplate.select(Product.class)
                        .matching(Query.query(Criteria.where(Product.CATEGORY_ID_COLUMN).is(category.getId())))
                        .all()
                        .collectList(),
                CategoryProduct::new
        );
    }
}
