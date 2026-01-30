package org.muses.backendbulidtest251228.domain.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
public class UpdateMyProfileReqDT {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "닉네임 (중복 불가)", example = "리브")
    private String nickName;

    @NotBlank
    @Size(max = 500)
    @Schema(description = "자기소개", example = "안녕하세요. 공연 좋아해요!")
    private String introduction;

    @NotBlank
    @Schema(description = "생년월일 (yyyy-MM-dd)", example = "2003-05-30")
    private String birthday;

    @Schema(description = "성별 (0=남, 1=여)", example = "0")
    private Integer gender;
}
