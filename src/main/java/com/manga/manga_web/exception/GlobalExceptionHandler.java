package com.manga.manga_web.exception;

import com.gigalike.shared.dto.ApiResponse;
import com.gigalike.shared.exception.BusinessException;
import com.gigalike.shared.exception.ResourceNotFoundException;
import com.gigalike.shared.exception.UnauthorizedException;
import com.gigalike.shared.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        log.error("Business exception: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(com.gigalike.shared.exception.UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation failed: {}", errors);
        ApiResponse<Object> response = ApiResponse.error("Validation failed", errors.toString(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.error("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpException(Exception ex) {
        log.error("Http handler error: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.error("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(Exception ex) {
        log.error("Notfound handler error: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.error("Not found", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(Exception ex) {
        log.error("User notfound handler error: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.error("User not found", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
