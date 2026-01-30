package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatorApplyResDT {
    private Long applicationId;
    private String creatorType;
    private String status;
}
