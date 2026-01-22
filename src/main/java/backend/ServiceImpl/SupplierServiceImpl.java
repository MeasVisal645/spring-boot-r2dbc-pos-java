package backend.ServiceImpl;

import backend.Entities.Supplier;
import backend.Repository.SupplierRepository;
import backend.Service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public Mono<Supplier> create(Supplier supplier) {
        return supplierRepository.save(Supplier.from(supplier)
                        .isActive(true)
                        .createdDate(LocalDateTime.now())
                        .build()
        );
    }
}
