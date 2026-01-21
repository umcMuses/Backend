package org.muses.backendbulidtest251228.global.apiPayload.exception;

import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // JSON 파싱 에러 (Enum 변환 실패 등)
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
        log.error("Unhandled Exception: ", e);  // 서버 로그에만 trace 기록
        return ApiResponse.fail(ErrorCode.SERVER_ERROR);  // 클라이언트에는 간단한 메시지만
    }

    // 에러 메시지에서 잘못된 값 추출
    private String extractInvalidValue(String message) {
        // "from String \"KOREA\"" 패턴에서 KOREA 추출
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
