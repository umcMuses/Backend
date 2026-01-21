package org.muses.backendbulidtest251228.domain.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TODO new: MUS-014 펀딩 설정에서 필요한 펀딩 방식
 * ERD에 없어서 새로 추가함
 */
@Getter
@RequiredArgsConstructor
public enum FundingType {
    ALL_OR_NOTHING("목표 금액 미달성 시 전액 환불"),
    KEEP_IT_ALL("목표 금액 미달성 시에도 펀딩 진행");

    private final String description;
}
