package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
public class CreatorApplicationDocResDT {
    private Long docId;
    private String docType;

    @Schema(description = "첨부파일 ID(attachment.id)", example = "18")
    private Long attachmentId;

    @Schema(description = "파일 접근 URL", example = "http://localhost:9098/files/member/2/abc123.pdf")
    private String fileUrl;

    @Schema(description = "원본 파일명", example = "bankbook.pdf")
    private String originalFilename;

    @Schema(description = "확장자", example = "pdf")
    private String extension;
}
