package backend.Service;

import backend.Entities.Supplier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface SupplierService {

    Mono<Supplier> create(Supplier supplier);
}
