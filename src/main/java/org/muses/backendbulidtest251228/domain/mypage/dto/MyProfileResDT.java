package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyProfileResDT {

    private Long memberId;

    private String name;
    private String email;
    private String nickName;

    private String introduction;
    private String birthday;
    private Integer gender;       // 0 남자, 1 여자

    private String profileImgUrl;

    private Integer ticketCount;
    private Integer supportCount;
    private Integer supportLevel;
}
