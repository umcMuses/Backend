package org.muses.backendbulidtest251228.domain.order.exception;

import org.muses.backendbulidtest251228.global.apiPayload.code.error.BaseErrorCode;
import org.muses.backendbulidtest251228.global.apiPayload.exception.GeneralException;

public class OrderException extends GeneralException {
    public OrderException(BaseErrorCode code) {
        super(code);
    }
}
