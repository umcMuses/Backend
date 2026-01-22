package org.muses.backendbulidtest251228.global.apiPayload.code;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드 인터페이스
 * 
 * 도메인별 에러코드 확장 시 구현
 * 예) OrderErrorCode implements BaseErrorCode
 */
public interface BaseErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
