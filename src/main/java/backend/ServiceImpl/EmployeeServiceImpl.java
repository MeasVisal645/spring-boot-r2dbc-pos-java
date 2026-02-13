package backend.ServiceImpl;

import backend.Dto.EmployeeDto;
import backend.Dto.EmployeeUser;
import backend.Entities.Employee;
import backend.Entities.OrderItem;
import backend.Entities.User;
import backend.Mapper.EmployeeMapper;
import backend.Mapper.UserMapper;
import backend.Repository.EmployeeRepository;
import backend.Repository.UserRepository;
import backend.Service.EmployeeService;
import backend.Service.FileService;
import backend.Utils.NestedPaginationUtils;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final FileService fileService;
    private final String publicUrl;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, UserRepository userRepository, R2dbcEntityTemplate r2dbcEntityTemplate, FileService fileService,@Value("${r2.publicUrl}")  String publicUrl) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.fileService = fileService;
        this.publicUrl = publicUrl;
    }


    @Override
    public Flux<EmployeeDto> findAll() {
        return employeeRepository.findAll()
                .map(EmployeeMapper::toDto);
    }

    @Override
    public Mono<Employee> findById(Long id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee not found")));
    }

    @Override
    public Mono<Employee> create(Employee employee) {
        return employeeRepository.save(Employee.from(employee)
                .createdDate(LocalDateTime.now())
                .isActive(true)
                .build());
    }

    @Override
    public Mono<EmployeeDto> createWithImage(EmployeeDto dto, Mono<FilePart> file) {

        Employee entity = EmployeeMapper.toEntity(dto);
        entity.setActive(true);
        entity.setCreatedDate(LocalDateTime.now());

        return employeeRepository.save(entity)
                .flatMap(emp -> file
                                .flatMap(filePart -> {
                                    String imageKey = "employee/profile/" + emp.getId();
                                    String imageUrl = publicUrl + "/" + imageKey;

                                    return fileService.uploadFile(imageKey, filePart)
                                            .then(Mono.defer(() -> {
                                                emp.setImageKey(imageKey);
                                                emp.setImageUrl(imageUrl);
                                                return employeeRepository.save(emp);
                                            }))
                                            .onErrorResume(e ->
                                                    fileService.deleteFile(imageKey)
                                                            .then(Mono.error(e)));
                                })
                                .switchIfEmpty(Mono.just(emp))
                )
                .map(EmployeeMapper::toDto);
    }

    @Override
    public Mono<String> updateImage(Long id, FilePart file) {
        String imageKey = "employee/profile/" + id;
        String imageUrl = publicUrl + "/" + imageKey;

        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")))
                .flatMap(employee -> {
                    String oldKey = employee.getImageKey();

                    Mono<Void> deleteOld = (oldKey == null || oldKey.isBlank())
                            ? Mono.empty()
                            : fileService.deleteFile(oldKey)
                            .onErrorResume(e -> Mono.empty());

                    return fileService.uploadFile(imageKey, file)
                            .then(deleteOld)
                            .then(Mono.defer(() -> {
                                employee.setImageKey(imageKey);
                                employee.setImageUrl(imageUrl);
                                return employeeRepository.save(employee);
                            }));
                })
                .thenReturn("Uploaded");
    }

    @Override
    public Mono<Employee> update(Employee employee) {
        return employeeRepository.findById(employee.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")))
                .flatMap(existingEmployee -> {
                        Employee.update(existingEmployee, employee)
                                .setUpdatedDate(LocalDateTime.now());
                    return employeeRepository.save(existingEmployee);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return employeeRepository.findById(id)
                // validate existing id
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")))
                .flatMap(employee -> {
                    String oldKey = employee.getImageKey();

                    Mono<Void> deleteFile = (oldKey == null || oldKey.isBlank())
                            ? Mono.empty()
                            : fileService.deleteFile(oldKey).onErrorResume(e -> Mono.empty());

                    return deleteFile
                            .then(employeeRepository.deleteById(id))
                            .then(userRepository.deleteByEmployeeId(id));
                });
    }


    @Override
    public Mono<PageResponse<EmployeeUser>> findPagination(Integer pageNumber, Integer pageSize) {
        return NestedPaginationUtils.fetchPagination(
                r2dbcEntityTemplate,
                Employee.class,
                Employee.IS_ACTIVE_COLUMN,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Sort.by(Sort.Order.desc(Employee.CREATED_DATE_COLUMN)),
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
