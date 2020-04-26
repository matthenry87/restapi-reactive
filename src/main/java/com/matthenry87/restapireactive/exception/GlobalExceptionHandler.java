package com.matthenry87.restapireactive.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Error>> serverWebInputException(ServerWebInputException e) {

        var cause = e.getCause();

        if (cause instanceof DecodingException) {

            var cause1 = cause.getCause();

            if (cause1 instanceof InvalidFormatException) {

                return Mono.just(processInvalidFormatException((InvalidFormatException) cause1));
            }
        }

        var error = new Error(null, e.getMessage());

        return Mono.just(new ResponseEntity<>(error, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<List<Error>>> webExchangeBindException(WebExchangeBindException e) {

        var fieldErrors = e.getBindingResult().getFieldErrors();

        var errors = fieldErrors.stream()
                .map(x -> new Error(x.getField(), x.getDefaultMessage()))
                .collect(Collectors.toList());

        return Mono.just(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Error>> alreadyExistsException(AlreadyExistsException e) {

        var message = e.getMessage();

        var error = new Error(null, message == null ? "already exists" : message);

        return Mono.just(new ResponseEntity<>(error, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Error>> notFoundException(NotFoundException e) {

        var error = new Error(null, "not found");

        return Mono.just(new ResponseEntity<>(error, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public Mono<ResponseEntity<Error>> unsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException e) {

        var error = new Error(null, e.getMessage());

        return Mono.just(new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Error>> exception(Exception e) {

        return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<Error> processInvalidFormatException(InvalidFormatException invalidFormatException) {

        var targetType = invalidFormatException.getTargetType();

        if (Enum.class.isAssignableFrom(targetType)) {

            var enumConstants = (Enum[]) targetType.getEnumConstants();

            var values = Arrays.stream(enumConstants)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            var message  = "Invalid value. Valid values: " + values;
            var field = invalidFormatException.getPath().get(0).getFieldName();

            var error = new Error(field, message);

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Getter
    @AllArgsConstructor
    class Error {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String field;
        private final String message;

    }

}
