package backend.ServiceImpl;

import backend.Dto.ProductDto;
import backend.Entities.Product;
import backend.Mapper.ProductMapper;
import backend.Repository.BrandRepository;
import backend.Repository.CategoryRepository;
import backend.Repository.ProductRepository;
import backend.Service.ProductService;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import backend.Utils.RepositoryUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final RepositoryUtils repositoryUtils;

    @Override
    public Flux<ProductDto> findAll() {
        return productRepository.findAll()
                .map(ProductMapper::toDto);

//        return repositoryUtils.findAllActive(r2dbcEntityTemplate,
//                                            Product.class,
//                                            Product.IS_ACTIVE_COLUMN,
//                                            Product.LABEL)
//                .map(ProductMapper::toDto);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return repositoryUtils.findById(r2dbcEntityTemplate,
                                        Product.class,
                                        Product.ID_COLUMN,
                                        id,
                                        Product.IS_ACTIVE_COLUMN,
                                        Product.LABEL);
    }

    @Override
    public Mono<Product> create(Product product) {
        return categoryRepository.findById(product.getCategoryId())  //Check category exists
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Category not found"
                        )
                ))
                .flatMap(category ->
                        productRepository.existsByCode(product.getCode())  //Check product code exists
                )
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Product code already exists"
                                )
                        );
                    }

                    return productRepository.save(
                            Product.from(product)
                                    .isActive(true)
                                    .createdDate(LocalDateTime.now())
                                    .build()
                    );
                });
    }


    @Override
    public Mono<Product> update(Product product) {
        return categoryRepository.findById(product.getCategoryId())
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found")
                ))
                .flatMap(category ->
                        productRepository.findById(product.getId()))
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found")
                ))
                .flatMap(existingProduct -> {
                    Product.update(existingProduct, product);
                    existingProduct.setUpdatedDate(LocalDateTime.now());
                    return productRepository.save(existingProduct);
                });
    }

    @Override
    public Mono<Long> delete(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .flatMap(product ->
                            productRepository.deleteById(id)
                                    .thenReturn(id)
                        );
    }

    //check existing for generating
    @Override
    public Mono<Boolean> existsByProductCode(String code) {
        return r2dbcEntityTemplate.select(Product.class)
                .matching(Query.query(Criteria.where(Product.CODE_COLUMN).is(code)))
                .exists();
    }

    @Override
    public Mono<PageResponse<ProductDto>> findPagination(Integer pageNumber, Integer pageSize) {
        return PaginationUtils.fetchPagedResponse(
                r2dbcEntityTemplate,
                Product.class,
                ProductMapper::toDto,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Product.IS_ACTIVE_COLUMN,
                Sort.by(Sort.Order.desc(Product.CREATED_DATE_COLUMN),
                        Sort.Order.desc(Product.UPDATED_DATE_COLUMN))
        );
    }

    @Override
    public Mono<Product> addQuantity(Long id, int addQuantity) {
        return r2dbcEntityTemplate.select(Product.class)
                .matching(Query.query(Criteria.where(Product.ID_COLUMN).is(id)))
                .one()
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .flatMap( product -> {
                    int newQuantity = product.getQuantity() + addQuantity;
                    product.setQuantity(newQuantity);
                    return r2dbcEntityTemplate.update(Product.class)
                            .matching(Query.query(Criteria.where(Product.ID_COLUMN).is(id)))
                            .apply(Update.update(Product.QUANTITY_COLUMN, newQuantity)
                                    .set(Product.UPDATED_DATE_COLUMN, LocalDateTime.now()))
                                    .thenReturn(product);
                });
    }

}
