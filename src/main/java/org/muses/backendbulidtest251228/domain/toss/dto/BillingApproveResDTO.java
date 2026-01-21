package org.muses.backendbulidtest251228.domain.toss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingApproveResDTO {
    private String mId;
    private String paymentKey;   // 결제 식별 키
    private String orderId;
    private String status;       // DONE / CANCELED / ABORTED
    private Long totalAmount;
    private String approvedAt;
    private Failure failure;

    @Getter
    @Setter
    public static class Failure {
        private String code;
        private String message;
    }
}
