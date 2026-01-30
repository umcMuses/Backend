package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatorApplicationDocResDT {
    private Long docId;
    private String docType;
    private Long attachmentId;
    private String fileUrl;
    private String originalFilename;
    private String extension;
}
