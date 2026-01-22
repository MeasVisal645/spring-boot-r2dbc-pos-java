package backend.ServiceImpl;

import backend.Entities.Supplier;
import backend.Entities.SupplierContact;
import backend.Repository.SupplierContactRepository;
import backend.Repository.SupplierRepository;
import backend.Service.SupplierContactService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SupplierContactServiceImpl implements SupplierContactService {

    private final SupplierContactRepository supplierContactRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public Mono<SupplierContact> create(SupplierContact supplierContact) {
        return supplierRepository.findById(supplierContact.getSupplierId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found")))
                .flatMap(supplier -> supplierContactRepository.save(
                        SupplierContact.from(supplierContact)
                                .isActive(true)
                                .createdDate(LocalDateTime.now())
                                .build()
                ));
    }
}
