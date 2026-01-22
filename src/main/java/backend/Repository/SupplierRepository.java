package backend.Repository;

import backend.Entities.Supplier;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends R2dbcRepository<Supplier, Long> {
}
