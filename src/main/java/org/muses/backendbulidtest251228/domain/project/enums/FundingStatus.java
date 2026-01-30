package org.muses.backendbulidtest251228.domain.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FundingStatus {
    PREPARING("준비중"),
    SCHEDULED("오픈 예정"),
    FUNDING("펀딩중"),
    CLOSING("마감 임박"),
    SUCCESS("펀딩 성공"),
    FAIL("펀딩 실패");

    private final String description;
}
