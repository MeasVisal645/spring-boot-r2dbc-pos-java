package backend.Controller;

import backend.Dto.SignInRequest;
import backend.Dto.SignInResponse;
import backend.Dto.UserDto;
import backend.Entities.User;
import backend.Service.UserService;
import backend.Utils.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public Mono<User> signUp(@RequestBody User user) {
        return userService.signUp(user);
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<SignInResponse>> signIn(@RequestBody SignInRequest request) {
        return userService.signIn(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/user/me")
    public Mono<UserDto> whoAmI() {
        return userService.whoAmI();
    }

    @PostMapping("/refresh")
    public Mono<String> refresh(@RequestBody RefreshToken request) {
        return userService.refreshToken(request.refreshToken());
    }

}
