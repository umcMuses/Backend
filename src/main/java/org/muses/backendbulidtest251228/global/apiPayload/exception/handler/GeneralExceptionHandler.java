package org.muses.backendbulidtest251228.global.apiPayload.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.common.ApiResponse;
import org.muses.backendbulidtest251228.common.ErrorCode;
import org.muses.backendbulidtest251228.global.apiPayload.exception.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<?>> handleException(
            GeneralException ex
    ) {

        return ResponseEntity.status(ex.getCode().getStatus())
                .body(ApiResponse.fail(
                        ex.getCode()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        log.warn("[Validation Error] {}", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.VALIDATION_ERROR, errorMessage));
    }

}
