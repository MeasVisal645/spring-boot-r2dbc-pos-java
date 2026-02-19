package backend.Controller;

import backend.Entities.User;
import backend.Request.Request;
import backend.Request.Response;
import backend.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public Mono<User> signUp(@RequestBody User user) {
        return userService.create(user);
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<Response>> signIn(@RequestBody Request request) {
        return userService.signIn(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/refresh")
    public Mono<Response> refreshToken(@CookieValue(name = "refreshToken", required = false) String token) {
        return userService.refreshToken(token);
    }
}
