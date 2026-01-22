package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "프로젝트 초기 생성 요청 DTO - 빈 프로젝트 생성 시 사용 (JWT 인증 필수)")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequestDT {

    @Schema(
            description = "[Deprecated] 사용자 ID - JWT 인증으로 자동 추출되므로 입력 불필요",
            example = "1",
            deprecated = true
    )
    private Long userId;

    @Schema(
            description = "프로젝트 제목 (선택사항, 미입력 시 '새 프로젝트'로 자동 설정)",
            example = "임시프로젝트이름"
    )
    private String title;
}
