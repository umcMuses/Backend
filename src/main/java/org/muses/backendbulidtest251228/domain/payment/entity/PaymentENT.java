package org.muses.backendbulidtest251228.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payments_order_id", columnNames = "order_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @Setter
    private OrderENT order;

    @Column(name = "idem_key")
    private String idemKey;

    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "fail_reason")
    private String failReason;



    public void setTime() {
        this.approvedAt = LocalDateTime.now();

    }

    public void markRequested(){
        this.status = PaymentStatus.READY;
    }


    public void markSuccess(String paymentKey, String res) {
        this.status = PaymentStatus.SUCCESS;
        this.paymentKey = paymentKey;
        this.responseBody = res;
        this.approvedAt = LocalDateTime.now();
    }

    public void markFailed(String reason, String res) {
        if (this.status == PaymentStatus.SUCCESS) return;
        this.status = PaymentStatus.FAIL;
        this.failReason = reason;
        this.responseBody = res;
        this.approvedAt = LocalDateTime.now();
    }


}

