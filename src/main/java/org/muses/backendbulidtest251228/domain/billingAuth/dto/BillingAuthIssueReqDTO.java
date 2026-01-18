package org.muses.backendbulidtest251228.domain.billingAuth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BillingAuthIssueReqDTO {

    @NotBlank(message = "인증 키(authKey)는 필수입니다.")
    private String authKey;

    @NotBlank(message = "고객 키(customerKey)는 필수입니다.")
    private String customerKey;

}
