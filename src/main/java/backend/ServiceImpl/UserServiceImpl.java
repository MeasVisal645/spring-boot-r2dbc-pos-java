package backend.ServiceImpl;

import backend.Dto.SignInRequest;
import backend.Dto.SignInResponse;
import backend.Dto.UserDto;
import backend.Entities.User;
import backend.Entities.UserRole;
import backend.Repository.UserRepository;
import backend.Service.UserService;
import backend.Utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;


    @Override
    public Mono<SignInResponse> signIn(SignInRequest request) {
        if (request.username() == null || request.password() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password not found!"));
        }

        return userRepository.findByUsername(request.username())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found!")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!"));
                    }
                    return Mono.just(new SignInResponse(jwtUtil.generateToken(user)));   // "Bearer " + jwtUtil.generateToken(user)
                });
    }

    @Override
    public Mono<User> signUp(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getUserRole() == null) {
            user.setUserRole(UserRole.ROLE_USER);
        }
        return userRepository.save(user);
    }

    @Override
    public Mono<Void> deleteByUsername(String username) {
        return null;
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return r2dbcEntityTemplate.select(User.class)
                .matching(Query.query(Criteria.where(User.USERNAME_COLUMN).is(username)))
                .one();
    }

    @Override
    public Mono<UserDto> whoAmI() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    String username = auth.getName();
                    return userRepository.findByUsername(username)
                            .map(user -> new UserDto(user.getUsername(), user.getUserRole().name()));
                });
    }

    @Override
    public Mono<String> refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            return Mono.error(new RuntimeException("Invalid Refresh Token"));
        }
        String username = jwtUtil.extractUsername(refreshToken);
        return userRepository.findByUsername(username)
                .map(jwtUtil::generateToken);
    }
}
