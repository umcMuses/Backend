package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreatorApplicationSubmitResDT {
    private Long applicationId;
    private String status;
    private boolean submitted;      // 검증 통과 여부
    private List<String> required;  // 요구 서류
    private List<String> uploaded;  // 업로드된 서류
    private List<String> missing;   // 누락 서류
}
