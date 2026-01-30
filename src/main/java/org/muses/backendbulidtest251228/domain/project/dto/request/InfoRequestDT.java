package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "5단계: 정보 저장 요청 DTO - 프로젝트 진행자 및 담당자 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InfoRequestDT {

    // ========== 진행자 정보 (프로젝트 페이지에 공개) ==========
    @Schema(description = "[공개] 진행자 프로필 이미지 URL", example = "https://storage.example.com/profiles/host1.jpg")
    private String hostProfileImg;

    @Schema(description = "[공개] 진행자 연락처 (후원자 문의용)", example = "010-1234-5678")
    private String hostPhone;

    @Schema(description = "[비공개] 진행자 생년월일 (정산용, YYYY-MM-DD 형식)", example = "1990-05-15")
    private String hostBirth;

    @Schema(description = "[비공개] 진행자 주소 (정산용)", example = "서울특별시 강남구 테헤란로 123")
    private String hostAddress;

    @Schema(description = "[공개] 진행자 소개 (프로젝트 페이지에 표시)", example = "인디밴드 '별빛'의 리더이자 보컬을 맡고 있습니다. 음악으로 여러분과 소통하고 싶습니다.")
    private String hostBio;

    // ========== 담당자 정보 (플랫폼 내부용, 비공개) ==========
    @Schema(description = "[비공개] 담당자 이름 (선택, 진행자와 다른 경우)", example = "김매니저")
    private String managerName;

    @Schema(description = "[비공개] 담당자 연락처 (선택, 긴급 연락용)", example = "010-9876-5432")
    private String managerPhone;

    @Schema(description = "[비공개] 담당자 이메일 (선택, 공지/안내 수신용)", example = "manager@example.com")
    private String managerEmail;
}
