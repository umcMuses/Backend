package org.muses.backendbulidtest251228.domain.toss.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingApproveReqDT {
    private BigDecimal amount;
    private String customerKey;
    private String orderId;
    private String orderName;

}
