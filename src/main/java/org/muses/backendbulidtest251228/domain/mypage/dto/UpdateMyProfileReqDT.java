package org.muses.backendbulidtest251228.domain.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMyProfileReqDT {

    @NotBlank
    @Size(max = 50)
    private String nickName;

    @NotBlank
    @Size(max = 500)
    private String introduction;

    @NotBlank
    private String birthday;

    private Integer gender;
}
