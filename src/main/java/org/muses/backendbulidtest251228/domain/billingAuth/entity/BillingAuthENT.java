package org.muses.backendbulidtest251228.domain.billingAuth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.billingAuth.enums.BillingAuthStatus;
import org.muses.backendbulidtest251228.domain.billingAuth.enums.PgProvider;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "billing_auth",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_billing_auth_billing_key", columnNames = "billing_key"),
                @UniqueConstraint(name = "uk_billing_auth_order_id", columnNames = "order_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BillingAuthENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_auth_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @Setter
    private OrderENT order;

    @Column(name = "billing_key", nullable = false, unique = true)
    private String billingKey;

    @Column(name = "customer_key", nullable = false)
    private String customerKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BillingAuthStatus status; // ACTIVE, REVOKED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private PgProvider provider; // TOSS

    @Column(name = "card_company")
    private String cardCompany;

    @Column(name = "card_number_masked")
    private String cardNumberMasked;


    public static BillingAuthENT active(OrderENT order, String customerKey, String billingKey,
                                     String cardCompany, String cardNumberMasked) {
        return BillingAuthENT.builder()
                .order(order)
                .customerKey(customerKey)
                .billingKey(billingKey)
                .status(BillingAuthStatus.ACTIVE)
                .provider(PgProvider.TOSS)
                .cardCompany(cardCompany)
                .cardNumberMasked(cardNumberMasked)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void revoke() {
        this.status = BillingAuthStatus.REVOKED;
        this.revokedAt = LocalDateTime.now();
    }

}

