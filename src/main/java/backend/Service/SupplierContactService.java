package backend.Service;

import backend.Entities.SupplierContact;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface SupplierContactService {

    Mono<SupplierContact> create(SupplierContact supplierContact);
}
