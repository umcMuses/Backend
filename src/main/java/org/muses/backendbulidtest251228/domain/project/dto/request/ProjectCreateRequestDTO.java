package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "프로젝트 생성 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequestDTO {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "프로젝트 제목 (선택)", example = "새 프로젝트")
    private String title;
}
