package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProfileImageResponse {
    private String profileImgUrl;
}
// 마지막에 건들 예정