package org.muses.backendbulidtest251228.global.businessError;

import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 비즈니스 로직 관련 예외 처리
 * - BusinessException
 * - IllegalArgumentException
 * - IllegalStateException
 * - 기타 서버 오류
 */
@Slf4j
@RestControllerAdvice
@Order(2)
public class BusinessExceptionHandler {

    // BusinessException 처리 (비즈니스 로직 예외)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: [{}] {}", e.getCode(), e.getMessage());

        ApiResponse<?> response;
        if (e.getDetail() != null) {
            response = ApiResponse.fail(e.getCode(), e.getMessage(), e.getDetail());
        } else {
            response = ApiResponse.fail(e.getCode(), e.getMessage());
        }

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    // IllegalArgumentException (잘못된 요청 - 프로젝트 없음 등)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    // IllegalStateException (비즈니스 로직 오류 - 제출 조건 미충족 등)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());
        return ApiResponse.fail(ErrorCode.BUSINESS_ERROR, e.getMessage());
    }

    // 그 외 모든 예외 (서버 오류)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ApiResponse.fail(ErrorCode.SERVER_ERROR);
    }
}
