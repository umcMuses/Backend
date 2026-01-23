package org.muses.backendbulidtest251228.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_managers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectManagerENT {

    // NOTE : ERD는 복합 PK (host_id, project_id) → 단일 PK (project_id)로 변경
    @Id
    @Column(name = "project_id")
    private Long projectId;

    // TODO : Member 엔티티 생성 후 FK 연결 필요 (host_id → Member)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "host_id", nullable = false)
    // private MemberENT host;
    @Column(name = "host_id", nullable = false)
    private Long hostId;

    // =============== 진행자 정보 (공개용) ===============

    @Column(name = "host_profile_img", columnDefinition = "TEXT")
    private String hostProfileImg;

    @Column(name = "host_phone", nullable = false, length = 20)
    private String hostPhone;

    @Column(name = "host_birth", length = 20)
    private String hostBirth;

    @Column(name = "host_address", length = 255)
    private String hostAddress;

    @Column(name = "host_bio", columnDefinition = "TEXT")
    private String hostBio;

    // =============== 담당자 정보 (비공개/관리자용) ===============

    @Column(name = "manager_name", length = 20)
    private String managerName;

    @Column(name = "manager_phone", length = 20)
    private String managerPhone;

    @Column(name = "manager_email", length = 50)
    private String managerEmail;

    // NOTE : ERD는 FK만 있으나, @MapsId로 Project와 PK 공유 (1:1 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "project_id")
    @Setter
    private ProjectENT project;

    // 도메인 메서드 - 진행자 정보 수정
    public void updateHostInfo(String hostProfileImg, String hostPhone, String hostBirth,
                               String hostAddress, String hostBio) {
        this.hostProfileImg = hostProfileImg;
        this.hostPhone = hostPhone;
        this.hostBirth = hostBirth;
        this.hostAddress = hostAddress;
        this.hostBio = hostBio;
    }

    // 도메인 메서드 - 담당자 정보 수정
    public void updateManagerInfo(String managerName, String managerPhone, String managerEmail) {
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.managerEmail = managerEmail;
    }

    // 정적 팩토리 메서드
    public static ProjectManagerENT of(Long hostId, String hostProfileImg, String hostPhone,
                                        String hostBirth, String hostAddress, String hostBio,
                                        String managerName, String managerPhone, String managerEmail) {
        return ProjectManagerENT.builder()
                .hostId(hostId)
                .hostProfileImg(hostProfileImg)
                .hostPhone(hostPhone)
                .hostBirth(hostBirth)
                .hostAddress(hostAddress)
                .hostBio(hostBio)
                .managerName(managerName)
                .managerPhone(managerPhone)
                .managerEmail(managerEmail)
                .build();
    }
}
