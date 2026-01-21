package org.muses.backendbulidtest251228.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1)  ApiException은 여기서 처리
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException e) {
        ErrorCode code = e.getErrorCode();
        HttpStatus status = mapStatus(code);

        ApiResponse<Object> body =
                (e.getDetail() == null)
                        ? ApiResponse.fail(code.getCode(), e.getMessage())
                        : ApiResponse.fail(code.getCode(), e.getMessage(), e.getDetail());

        return ResponseEntity.status(status).body(body);
    }

    // 2) validation(@Valid) 사용하면 여기로 들어옴
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, Object> detail = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err ->
                detail.put(err.getField(), err.getDefaultMessage())
        );

        ApiResponse<Object> body = ApiResponse.fail(
                ErrorCode.VALIDATION_ERROR.getCode(),
                ErrorCode.VALIDATION_ERROR.getMessage(),
                detail
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 3) 그 외 예외는 전부 500으로 통일
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnknown(Exception e) {
        ApiResponse<Object> body = ApiResponse.fail(
                ErrorCode.SERVER_ERROR.getCode(),
                ErrorCode.SERVER_ERROR.getMessage(),
                Map.of("originalMessage", e.getMessage())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * ErrorCode -> HTTP Status 매핑
     */
    private HttpStatus mapStatus(ErrorCode code) {
        return switch (code) {
            // 인증
            case AUTH_REQUIRED, AUTH_EXPIRED, AUTH_INVALID -> HttpStatus.UNAUTHORIZED;
            case AUTH_FORBIDDEN -> HttpStatus.FORBIDDEN;

            // 요청/리소스
            case NOT_FOUND -> HttpStatus.NOT_FOUND;

            // 중복 오류
            case DUPLICATE -> HttpStatus.CONFLICT;

            // 요청 오류
            case BAD_REQUEST, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;

            // 시스템
            case NETWORK_ERROR, TIMEOUT, SERVER_ERROR, UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;

            // 비즈니스
            case BUSINESS_ERROR, INSUFFICIENT_BALANCE, ALREADY_PROCESSED -> HttpStatus.BAD_REQUEST;
        };
    }
}
