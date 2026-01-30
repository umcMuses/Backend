package org.muses.backendbulidtest251228.domain.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;

@Schema(description = "첨부파일 응답 DTO")
@Getter
@Builder
@AllArgsConstructor
public class AttachmentResponseDT {

    @Schema(description = "첨부파일 ID", example = "1")
    private Long id;

    @Schema(description = "파일 URL", example = "http://localhost:9098/files/project/1/abc123.jpg")
    private String fileUrl;

    @Schema(description = "원본 파일명", example = "concert_poster.jpg")
    private String originalFilename;

    @Schema(description = "파일 확장자", example = "jpg")
    private String extension;

    public static AttachmentResponseDT from(AttachmentENT entity) {
        return AttachmentResponseDT.builder()
                .id(entity.getId())
                .fileUrl(entity.getFileUrl())
                .originalFilename(entity.getOriginalFilename())
                .extension(entity.getExtension())
                .build();
    }
}
