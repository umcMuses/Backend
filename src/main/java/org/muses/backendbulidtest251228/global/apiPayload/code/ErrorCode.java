package org.muses.backendbulidtest251228.global.apiPayload.code;

/**
 * 에러 코드 정의
 * 
 * 코드 체계:
 * - SYS_1xxx: 시스템 에러
 * - AUTH_2xxx: 인증 에러
 * - REQ_3xxx: 요청 에러
 * - BIZ_4xxx: 비즈니스 에러
 */
public enum ErrorCode {

    // 시스템 에러 (1xxx)
    UNKNOWN_ERROR("SYS_1000", "알 수 없는 오류가 발생했습니다"),
    NETWORK_ERROR("SYS_1001", "네트워크 연결을 확인해주세요"),
    TIMEOUT("SYS_1002", "요청 시간이 초과되었습니다"),
    SERVER_ERROR("SYS_1003", "서버 오류가 발생했습니다"),

    // 인증 에러 (2xxx)
    AUTH_REQUIRED("AUTH_2000", "로그인이 필요합니다"),
    AUTH_EXPIRED("AUTH_2001", "인증이 만료되었습니다. 다시 로그인해주세요"),
    AUTH_INVALID("AUTH_2002", "유효하지 않은 인증 정보입니다"),
    AUTH_FORBIDDEN("AUTH_2003", "접근 권한이 없습니다"),

    // 요청 에러 (3xxx)
    BAD_REQUEST("REQ_3000", "잘못된 요청입니다"),
    VALIDATION_ERROR("REQ_3001", "입력 값을 확인해주세요"),
    NOT_FOUND("REQ_3002", "요청한 리소스를 찾을 수 없습니다"),
    DUPLICATE("REQ_3003", "이미 존재하는 데이터입니다"),

    // 비즈니스 에러 (4xxx)
    BUSINESS_ERROR("BIZ_4000", "처리 중 오류가 발생했습니다"),
    INSUFFICIENT_BALANCE("BIZ_4001", "잔액이 부족합니다"),
    ALREADY_PROCESSED("BIZ_4002", "이미 처리된 요청입니다");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
