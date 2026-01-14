package backend.Exception;

import backend.Utils.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Order(-2)
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ResponseEntity<?>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex
    ){

        HashMap<String, String> errors = new HashMap<>();
        if (ex.getCause() instanceof MismatchedInputException mismatchedInputException) {
            String fieldName = mismatchedInputException.getPath().get(0).getFieldName();
            String errorMessage = "not a valid representation! Please provide a valid value.";
            errors.put(fieldName,errorMessage);
        } else {
            String errorMessage = "Invalid JSON request. Please provide valid data.";
            return Mono.just(ResponseEntity.badRequest().body(errorMessage));
        }

        return Mono.just( ResponseEntity.badRequest().body(errors));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handlerMethodArgumentNotValidException(
            WebExchangeBindException exception
    ){
        HashMap<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors()
                .forEach(error-> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName,errorMessage);

                });
        System.out.println(errors);
        return  new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public Mono <ResponseEntity<?>> handlerResourceNotFound (
            ApiException exception
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getStatus().getReasonPhrase(),
                exception.getCode(),
                exception.getMsg());
        return Mono.just(ResponseEntity
                .status(exception.getStatus())
                .body(errorResponse));
    }

    @ExceptionHandler(NumberFormatException.class)
    public Mono<ResponseEntity<?>> handlerNumberFormatException (
            NumberFormatException exception
    ){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                String.format("not a valid representation! Please provide a valid value. %s",exception.getMessage()));
        return Mono.just( ResponseEntity
                .badRequest()
                .body(errorResponse));

    }
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<?>> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        // Log the exception (optional)
        System.err.println("IllegalArgumentException: " + ex.getMessage());

        // Return a custom response entity
        return Mono.just(ResponseEntity
                .badRequest()
                .body("Invalid argument: " + ex.getMessage()));
    }


    //API
    //400

}
