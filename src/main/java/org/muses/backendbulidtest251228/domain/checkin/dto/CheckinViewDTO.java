package org.muses.backendbulidtest251228.domain.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckinViewDTO {

    private Long projectId;
    private String buyerName;
    private String buyerNickname;
    private String rewardTitle;
    private Integer quantity;
}
