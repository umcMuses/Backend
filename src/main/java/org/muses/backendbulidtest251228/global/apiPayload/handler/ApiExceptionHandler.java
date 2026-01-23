package org.muses.backendbulidtest251228.global.apiPayload.handler;

import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * API 요청/응답 관련 예외 처리
 * - JSON 파싱 에러
 * - 유효성 검사 실패
 * - 타입 변환 실패
 */
@Slf4j
@RestControllerAdvice
@Order(1)
public class ApiExceptionHandler {
    // JSON 파싱시 에러 (Enum 변환 실패 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());

        String message = "요청 형식이 올바르지 않습니다";

        // Enum 변환 실패 시 잘못된 값 추출
        if (e.getMessage() != null && e.getMessage().contains("Cannot deserialize value of type")) {
            String invalidValue = extractInvalidValue(e.getMessage());
            message = "'" + invalidValue + "'은(는) 유효하지 않은 값입니다. 허용된 값을 확인해주세요";
        }

        return ApiResponse.fail(ErrorCode.VALIDATION_ERROR, message);
    }

    // @RequestParam Enum 변환 실패
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());

        String invalidValue = e.getValue() != null ? e.getValue().toString() : "null";
        String message = "'" + invalidValue + "'은(는) 유효하지 않은 값입니다. 허용된 값을 확인해주세요";

        return ApiResponse.fail(ErrorCode.VALIDATION_ERROR, message);
    }

    // @Valid 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("입력 값을 확인해주세요");

        return ApiResponse.fail(ErrorCode.VALIDATION_ERROR, message);
    }

    // 에러 메시지에서 잘못된 값 추출
    private String extractInvalidValue(String message) {
        int startIdx = message.indexOf("from String \"");
        if (startIdx != -1) {
            startIdx += 13;
            int endIdx = message.indexOf("\"", startIdx);
            if (endIdx != -1) {
                return message.substring(startIdx, endIdx);
            }
        }
        return "알 수 없음";
    }
}
