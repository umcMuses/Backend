package org.muses.backendbulidtest251228.domain.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RewardENT {

    // NOTE : ERD는 복합 PK (reward_id, project_id) → 단일 PK (reward_id)로 변경
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long id;

    // MUS-017: 리워드 이름 (예: VIP석, 일반권 등)
    @Column(name = "reward_name", nullable = false, length = 100)
    private String rewardName;

    // MUS-017: 리워드 금액
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    // MUS-017: 리워드 설명
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // MUS-015, MUS-017: 제한 수량 (리워드별 수량 설정)
    @Column(name = "total_quantity")
    private Integer totalQuantity;

    // 판매된 수량 (실시간 집계)
    @Column(name = "sold_quantity")
    @Builder.Default
    private Integer soldQuantity = 0;

    @Column(name = "entry_at")
    private LocalDateTime entryAt;

    // MUS-017: QR 발급 여부 (TICKET: QR 발급, NONE: QR 미발급)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RewardType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @Setter
    private ProjectENT project;

    // 도메인 메서드 - 리워드 정보 수정
    public void updateRewardInfo(String rewardName, BigDecimal price, String description,
                                  Integer totalQuantity, RewardType type) {
        this.rewardName = rewardName;
        this.price = price;
        this.description = description;
        this.totalQuantity = totalQuantity;
        this.type = type;
    }

    // 도메인 메서드 - 판매 수량 증가
    public void increaseSoldQuantity(int quantity) {
        this.soldQuantity += quantity;
    }

    // 도메인 메서드 - 재고 확인
    public boolean hasStock(int requestedQuantity) {
        if (this.totalQuantity == null) {
            return true; // 수량 제한 없음
        }
        return (this.totalQuantity - this.soldQuantity) >= requestedQuantity;
    }

    // 도메인 메서드 - 남은 수량 조회
    public Integer getRemainingQuantity() {
        if (this.totalQuantity == null) {
            return null; // 수량 제한 없음
        }
        return this.totalQuantity - this.soldQuantity;
    }

    // 정적 팩토리 메서드
    public static RewardENT of(String rewardName, BigDecimal price, String description,
                                Integer totalQuantity, RewardType type) {
        return RewardENT.builder()
                .rewardName(rewardName)
                .price(price)
                .description(description)
                .totalQuantity(totalQuantity)
                .type(type)
                .build();
    }
}
