package org.muses.backendbulidtest251228.global.businessError;

import lombok.Getter;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.apiPayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 예외
 * 
 * 사용 예시 (공통 에러코드):
 * - throw new BusinessException(ErrorCode.NOT_FOUND);
 * - throw new BusinessException(ErrorCode.DUPLICATE, "이미 사용 중인 닉네임입니다.");
 * - throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 요청", Map.of("field", "value"));
 * 
 * 사용 예시 (도메인별 에러코드):
 * - throw new BusinessException(OrderErrorCode.ALREADY_PAID);
 * - throw new BusinessException(OrderErrorCode.INVALID, "주문 상태가 올바르지 않습니다.", Map.of("orderId", 1L));
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final Object detail;

    // =============== ErrorCode (공통 에러코드) ===============

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.detail = null;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.detail = null;
    }

    public BusinessException(ErrorCode errorCode, String message, Object detail) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.detail = detail;
    }

    // =============== BaseErrorCode (도메인별 에러코드) ===============

    public BusinessException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.detail = null;
    }

    public BusinessException(BaseErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.detail = null;
    }

    public BusinessException(BaseErrorCode errorCode, String message, Object detail) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.detail = detail;
    }
}
