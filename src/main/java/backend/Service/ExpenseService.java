package backend.Service;

import backend.Entities.Expense;
import backend.Dto.ExpenseDto;
import backend.Utils.PageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public interface ExpenseService {

    Flux<Expense> findAll();
    Mono<Expense> findById(Long id);
    Mono<Expense> create(Expense expense);
    Mono<Expense> update(Expense expense);
    Mono<Void> delete(Long id);
    Mono<PageResponse<ExpenseDto>> findPagination(Integer pageNumber, Integer pageSize, LocalDate startDate, LocalDate endDate, String search);
}
