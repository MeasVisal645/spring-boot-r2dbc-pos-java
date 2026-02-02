package backend.Service;

import backend.Dto.SupplierDetails;
import backend.Entities.Supplier;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface SupplierService {

    Mono<PageResponse<SupplierDetails>> findPagination(Integer pageNumber, Integer pageSize);
    Mono<Supplier> create(Supplier supplier);
}
