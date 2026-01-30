package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.*;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class CreatorCenterProjectReqDT {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProjectSettingsRequest {

        @Schema(
                description = "프로젝트 한줄/상세 소개(설정 화면에서 수정 가능)",
                example = "재즈 라이브 공연을 준비 중입니다. 2월 10일 홍대에서 만나요!"
        )
        private String description;

        @Schema(
                description = """
                        태그 목록 ["재즈","공연","홍대"]
                        """,
                example = "[\"재즈\",\"공연\",\"홍대\"]"
        )
        private List<String> tags;
    }
}
