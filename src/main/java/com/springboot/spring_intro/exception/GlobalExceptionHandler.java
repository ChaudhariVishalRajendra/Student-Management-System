package com.springboot.spring_intro.exception;

import com.springboot.spring_intro.payload.ApiResponse;
import com.springboot.spring_intro.payload.ErrorResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(StudentNotFoundException ex,
//                                                                        HttpServletRequest req){

    public ResponseEntity<ApiResponse<Object>> handleStudentNotFoundException(StudentNotFoundException ex,
                                                                      HttpServletRequest req){
//        ErrorResponse errorResponse = new ErrorResponse(
//                LocalDateTime.now(),
//                HttpStatus.NOT_FOUND.value(), // give status code 404
//                HttpStatus.NOT_FOUND.getReasonPhrase(), // status text, reable text
//                ex.getMessage(), // return msg of exception
//                req.getRequestURI() // Return current endpoint
//        );

        log.warn("Student Not Found: {}", ex.getMessage());
        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // give status code 404
                ex.getMessage(), // return msg of exception
                null // Return current endpoint
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateException(DuplicateEmailException ex,
                                                                  HttpServletRequest req){
        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(), // 409
                ex.getMessage(),
                req.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex,
                                                                   HttpServletRequest req){

        Map<String, String> err = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {
                    err.put(
                            fieldError.getField(),
                            fieldError.getDefaultMessage()
                    );
                });

        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                err
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                      HttpServletRequest req){
        log.warn("Resource Not Found: ",ex.getMessage());
        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // 404
                ex.getMessage(),
                req.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<Object>> handleOptimisticLockException(OptimisticLockException ex,
                                                                             HttpServletRequest req){
        ApiResponse<Object> res = new ApiResponse<>(
                false,
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Student was already updated by another user",
                null
        );
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex,
                                                                      HttpServletRequest req) {
        log.error("==> [GlobalExceptionHandler] 500 Internal Server Error at [{} {}] | Exception: {} | Message: {}",
                req.getMethod(), req.getRequestURI(), ex.getClass().getName(), ex.getMessage(), ex);

        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage() != null ? ex.getMessage() : "Internal Server Error (" + ex.getClass().getSimpleName() + ")",
                req.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
