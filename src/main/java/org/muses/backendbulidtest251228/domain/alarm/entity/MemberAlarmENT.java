package org.muses.backendbulidtest251228.domain.alarm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Table(name = "members_alarm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberAlarmENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id", nullable = false)
    private AlarmENT alarm;

    @Column(name = "alarm_time", nullable = false)
    private LocalDateTime alarmTime;

    // 1 = 표시, 그 외 삭제
    @Column(name = "alarm_is_active", nullable = false)
    @Builder.Default
    private Integer isActive = 1;

    // JSON 형태로 저장할 파라미터 (예: {"projectName": "인디밴드 콘서트"})
    @Column(name = "alarm_params", columnDefinition = "TEXT")
    private String alarmParams;

    // 알람 삭제 (soft delete)
    public void deactivate() {
        this.isActive = 0;
    }

    // 정적 팩토리 메서드
    public static MemberAlarmENT of(Member member, AlarmENT alarm, String params) {
        return MemberAlarmENT.builder()
                .member(member)
                .alarm(alarm)
                .alarmTime(LocalDateTime.now())
                .isActive(1)
                .alarmParams(params)
                .build();
    }
}
