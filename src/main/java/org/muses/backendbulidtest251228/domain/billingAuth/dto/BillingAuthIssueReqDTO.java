package org.muses.backendbulidtest251228.domain.billingAuth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "빌링키 발급 요청 DTO")
public class BillingAuthIssueReqDTO {

    @Schema(
            description = "토스 결제 인증 완료 후 successUrl로 전달되는 authKey",
            example = "auth_4a2f8c9d0e"
    )
    @NotBlank(message = "인증 키(authKey)는 필수입니다.")
    private String authKey;

    @Schema(
            description = "구매자 식별 키 (billingKey와 1:N 관계)",
            example = "user_12345"
    )
    @NotBlank(message = "고객 키(customerKey)는 필수입니다.")
    private String customerKey;
}
