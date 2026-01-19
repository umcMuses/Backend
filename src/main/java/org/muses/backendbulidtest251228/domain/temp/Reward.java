package org.muses.backendbulidtest251228.domain.temp;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 리워드 명칭 (예: 얼리버드 세트, 감사 굿즈 등)
    @Column(nullable = false)
    private String title;

    // 리워드 상세 설명
    @Column(columnDefinition = "TEXT")
    private String content;

    // 리워드 금액 (BigDecimal 권장)
    @Column(nullable = false)
    private BigDecimal price;

}
