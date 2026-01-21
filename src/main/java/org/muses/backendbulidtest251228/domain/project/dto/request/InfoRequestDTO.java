package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "5단계: 정보 저장 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InfoRequestDTO {

    // 진행자 정보 (공개)
    @Schema(description = "진행자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String hostProfileImg;

    @Schema(description = "진행자 연락처", example = "010-1234-5678")
    private String hostPhone;

    @Schema(description = "진행자 생년월일", example = "1990-01-01")
    private String hostBirth;

    @Schema(description = "진행자 주소", example = "서울시 강남구")
    private String hostAddress;

    @Schema(description = "진행자 소개", example = "인디밴드 '별빛' 리더입니다")
    private String hostBio;

    // 담당자 정보 (비공개)
    @Schema(description = "담당자 이름", example = "김담당")
    private String managerName;

    @Schema(description = "담당자 연락처", example = "010-9876-5432")
    private String managerPhone;

    @Schema(description = "담당자 이메일", example = "manager@example.com")
    private String managerEmail;
}
