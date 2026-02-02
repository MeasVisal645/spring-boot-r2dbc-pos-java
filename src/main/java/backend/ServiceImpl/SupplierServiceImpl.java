package backend.ServiceImpl;

import backend.Dto.SupplierDetails;
import backend.Entities.Supplier;
import backend.Entities.SupplierContact;
import backend.Repository.SupplierRepository;
import backend.Service.SupplierService;
import backend.Utils.NestedPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
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

                supplier -> r2dbcEntityTemplate.select(SupplierContact.class)
                        .matching(Query.query(Criteria.where(SupplierContact.SUPPLIER_ID_COLUMN).is(supplier.getId())))
                        .all()
                        .collectList(),
                SupplierDetails::new
        );
    }

    @Override
    public Mono<Supplier> create(Supplier supplier) {
        return supplierRepository.save(Supplier.from(supplier)
                        .isActive(true)
                        .createdDate(LocalDateTime.now())
                        .build()
        );
    }
}
