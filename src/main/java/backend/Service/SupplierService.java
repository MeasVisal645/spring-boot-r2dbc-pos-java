package backend.Service;

import backend.Dto.SupplierDetails;
import backend.Dto.SupplierDto;
import backend.Entities.Supplier;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface SupplierService {

    Flux<SupplierDto> findAll();
    Mono<Supplier> findById(Long id);
    Mono<Supplier> create(Supplier supplier);
    Mono<Supplier> update(Supplier supplier);
    Mono<Long> delete(Long id);

    Mono<PageResponse<SupplierDetails>> findPagination(Integer pageNumber, Integer pageSize);
}
