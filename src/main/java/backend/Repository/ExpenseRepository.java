package backend.Repository;

import backend.Entities.Expense;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends R2dbcRepository<Expense, Long> {
}
