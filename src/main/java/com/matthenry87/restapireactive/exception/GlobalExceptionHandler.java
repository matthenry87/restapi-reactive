package com.matthenry87.restapireactive.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Error> serverWebInputException(ServerWebInputException e) {

        Throwable cause = e.getCause();

        if (cause instanceof DecodingException) {

            Throwable cause1 = cause.getCause();

            if (cause1 instanceof InvalidFormatException) {

                return processInvalidFormatException((InvalidFormatException) cause1);
            }
        }

        throw e;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<List<Error>> webExchangeBindException(WebExchangeBindException e) {

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<Error> errors = fieldErrors.stream()
                .map(x -> new Error(x.getField(), x.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<Error>> constraintViolationException(ConstraintViolationException e) {

        List<Error> errors = new ArrayList<>();

        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {

            Path propertyPath = constraintViolation.getPropertyPath();

            String name = ((PathImpl) propertyPath).getLeafNode().getName();
            String message = constraintViolation.getMessage();

            errors.add(new Error(name, message));
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> alreadyExistsException(AlreadyExistsException e) {

        String message = e.getMessage();

        Error error = new Error(null, message == null ? "already exists" : message);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> notFoundException(NotFoundException e) {

        Error error = new Error(null, "not found");

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> exception(Exception e) {

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Error> processInvalidFormatException(InvalidFormatException invalidFormatException) {

        Class<?> targetType = invalidFormatException.getTargetType();

        if (Enum.class.isAssignableFrom(targetType)) {

            Enum[] enumConstants = (Enum[]) targetType.getEnumConstants();

            String values = Arrays.stream(enumConstants)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            String message  = "Invalid value. Valid values: " + values;
            String field = invalidFormatException.getPath().get(0).getFieldName();

            Error error = new Error(field, message);

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    class Error {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String field;
        private final String message;

    }

}