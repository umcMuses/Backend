package org.muses.backendbulidtest251228.global.apiPayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST,
            "COMMON400",
            "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "AUTH401",
            "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,
            "AUTH403",
            "요청이 거부되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND,
            "COMMON404",
            "요청한 리소스를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
