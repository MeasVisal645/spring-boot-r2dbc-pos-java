package backend.Controller;

import backend.Dto.CustomerDto;
import backend.Entities.Customer;
import backend.Repository.CustomerRepository;
import backend.Service.CustomerService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/customer")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/all")
    public Flux<CustomerDto> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Customer> findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PostMapping("/create")
    public Mono<Customer> create(@RequestBody Customer customer) {
        return customerService.create(customer);
    }

    @PutMapping("/update")
    public Mono<Customer> update(@RequestParam Customer customer) {
        return customerService.update(customer);
    }

    @DeleteMapping("/{id}")
    public Mono<Long> delete(@PathVariable Long id) {
        return customerService.delete(id);
    }

    @GetMapping
    public Mono<PageResponse<CustomerDto>> findPagination(@RequestParam Integer pageNumber, Integer pageSize) {
        return customerService.findPagination(pageNumber, pageSize);
    }

}
