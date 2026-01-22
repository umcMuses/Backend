package org.muses.backendbulidtest251228.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectTagENT {

    // NOTE : ERD는 복합 PK (tag_id, project_id) → 단일 PK (tag_id)로 변경
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    // MUS-013: 기본 정보 - 태그 설정
    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @Setter
    private ProjectENT project;

    // 정적 팩토리 메서드
    public static ProjectTagENT of(String tagName) {
        return ProjectTagENT.builder()
                .tagName(tagName)
                .build();
    }
}
