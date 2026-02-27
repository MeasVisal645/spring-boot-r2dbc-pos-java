package backend.Controller;

import backend.Entities.Expense;
import backend.Dto.ExpenseDto;
import backend.Service.ExpenseService;
import backend.Utils.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

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

    @GetMapping("/{id}")
    public Mono<Expense> findById(@PathVariable Long id) {
        return expenseService.findById(id);
    }

    @PostMapping("/create")
    public Mono<Expense> create(@RequestBody Expense expense) {
        return expenseService.create(expense);
    }

    @PutMapping("/update")
    public Mono<Expense> update(@RequestBody Expense expense) {
        return expenseService.update(expense);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return expenseService.delete(id);
    }

    @GetMapping
    public Mono<PageResponse<ExpenseDto>> findPagination(
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String search
    ) {
        return expenseService.findPagination(
                pageNumber,
                pageSize,
                startDate,
                endDate,
                search
        );
    }
}
