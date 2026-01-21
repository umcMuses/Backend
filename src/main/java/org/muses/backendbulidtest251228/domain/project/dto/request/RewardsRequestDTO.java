package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "3단계: 리워드 저장 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RewardsRequestDTO {

    @Schema(description = "리워드 목록")
    private List<RewardRequestDTO> rewards;
}
