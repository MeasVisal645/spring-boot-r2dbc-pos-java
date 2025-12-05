package backend.Service;

import backend.Dto.SignInRequest;
import backend.Dto.SignInResponse;
import backend.Dto.UserDto;
import backend.Entities.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface UserService {

    Mono<SignInResponse> signIn(SignInRequest request);
    Mono<User> signUp(User user);
    Mono<Void> deleteByUsername(String username);
    Mono<User> findByUsername(String username);
    Mono<UserDto> whoAmI();
    Mono<String> refreshToken(String refreshToken);

}
