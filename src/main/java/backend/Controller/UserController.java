package backend.Controller;

import backend.Dto.UserDto;
import backend.Entities.User;
import backend.Request.Request;
import backend.Request.Response;
import backend.Service.UserService;
import backend.Utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/all")
    public Flux<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/user/{id}")
    public Mono<User> findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/user/update")
    public Mono<User> update(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/user/delete")
    public Mono<Long> delete(@RequestParam Long id) {
        return userService.delete(id);
    }

    @GetMapping("/user/me")
    public Mono<UserDto> whoAmI() {
        return userService.whoAmI();
    }

    @GetMapping("/user")
    public Mono<PageResponse<UserDto>> findPagination(@RequestParam Integer pageNumber, Integer pageSize) {
        return userService.findPagination(pageNumber, pageSize);
    }


}
