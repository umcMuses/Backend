package org.muses.backendbulidtest251228.domain.mypage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.domain.mypage.enums.CreatorType;
import org.muses.backendbulidtest251228.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "creator_applications")
public class CreatorApplicationENT extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreatorType creatorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    public static CreatorApplicationENT create(Member member, CreatorType type) {
        CreatorApplicationENT ent = new CreatorApplicationENT();
        ent.member = member;
        ent.creatorType = type;
        ent.status = ApplicationStatus.PENDING;
        return ent;
    }
}
