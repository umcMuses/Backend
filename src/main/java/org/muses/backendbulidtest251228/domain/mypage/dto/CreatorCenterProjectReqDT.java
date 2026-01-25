package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.*;

import java.util.List;

public class CreatorCenterProjectReqDT {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProjectSettingsRequest {
        private String description;
        private List<String> tags;
    }
}
