package org.muses.backendbulidtest251228.domain.checkin.entity;


import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_checkin_links",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "project_id"),
                @UniqueConstraint(columnNames = "token")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectCheckinLinkENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkin_link_id")
    private Long id;

    // 1대1 단방향
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectENT project;

    @Column(nullable = false, length = 100)
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
