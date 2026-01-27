package backend.ServiceImpl;

import backend.Dto.EmployeeDto;
import backend.Dto.EmployeeUser;
import backend.Entities.Employee;
import backend.Entities.User;
import backend.Mapper.EmployeeMapper;
import backend.Mapper.UserMapper;
import backend.Repository.EmployeeRepository;
import backend.Service.EmployeeService;
import backend.Utils.NestedPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<EmployeeDto> findAll() {
        return employeeRepository.findAll()
                .map(EmployeeMapper::toDto);
    }

    @Override
    public Mono<Employee> findById(Long id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found with id: " + id))
                );
    }

    @Override
    public Mono<Employee> create(Employee employee) {
        return employeeRepository.save(Employee.from(employee)
                .createdDate(LocalDateTime.now())
                .isActive(true)
                .build());
    }

    @Override
    public Mono<Employee> update(Employee employee) {
        return employeeRepository.findById(employee.getId())
                .flatMap(existingEmployee -> {
                        Employee.update(existingEmployee, employee)
                                .setUpdatedDate(LocalDateTime.now());
                    return employeeRepository.save(existingEmployee);
                });
    }

    @Override
    public Mono<Long> delete(Long id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")))
                .flatMap(employee ->
                            employeeRepository.deleteById(id)
                                    .thenReturn(id)
                        );
    }

    @Override
    public Mono<PageResponse<EmployeeUser>> findPagination(Integer pageNumber, Integer pageSize) {
        return NestedPaginationUtils.fetchPagination(
                r2dbcEntityTemplate,
                Employee.class,
                Employee.IS_ACTIVE_COLUMN,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                employee -> r2dbcEntityTemplate.select(User.class)
                        .matching(Query.query(Criteria.where(User.EMPLOYEE_ID_COLUMN).is(employee.getId())))
                        .all()
                        .collectList()
                        .map(users -> users.stream()
                                .map(UserMapper::toDto)
                                .toList()),
                (employee, users) -> new EmployeeUser(EmployeeMapper.toDto(employee), users)
        );
    }



}
