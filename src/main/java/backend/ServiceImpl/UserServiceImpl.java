package backend.ServiceImpl;

import backend.Dto.UserDto;
import backend.Entities.Brand;
import backend.Entities.CustomUserDetails;
import backend.Entities.Role;
import backend.Entities.User;
import backend.Mapper.UserMapper;
import backend.Repository.EmployeeRepository;
import backend.Repository.UserRepository;
import backend.Request.Request;
import backend.Request.Response;
import backend.Service.UserService;
import backend.Utils.JwtUtil;
import backend.Utils.PageResponse;
import backend.Utils.PaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Mono<Response> signIn(Request request) {

        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return Mono.error(
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password is required")
            );
        }

        return userRepository.findByUsername(request.username())
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                ))
                .flatMap(user -> {

                    if (!user.isActive()) {
                        return Mono.error(
                                new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is inactive")
                        );
                    }

                    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                        return Mono.error(
                                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
                        );
                    }
                    return Mono.just(new Response(jwtUtil.generateToken(user)));
                });
    }

    @Override
    public Flux<UserDto> findAll() {
        return userRepository.findAll()
                .map(UserMapper::toDto);
    }

    @Override
    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> create(User user) {
        var encodedPassword = passwordEncoder.encode(user.getPassword());

        return employeeRepository.findById(user.getEmployeeId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")))
                .flatMap(employee -> userRepository.existsByEmployeeId(user.getEmployeeId()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Employee already has a user account"));
                    }

                    return userRepository.save(
                            User.from(user)
                                    .role(Role.ROLE_USER)
                                    .password(encodedPassword)
                                    .employeeId(user.getEmployeeId())
                                    .isActive(true)
                                    .createdDate(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    @Override
    public Mono<User> update(User user) {
        var encodedPassword = passwordEncoder.encode(user.getPassword());
        return userRepository.findById(user.getId())
                .flatMap(existingUser -> {
                    User.update(existingUser, user);
                    existingUser.setUpdatedDate(LocalDateTime.now());
                    existingUser.setPassword(encodedPassword);

                    return userRepository.save(existingUser);
                });
    }

    @Override
    public Mono<Long> delete(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .flatMap(user ->
                        userRepository.deleteById(id)
                                .thenReturn(id)
                );
    }

    @Override
    public Mono<UserDto> whoAmI() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> userRepository.findByUsername(auth.getName()))
                .flatMap(user ->
                        employeeRepository.findById(user.getEmployeeId())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")))
                                .map(employee ->
                                    new UserDto(
                                            user.getId(),
                                            user.getUsername(),
                                            user.getRole(),
                                            employee.getImageUrl()
                                    )
                                )
                );
    }


    @Override
    public Mono<Long> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    Object principal = auth.getPrincipal();

                    if (principal instanceof CustomUserDetails cud) {
                        return Mono.just(cud.getId());
                    }

                    if (principal instanceof String username) {
                        return userRepository.findByUsername(username)
                                .map(User::getId);
                    }

                    return Mono.error(new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid authentication principal"
                    ));
                });
    }

    @Override
    public Mono<PageResponse<UserDto>> findPagination(Integer pageNumber, Integer pageSize) {
        return PaginationUtils.fetchPagedResponse(
                r2dbcEntityTemplate,
                User.class,
                UserMapper::toDto,
                Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER),
                Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT),
                Criteria.where(User.IS_ACTIVE_COLUMN).isTrue(),
                Sort.by(Sort.Order.desc(User.CREATED_DATE_COLUMN))
        );
    }

}
