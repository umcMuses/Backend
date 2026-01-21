package org.muses.backendbulidtest251228.global.apiPayload.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode{

    OK(HttpStatus.OK,
            "COMMON200_1",
            "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED,
            "COMMON201_1",
            "리소스가 성공적으로 생성되었습니다."),
    ACCEPTED(HttpStatus.ACCEPTED,
            "COMMON202_1," ,
            "요청이 접수되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT,
            "COMMON204_1",
            "요청은 성공했으나 반환할 데이터가 없습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
