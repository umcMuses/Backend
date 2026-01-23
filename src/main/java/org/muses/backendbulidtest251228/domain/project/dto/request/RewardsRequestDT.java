package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "3단계: 리워드 저장 요청 DTO - 리워드 목록 일괄 저장 (기존 리워드 삭제 후 새로 저장)")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RewardsRequestDT {

    @Schema(description = "리워드(후원 상품) 목록 (최소 1개 이상 필수)")
    private List<RewardRequestDT> rewards;
}
