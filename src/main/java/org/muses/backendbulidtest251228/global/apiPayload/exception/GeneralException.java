package org.muses.backendbulidtest251228.global.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.muses.backendbulidtest251228.common.ErrorCode;
import org.muses.backendbulidtest251228.global.apiPayload.code.error.BaseErrorCode;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{

    private final BaseErrorCode code;
}
