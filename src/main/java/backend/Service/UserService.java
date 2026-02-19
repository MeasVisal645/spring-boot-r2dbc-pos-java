package backend.Service;

import backend.Dto.UserDto;
import backend.Entities.User;
import backend.Request.Request;
import backend.Request.Response;
import backend.Utils.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UserService {

    Flux<UserDto> findAll();
    Mono<User> findById(Long id);
    Mono<User> create(User user);
    Mono<User> update(User user);
    Mono<Long> delete(Long id);

    //Additional Method
    Mono<Response> signIn(Request request);
    Mono<Response> refreshToken(String token);
    Mono<UserDto> whoAmI();
    Mono<Long> currentUser();
    Mono<PageResponse<UserDto>> findPagination(Integer pageNumber, Integer pageSize);

}
