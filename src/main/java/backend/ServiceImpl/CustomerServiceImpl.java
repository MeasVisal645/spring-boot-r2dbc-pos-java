package backend.ServiceImpl;

import backend.Dto.CustomerDto;
import backend.Entities.Brand;
import backend.Entities.Customer;
import backend.Mapper.CustomerMapper;
import backend.Repository.CustomerRepository;
import backend.Service.CustomerService;
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
    public Mono<PageResponse<CustomerDto>> findPagination(Integer pageNumber, Integer pageSize) {
        return PaginationUtils.fetchPagedResponse(
                r2dbcEntityTemplate,
                Customer.class,
                CustomerMapper::toDto,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Criteria.where(Customer.IS_ACTIVE_COLUMN).isTrue(),
                Sort.by(Sort.Order.desc(Customer.CREATED_DATE_COLUMN),
                        Sort.Order.desc(Customer.UPDATED_DATE_COLUMN))
        );
    }
}
