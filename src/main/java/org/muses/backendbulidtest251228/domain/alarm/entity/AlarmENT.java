package org.muses.backendbulidtest251228.domain.alarm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlarmENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    // 알람 템플릿 본문 (예: "${projectName}가 시작되었습니다")
    @Column(name = "alarm_context", nullable = false, columnDefinition = "TEXT")
    private String alarmContext;
}
