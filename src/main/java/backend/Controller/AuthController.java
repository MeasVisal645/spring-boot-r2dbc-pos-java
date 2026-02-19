package backend.Controller;

import backend.Entities.User;
import backend.Request.Request;
import backend.Request.Response;
import backend.Service.UserService;
import backend.Utils.CookieUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public Mono<ResponseEntity<Response>> signin(@RequestBody Request req) {
        return userService.signIn(req)
                .map(tokens -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, CookieUtil.responseCookie(tokens.refreshToken()).toString())
                        .body(new Response(tokens.accessToken(), tokens.refreshToken()))
                );
    }

    @PostMapping("/refresh")
    public Mono<Response> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        System.out.println("COOKIE refreshToken = " + refreshToken);
        return userService.refreshToken(refreshToken);
    }
}
