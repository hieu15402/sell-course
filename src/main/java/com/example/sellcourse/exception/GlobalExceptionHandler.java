package com.example.sellcourse.exception;

import com.example.sellcourse.dto.response.general.GenericResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice(basePackages = "com/example/sellcourse/controller")
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<GenericResponse<?>> handlingRuntimeException(Exception e) {
        log.error(e.getMessage(), e);
        GenericResponse<?> apiResponse = GenericResponse.builder()
                .code(400)
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MessagingException.class)
    ResponseEntity<GenericResponse<?>> handlingMessagingException(MessagingException e) {
        log.error(e.getMessage(), e);
        GenericResponse<?> apiResponse = GenericResponse.builder()
                .code(400)
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<GenericResponse<?>> handlingAppException(AppException exception) {
        log.error(exception.getMessage(), exception);
        ErrorCode errorCode = exception.getErrorCode();

        GenericResponse<?> apiResponse = GenericResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<GenericResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage(), exception);
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(GenericResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var firstError = exception.getBindingResult().getAllErrors().get(0);
            if (firstError instanceof ConstraintViolation<?> constraintViolation) {
                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
                log.info("Attributes: {}", attributes);
            }

        } catch (IllegalArgumentException e) {
            log.error("Error while mapping validation error", e);
        }

        GenericResponse<Object> apiResponse = new GenericResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
