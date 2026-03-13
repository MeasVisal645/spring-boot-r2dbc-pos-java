package backend.ServiceImpl;

import backend.Dto.CategoryDto;
import backend.Entities.Category;
import backend.Mapper.CategoryMapper;
import backend.Repository.CategoryRepository;
import backend.Service.CategoryService;
import backend.Utils.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.http.HttpStatus;
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
    private final RepositoryUtils repositoryUtils;

    @Override
    public Flux<CategoryDto> findAll() {
        return repositoryUtils.findAllActive(
                        r2dbcEntityTemplate,
                        Category.class,
                        Category.IS_ACTIVE_COLUMN,
                        Category.LABEL
                )
                .map(CategoryMapper::toDto);
    }

    @Override
    public Mono<ResponseDTO<Category>> findById(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id)
                ))
                .map(category -> new ResponseDTO<>(
                        HttpStatus.OK,
                        HttpStatus.OK.value(),
                        "Category retrieved successfully",
                        null,
                        category
                ));
    }

    @Override
    public Mono<ResponseDTO<Category>> create(Category category) {
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
                })
                .map(saved -> new ResponseDTO<>(
                        HttpStatus.CREATED,
                        HttpStatus.CREATED.value(),
                        "Category created successfully",
                        null,
                        saved
                ));
    }

    @Override
    public Mono<ResponseDTO<Category>> update(Category category) {
        return categoryRepository.findById(category.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")))
                .flatMap(existingCategory -> {
                    Category.update(existingCategory, category)
                            .setUpdatedDate(LocalDateTime.now());
                    return categoryRepository.save(existingCategory);
                })
                .map(updated -> new ResponseDTO<>(
                        HttpStatus.OK,
                        HttpStatus.OK.value(),
                        "Category updated successfully",
                        null,
                        updated
                ));
    }

    @Override
    public Mono<ResponseDTO<Long>> delete(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")))
                .flatMap(category ->
                        categoryRepository.deleteById(id)
                                .thenReturn(id)
                )
                .map(deletedId -> new ResponseDTO<>(
                        HttpStatus.OK,
                        HttpStatus.OK.value(),
                        "Category deleted successfully",
                        null,
                        deletedId
                ));
    }

    @Override
    public Mono<PageResponse<CategoryDto>> findPagination(Integer pageNumber, Integer pageSize, String search, Boolean isActive) {
        Criteria criteria = Criteria.empty();

        if (isActive != null) {
            criteria = criteria.and(Category.IS_ACTIVE_COLUMN).is(isActive);
        }

        if (search != null && !search.isBlank()) {
            criteria = criteria.and(Category.NAME_COLUMN).like("%" + search + "%");
        }

        return FilteredWithPaginationUtils.fetch(
                r2dbcEntityTemplate,
                Category.class,
                criteria,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(
                        Sort.Order.desc(Category.CREATED_DATE_COLUMN),
                        Sort.Order.desc(Category.UPDATED_DATE_COLUMN)
                ),
                CategoryMapper::toDto
        );
    }
}
