package org.muses.backendbulidtest251228.global.apiPayload.code.success;

import org.springframework.http.HttpStatus;

public interface BaseSuccessCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}