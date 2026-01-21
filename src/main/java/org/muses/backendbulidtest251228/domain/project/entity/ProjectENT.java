package org.muses.backendbulidtest251228.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.project.dto.request.FundingRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.OutlineRequestDTO;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.enums.FundingType;
import org.muses.backendbulidtest251228.domain.project.enums.Region;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    // TODO : Member 엔티티 생성 후 FK 연결 필요
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable = false)
    // private MemberENT member;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "last_saved_step", nullable = false)
    private Integer lastSavedStep;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // NOTE : ERD에 없음 - 대표 이미지 URL 추가 (MUS-013)
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_limit", length = 20)
    private AgeLimit ageLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    @Column(name = "target_amount", precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "opening")
    private LocalDateTime opening;

    // NOTE : ERD에 없음 - 펀딩 방식 추가 (MUS-014)
    @Enumerated(EnumType.STRING)
    @Column(name = "funding_type", length = 20)
    private FundingType fundingType;

    @Column(name = "achieve_rate", nullable = false)
    @Builder.Default
    private Integer achieveRate = 0;

    @Column(name = "supporter_count", nullable = false)
    @Builder.Default
    private Integer supporterCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "funding_status")
    @Builder.Default
    private FundingStatus fundingStatus = FundingStatus.PREPARING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectContentENT projectContent;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectManagerENT projectManager;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectTagENT> projectTags = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RewardENT> rewards = new ArrayList<>();

    // 연관관계 편의 메서드
    public void setProjectContent(ProjectContentENT content) {
        this.projectContent = content;
        content.setProject(this);
    }

    public void setProjectManager(ProjectManagerENT manager) {
        this.projectManager = manager;
        manager.setProject(this);
    }

    public void addTag(ProjectTagENT tag) {
        this.projectTags.add(tag);
        tag.setProject(this);
    }

    public void addReward(RewardENT reward) {
        this.rewards.add(reward);
        reward.setProject(this);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========================================
    // TODO: 컨버터 사용 여부 확인

    // 1단계: 개요 업데이트
    public void updateOutline(OutlineRequestDTO dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.thumbnailUrl = dto.getThumbnailUrl();
        this.ageLimit = dto.getAgeLimit();
        this.region = dto.getRegion();
    }

    // 2단계: 펀딩 업데이트
    public void updateFunding(FundingRequestDTO dto) {
        this.targetAmount = dto.getTargetAmount();
        this.opening = dto.getOpening();
        this.deadline = dto.getDeadline();
        this.fundingType = dto.getFundingType();
    }

    // 저장 단계 업데이트
    public void updateLastSavedStep(int step) {
        if (step > this.lastSavedStep) {
            this.lastSavedStep = step;
        }
    }

    // 상태 업데이트
    public void updateStatus(String status) {
        this.status = status;
    }

    // 펀딩 상태 업데이트
    public void updateFundingStatus(FundingStatus fundingStatus) {
        this.fundingStatus = fundingStatus;
    }
}
