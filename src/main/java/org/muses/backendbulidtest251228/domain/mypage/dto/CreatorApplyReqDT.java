package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class CreatorApplyReqDT {
    @NotBlank(message = "creatorType은 필수입니다")
    private String creatorType;
}
