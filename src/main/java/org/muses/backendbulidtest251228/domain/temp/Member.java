package org.muses.backendbulidtest251228.domain.temp;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;



    @Column(name = "provider_id", length = 255)
    private String providerId;



    @Column(name = "ticket_count", nullable = false)
    private Integer ticketCount;

    @Column(name = "support_count", nullable = false)
    private Integer supportCount;

    @Column(name = "support_level", nullable = false)
    private Integer supportLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

