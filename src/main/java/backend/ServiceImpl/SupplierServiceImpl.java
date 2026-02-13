package backend.ServiceImpl;

import backend.Dto.SupplierDetails;
import backend.Entities.OrderItem;
import backend.Entities.Supplier;
import backend.Entities.SupplierContact;
import backend.Repository.SupplierRepository;
import backend.Service.SupplierService;
import backend.Utils.NestedPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Mono<PageResponse<SupplierDetails>> findPagination(Integer pageNumber, Integer pageSize) {
        return NestedPaginationUtils.fetchPagination(
                r2dbcEntityTemplate,
                Supplier.class,
                Supplier.IS_ACTIVE_COLUMN,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(Sort.Order.desc(Supplier.CREATED_DATE_COLUMN)),
                supplier -> r2dbcEntityTemplate.select(SupplierContact.class)
                        .matching(Query.query(Criteria.where(SupplierContact.SUPPLIER_ID_COLUMN).is(supplier.getId())))
                        .all()
                        .collectList(),
                SupplierDetails::new
        );
    }

    @Override
    public Flux<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Mono<Supplier> findById(Long id) {
        return supplierRepository.findById(id);
    }

    @Override
    public Mono<Supplier> create(Supplier supplier) {
        return supplierRepository.save(Supplier.from(supplier)
                        .isActive(true)
                        .createdDate(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public Mono<Supplier> update(Supplier supplier) {
        return supplierRepository.findById(supplier.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "supplier not found")))
                .flatMap( existingSupplier -> {
                    Supplier.update(existingSupplier, supplier)
                            .setUpdatedDate(LocalDateTime.now());
                    return supplierRepository.save(existingSupplier);
                });
    }

    @Override
    public Mono<Long> delete(Long id) {
        return supplierRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found")))
                .flatMap(category ->
                        supplierRepository.deleteById(id)
                                .thenReturn(id)
                );
    }


}
