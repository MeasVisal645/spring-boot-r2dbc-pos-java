package backend.Controller;

import backend.Entities.Expense;
import backend.Entities.ExpenseDto;
import backend.Service.ExpenseService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/expense")
@CrossOrigin("*")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/all")
    public Flux<Expense> findAll() {
        return expenseService.findAll();
    }

    @PostMapping("/create")
    public Mono<Expense> create(@RequestBody Expense expense) {
        return expenseService.create(expense);
    }

    @GetMapping
    public Mono<PageResponse<ExpenseDto>> findPagination(@RequestParam Integer pageNumber, Integer pageSize) {
        return expenseService.findPagination(pageNumber, pageSize);
    }
}
