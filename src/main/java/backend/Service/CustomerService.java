package backend.Service;

import backend.Dto.CustomerDto;
import backend.Entities.Customer;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface CustomerService {

    Flux<CustomerDto> findAll();
    Mono<Customer> findById(Long id);
    Mono<Customer> create(Customer customer);
    Mono<Customer> update(Customer customer);
    Mono<Long> delete(Long id);

    Mono<PageResponse<CustomerDto>> findPagination(Integer pageNumber, Integer pageSize, String search, Boolean isActive);
}
