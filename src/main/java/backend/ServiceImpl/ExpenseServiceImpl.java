package backend.ServiceImpl;

import backend.Entities.Expense;
import backend.Dto.ExpenseDto;
import backend.Entities.OrderItem;
import backend.Mapper.ExpenseMapper;
import backend.Repository.ExpenseRepository;
import backend.Service.ExpenseService;
import backend.Service.UserService;
import backend.Utils.FilteredWithPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final UserService userService;

    @Override
    public Flux<Expense> findAll() {
        return expenseRepository.findAll();
    }

    @Override
    public Mono<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    public Mono<Expense> create(Expense expense) {
        return userService.currentUser()
                .flatMap(userId ->
                        expenseRepository.save(
                                Expense.from(expense)
                                        .userId(userId)
                                        .isComplete(true)
                                        .createdDate(LocalDateTime.now())
                                        .build()
                        )
                );
    }

    @Override
    public Mono<Expense> update(Expense expense) {
        return expenseRepository.findById(expense.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found")))
                .flatMap(existing -> {
                    Expense.update(existing, expense);
                    return expenseRepository.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return expenseRepository.deleteById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found")));
    }

    @Override
    public Mono<PageResponse<ExpenseDto>> findPagination(Integer pageNumber, Integer pageSize, LocalDate startDate, LocalDate endDate, String search) {
        Criteria criteria = Criteria.where(Expense.IS_COMPLETE_COLUMN).isTrue();

        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            criteria = criteria.and(Expense.CREATED_DATE_COLUMN).between(startDateTime, endDateTime);
        }

        if (search != null && !search.isBlank()) {
            criteria = criteria.and(Expense.NOTE_COLUMN).like("%" + search + "%")
                    .or(Expense.REFERENCE_COLUMN).like("%" + search + "%")
                    .or(Expense.CATEGORY_COLUMN).like("%" + search + "%");
        }

        return FilteredWithPaginationUtils.fetch(
                r2dbcEntityTemplate,
                Expense.class,
                criteria,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(Sort.Order.desc(OrderItem.CREATED_DATE_COLUMN)),
                ExpenseMapper::toDto
        );
    }
}
