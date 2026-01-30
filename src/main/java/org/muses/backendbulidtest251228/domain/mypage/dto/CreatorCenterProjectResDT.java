package org.muses.backendbulidtest251228.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreatorCenterProjectResDT {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "내 프로젝트 카드(목록 아이템)")
    public static class MyProjectItem {

        @Schema(description = "프로젝트 ID", example = "2")
        private Long projectId;

        @Schema(description = "프로젝트 제목", example = "푸른 오렌지 재즈 콘서트")
        private String title;

        @Schema(description = "펀딩 상태(뱃지용)", example = "FUNDING")
        private FundingStatus fundingStatus;

        @Schema(description = "D-day (예: 3이면 D-3)", example = "3")
        private Integer dDay;

        @Schema(description = "달성률(퍼센트, 0~)", example = "124")
        private Integer achieveRate;

        @Schema(description = "현재 모금액", example = "2840000")
        private BigDecimal raisedAmount;

        @Schema(description = "설정 화면에서 보여줄 태그 목록", example = "[\"재즈\",\"공연\"]")
        private List<String> tags;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "내 프로젝트 목록 응답")
    public static class MyProjectListResponse {
        @Schema(description = "프로젝트 리스트")
        private List<MyProjectItem> items;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "프로젝트 설정 조회/수정 응답")
    public static class ProjectSettingsResponse {

        @Schema(description = "프로젝트 ID", example = "2")
        private Long projectId;

        @Schema(description = "프로젝트 소개", example = "재즈 라이브 공연을 준비 중입니다.")
        private String description;

        @Schema(description = "태그 목록", example = "[\"재즈\",\"공연\"]")
        private List<String> tags;

        @Schema(description = "목표 금액(수정 불가, 표시용)", example = "3000000")
        private BigDecimal targetAmount;

        @Schema(description = "마감일시(수정 불가, 표시용)", example = "2026-02-10T18:00:00")
        private LocalDateTime deadline;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "메이커 명단 행(프로젝트 후원자/구매자 목록)")
    public static class MakerRow {

        @Schema(description = "회원 ID", example = "5")
        private Long memberId;

        @Schema(description = "닉네임", example = "재즈좋아")
        private String nickname;

        @Schema(description = "이름", example = "이지원")
        private String name;

        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;

        @Schema(description = "이메일", example = "2jw@gmail.com")
        private String email;

        @Schema(description = "구매 수량", example = "2")
        private Integer quantity;

        @Schema(description = "리워드(옵션)명", example = "VIP 티켓")
        private String rewardName;

        @Schema(
                description = "QR 상태(서버에서 상태를 관리하지 않으면 NONE 고정)",
                example = "NONE"
        )
        private String qrStatus;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "메이커 명단 응답")
    public static class MakerListResponse {

        @Schema(description = "프로젝트 ID", example = "2")
        private Long projectId;

        @Schema(description = "메이커 리스트")
        private List<MakerRow> items;
    }
}
