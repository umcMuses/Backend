package org.muses.backendbulidtest251228.global.apiPayload.code.error;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
