package org.muses.backendbulidtest251228.domain.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.global.entity.BaseEntity;

@Entity
@Table(name = "attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachmentENT extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false, length = 500)
    private String fileUrl;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false, length = 20)
    private String extension;

    @Builder
    public AttachmentENT(String targetType, Long targetId, String fileUrl, String originalFilename, String extension) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.fileUrl = fileUrl;
        this.originalFilename = originalFilename;
        this.extension = extension;
    }

    public static AttachmentENT of(String targetType, Long targetId, String fileUrl, String originalFilename, String extension) {
        return AttachmentENT.builder()
                .targetType(targetType)
                .targetId(targetId)
                .fileUrl(fileUrl)
                .originalFilename(originalFilename)
                .extension(extension)
                .build();
    }
}
