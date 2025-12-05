package backend.Exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@RestControllerAdvice
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        int status = 500;
        String message = "Internal Server Error";

        if (ex instanceof ResponseStatusException responseEx) {
            status = responseEx.getStatusCode().value();
            message = responseEx.getReason();
        }

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));

        String body = String.format("{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status, HttpStatus.valueOf(status).name(), message);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
