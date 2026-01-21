package org.muses.backendbulidtest251228.domain.temp;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    // 회원과의 관계 (외래키)
    // 실제 구현 시 User 엔티티와의 @ManyToOne 관계 설정을 추천합니다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // 하단 노트 참고 (제출 상태)

    @Column(name = "last_saved_step", nullable = false)
    private Integer lastSavedStep; // 1 ~ 5

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "age_limit", nullable = false, length = 20)
    private String ageLimit; // ALL, ADULT

    @Column(name = "region", nullable = false)
    @Enumerated(EnumType.STRING) // ENUM 타입 처리
    private Region region;

    @Column(name = "target_amount", precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "opening")
    private LocalDateTime opening;

    @Column(name = "achieve_rate", nullable = false)
    private Integer achieveRate; // 실시간 업데이트

    @Column(name = "supporter_count", nullable = false)
    private Integer supporterCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "funding_status", nullable = false)
    private FundingStatus fundingStatus;

    // 펀딩 성공 여부
    public boolean isGoalAchieved() {
        return achieveRate >= 100;
    }

}