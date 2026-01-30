package org.muses.backendbulidtest251228.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // 공통 에러
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 오류가 발생했습니다."),
    
    // 인증 에러
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH401", "인증이 필요합니다."),
    AUTH_INVALID(HttpStatus.UNAUTHORIZED, "AUTH401", "유효하지 않은 인증 정보입니다."),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH403", "요청이 거부되었습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),

    // 유효성 검사 에러
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON400", "입력 값을 확인해주세요."),
    DUPLICATE(HttpStatus.CONFLICT, "COMMON409", "이미 존재하는 데이터입니다."),

    // 비즈니스 에러
    BUSINESS_ERROR(HttpStatus.BAD_REQUEST, "COMMON400", "처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
