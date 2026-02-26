package backend.ServiceImpl;

import backend.Dto.AddQuantity;
import backend.Dto.EmployeeDto;
import backend.Dto.ProductDto;
import backend.Entities.Method;
import backend.Entities.Product;
import backend.Entities.QuantityAdjustment;
import backend.Mapper.ProductMapper;
import backend.Repository.BrandRepository;
import backend.Repository.CategoryRepository;
import backend.Repository.EmployeeRepository;
import backend.Repository.ProductRepository;
import backend.Service.FileService;
import backend.Service.ProductService;
import backend.Service.UserService;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import backend.Utils.RepositoryUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final FileService fileService;
    private final String publicUrl;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, UserService userService, FileService fileService,@Value("${r2.publicUrl}") String publicUrl, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.fileService = fileService;
        this.publicUrl = publicUrl;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }


    @Override
    public Flux<ProductDto> findAll() {
        return productRepository.findAll()
                .map(ProductMapper::toDto);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id);
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
    public Mono<Void> delete(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .flatMap( product -> {
                    String oldKey = product.getImageKey();

                    Mono<Void> deleteOld = (oldKey == null || oldKey.isBlank())
                            ? Mono.empty()
                            : fileService.deleteFile(oldKey)
                    .onErrorResume(e -> Mono.empty());

                    return deleteOld
                            .then(productRepository.deleteById(id));
                    });
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
                Criteria.where(Product.IS_ACTIVE_COLUMN).isTrue(),
                Sort.by(Sort.Order.desc(Product.CREATED_DATE_COLUMN))
        );
    }

    @Override
    public Mono<Product> addQuantity(Long id, AddQuantity dto) {
        if (dto == null || dto.method() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Method is required"));
        }
        if (dto.addQuantity() <= 0) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be > 0"));
        }

        return userService.currentUser()
                .flatMap(userId ->
                        r2dbcEntityTemplate.select(Product.class)
                                .matching(Query.query(Criteria.where(Product.ID_COLUMN).is(id)))
                                .one()
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                                .flatMap(product -> {
                                    int currentQty = product.getQuantity();
                                    int delta = dto.method() == Method.ADD ? dto.addQuantity() : -dto.addQuantity();
                                    int newQty = currentQty + delta;

                                    if (newQty < 0) {
                                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock"));
                                    }

                                    Query updateQuery = Query.query(Criteria.where(Product.ID_COLUMN).is(id).and(Product.QUANTITY_COLUMN).is(currentQty));

                                    return r2dbcEntityTemplate.update(Product.class)
                                            .matching(updateQuery)
                                            .apply(Update.update(Product.QUANTITY_COLUMN, newQty)
                                                    .set(Product.UPDATED_DATE_COLUMN, LocalDateTime.now()))
                                            .flatMap(rows -> {
                                                if (rows == 0) {
                                                    return Mono.error(new ResponseStatusException(
                                                            HttpStatus.CONFLICT,
                                                            "Quantity was changed by another request. Please retry."
                                                    ));
                                                }

                                                QuantityAdjustment qa = new QuantityAdjustment();
                                                qa.setProductName(product.getName());
                                                qa.setUserId(userId);
                                                qa.setMethod(dto.method());
                                                qa.setQuantity(dto.addQuantity());
                                                qa.setComplete(true);
                                                qa.setCreatedDate(LocalDateTime.now());

                                                product.setQuantity(newQty);
                                                product.setUpdatedDate(LocalDateTime.now());

                                                return r2dbcEntityTemplate.insert(QuantityAdjustment.class)
                                                        .using(qa)
                                                        .thenReturn(product);
                                            });
                                })
                );
    }

    @Override
    public Mono<Product> createWithImage(Product product, Mono<FilePart> file) {
        product.setActive(true);
        product.setCreatedDate(LocalDateTime.now());

        return productRepository.save(product)
                .flatMap(products -> file.flatMap(
                        filePart -> {
                            String imageKey = "product/" + product.getId();
                            String imageUrl = publicUrl + "/" + imageKey;

                            return fileService.uploadFile(imageKey, filePart)
                                    .then(Mono.defer(() -> {
                                        product.setImageKey(imageKey);
                                        product.setImageUrl(imageUrl);
                                        return productRepository.save(product);
                                    }))
                                    .onErrorResume(e -> fileService.deleteFile(imageKey)
                                            .then(Mono.error(e)));
                        })
                        .switchIfEmpty(Mono.just(product))
                );
    }

    @Override
    public Mono<String> updateImage(Long id, FilePart file) {
        String imageKey = "product/" + id;
        String imageUrl = publicUrl + "/" + imageKey;

        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .flatMap(product -> {
                    String oldKey = product.getImageKey();

                    Mono<Void> deleteOld = (oldKey == null || oldKey.isBlank())
                            ? Mono.empty()
                            : fileService.deleteFile(oldKey)
                            .onErrorResume(e -> Mono.empty());

                    return fileService.uploadFile(imageKey, file)
                            .then(deleteOld)
                            .then(Mono.defer(() -> {
                                product.setImageKey(imageKey);
                                product.setImageUrl(imageUrl);
                                return productRepository.save(product);
                            }));
                })
                .thenReturn("Uploaded");
    }
}
