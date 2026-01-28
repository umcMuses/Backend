package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyTicketResDT {

    private Long ticketId;
    private String projectTitle;
    private LocalDateTime opening;
    private String optionLabel;
    private String ticketToken;
    private String status;
}
