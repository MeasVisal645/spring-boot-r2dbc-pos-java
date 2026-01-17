package backend.Service;

import backend.Entities.Expense;
import backend.Entities.ExpenseDto;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface ExpenseService {

    Flux<Expense> findAll();
    Mono<Expense> create(Expense expense);
    Mono<PageResponse<ExpenseDto>> findPagination(Integer pageNumber, Integer pageSize);
}
