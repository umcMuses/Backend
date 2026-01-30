package org.muses.backendbulidtest251228.domain.mypage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.enums.DocType;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "creator_application_docs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_app_doctype", columnNames = {"app_id", "doc_type"})
        }
)
public class CreatorApplicationDocENT extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    private CreatorApplicationENT application;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 50)
    private DocType docType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private AttachmentENT attachment;

    private CreatorApplicationDocENT(CreatorApplicationENT application, DocType docType, AttachmentENT attachment) {
        this.application = application;
        this.docType = docType;
        this.attachment = attachment;
    }

    public static CreatorApplicationDocENT of(CreatorApplicationENT application, DocType docType, AttachmentENT attachment) {
        return new CreatorApplicationDocENT(application, docType, attachment);
    }
}
