package org.muses.backendbulidtest251228.domain.order.exception.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.muses.backendbulidtest251228.global.apiPayload.code.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {

    INVALID(HttpStatus.BAD_REQUEST,
            "COMMON400",
            "요청이 적절하지 않습니다."
    );


    private final HttpStatus status;
    private final String code;
    private final String message;
}
