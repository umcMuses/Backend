package org.muses.backendbulidtest251228.domain.settlement.entity;


import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.settlement.enums.SettlementStatus;

import java.math.BigDecimal;

@Entity
@Table(
        name = "settlements",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "project_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SettlementENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    // 1대1 단방향
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectENT project;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SettlementStatus status;

    @Column(name = "fee_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal feeAmount;

    @Column(name = "payout_amount", precision = 15, scale = 2)
    private BigDecimal payoutAmount;


    public void updateAmount(BigDecimal totalSum, BigDecimal feeAmount, BigDecimal payoutAmount) {
        this.totalAmount = totalSum;
        this.feeAmount = feeAmount;
        this.payoutAmount = payoutAmount;
    }
}

