package org.muses.backendbulidtest251228.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 프로필 조회/수정 응답")
public class MyProfileResDT {

    @Schema(description = "회원 ID", example = "2")
    private Long memberId;

    @Schema(description = "이름", example = "지원이")
    private String name;

    @Schema(description = "이메일", example = "2jw0305@gmail.com")
    private String email;

    @Schema(description = "닉네임", example = "리브")
    private String nickName;

    @Schema(description = "소개글", example = "재즈 공연 좋아합니다.")
    private String introduction;

    @Schema(description = "생년월일(yyyy-MM-dd)", example = "2003-05-30")
    private String birthday;

    @Schema(description = "성별(0=남자, 1=여자)", example = "1", allowableValues = {"0", "1"})
    private Integer gender;

    @Schema(description = "프로필 이미지 URL", example = "http://localhost:9098/files/member/2/profile.png")
    private String profileImgUrl;

    @Schema(description = "보유 티켓 수", example = "3")
    private Integer ticketCount;

    @Schema(description = "후원 횟수", example = "5")
    private Integer supportCount;

    @Schema(description = "후원 레벨", example = "2")
    private Integer supportLevel;
}
