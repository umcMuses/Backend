package org.muses.backendbulidtest251228.domain.order.entity;
import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.payment.entity.PaymentENT;
import org.muses.backendbulidtest251228.domain.temp.Member;
import org.muses.backendbulidtest251228.domain.temp.Project;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    // 주문자 (회원 1 : 주문 N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    // 대상 프로젝트 (프로젝트 1 : 주문 N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    //강결합, 주문 생성시 같이 생성
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemENT> orderItems = new ArrayList<>();

    // 별도 생성
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private BillingAuthENT billingAuth;

    // 별도 생성
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private PaymentENT payment;

    // ================== 연관관계 편의 메서드 ==================

    public void addItem(OrderItemENT item) {
        this.orderItems.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItemENT item) {
        this.orderItems.remove(item);
        item.setOrder(null);
    }

    // ================== 도메인 메서드 ==================

    public void changeTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }
}
