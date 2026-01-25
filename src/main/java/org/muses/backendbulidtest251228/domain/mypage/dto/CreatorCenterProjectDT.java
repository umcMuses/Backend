package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.*;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreatorCenterProjectDT {

    // 8번: 내 프로젝트 리스트
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyProjectItem {
        private Long projectId;
        private String title;
        private FundingStatus fundingStatus; // 진행중/완료 등 뱃지
        private Integer dDay;                // D-3 같은 것
        private Integer achieveRate;         // 86%, 124% 같은 것
        private BigDecimal raisedAmount;     // 오른쪽 금액(있으면)
        private List<String> tags;           // 설정 화면에도 쓸 수 있어서 같이 내려도 됨
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyProjectListResponse {
        private List<MyProjectItem> items;
    }

    // 6~7번: 프로젝트 설정(조회/수정 결과)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectSettingsResponse {
        private Long projectId;
        private String description;
        private List<String> tags;

        private BigDecimal targetAmount;   // 수정불가 항목 표시용
        private LocalDateTime deadline;    // 수정불가 항목 표시용
    }

    // 4~5번: 메이커 명단
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MakerRow {
        private Long memberId;
        private String nickname;
        private String name;
        private String phone;
        private String email;

        private Integer quantity;
        private String rewardName;

        private String qrStatus; // 서버에 상태가 없으면 NONE 고정
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MakerListResponse {
        private Long projectId;
        private List<MakerRow> items;
    }
}
