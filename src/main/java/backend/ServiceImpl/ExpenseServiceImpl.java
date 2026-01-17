package backend.ServiceImpl;

import backend.Entities.Expense;
import backend.Entities.ExpenseDto;
import backend.Entities.Product;
import backend.Mapper.ExpenseMapper;
import backend.Repository.ExpenseRepository;
import backend.Service.ExpenseService;
import backend.Service.UserService;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
    public Mono<PageResponse<ExpenseDto>> findPagination(Integer pageNumber, Integer pageSize) {
        return PaginationUtils.fetchPagedResponse(
                r2dbcEntityTemplate,
                Expense.class,
                ExpenseMapper::toDto,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Expense.IS_COMPLETE_COLUMN,
                Sort.by(Sort.Order.desc(Expense.CREATED_DATE_COLUMN))
        );
    }
}
