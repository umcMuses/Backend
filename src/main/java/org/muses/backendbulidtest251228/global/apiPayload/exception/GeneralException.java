package org.muses.backendbulidtest251228.global.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.muses.backendbulidtest251228.common.ErrorCode;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{

    private final ErrorCode code;
}
