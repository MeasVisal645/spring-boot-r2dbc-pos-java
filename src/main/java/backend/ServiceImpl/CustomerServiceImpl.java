package backend.ServiceImpl;

import backend.Dto.CustomerDto;
import backend.Entities.Brand;
import backend.Entities.Category;
import backend.Entities.Customer;
import backend.Mapper.CustomerMapper;
import backend.Repository.CustomerRepository;
import backend.Service.CustomerService;
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

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<CustomerDto> findAll() {
        return customerRepository.findAll()
                .map(CustomerMapper::toDto);
    }

    @Override
    public Mono<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Mono<Customer> create(Customer customer) {
        return customerRepository.save(customer.from(customer)
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .build()
        );
    }

    @Override
    public Mono<Customer> update(Customer customer) {
        return customerRepository.findById(customer.getId())
                .map(existingCustomer -> {
                    Customer.update(existingCustomer, customer)
                            .setUpdatedDate(LocalDateTime.now());
                    return existingCustomer;
                });
    }

    @Override
    public Mono<Long> delete(Long id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .flatMap(customer ->
                        customerRepository.deleteById(id)
                                .thenReturn(id)
                );
    }

    @Override
    public Mono<PageResponse<CustomerDto>> findPagination(Integer pageNumber, Integer pageSize, String search, Boolean isActive) {
        Criteria criteria = Criteria.empty();

        if (isActive != null) {
            criteria = criteria.and(Customer.IS_ACTIVE_COLUMN).is(isActive);
        }

        if (search != null && !search.isBlank()) {
            criteria = criteria
                    .or(Customer.NAME_COLUMN).like("%" + search + "%")
                    .or(Customer.PHONE_COLUMN).like("%" + search + "%")
                    .or(Customer.EMAIL_COLUMN).like("%" + search + "%");
        }

        return FilteredWithPaginationUtils.fetch(
                r2dbcEntityTemplate,
                Customer.class,
                criteria,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(
                        Sort.Order.desc(Category.CREATED_DATE_COLUMN),
                        Sort.Order.desc(Category.UPDATED_DATE_COLUMN)
                ),
                CustomerMapper::toDto
        );
    }
}
