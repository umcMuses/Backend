package org.muses.backendbulidtest251228.domain.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectContentENT {

    @Id
    @Column(name = "project_id")
    private Long projectId;

    // MUS-018: 상세 스토리 - 웹 에디터로 작성한 HTML 컨텐츠
    @Column(name = "story_html", nullable = false, columnDefinition = "TEXT")
    private String storyHtml;

    // MUS-019: 환불 정책 - 텍스트만 입력 가능
    @Column(name = "refund_policy", nullable = false, columnDefinition = "TEXT")
    private String refundPolicy;

    @Column(name = "location_detail", length = 255)
    private String locationDetail;

    // NOTE : ERD는 FK만 있으나, @MapsId로 Project와 PK 공유 (1:1 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "project_id")
    @Setter
    private ProjectENT project;

    public void updateStoryHtml(String storyHtml) {
        this.storyHtml = storyHtml;
    }

    public void updateRefundPolicy(String refundPolicy) {
        this.refundPolicy = refundPolicy;
    }

    public void updateLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public static ProjectContentENT of(String storyHtml, String refundPolicy) {
        return ProjectContentENT.builder()
                .storyHtml(storyHtml)
                .refundPolicy(refundPolicy)
                .build();
    }
}
