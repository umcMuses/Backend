package org.muses.backendbulidtest251228.domain.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "알람 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResDT {

    @Schema(description = "유저 알람 ID", example = "1")
    private Long memberAlarmId;

    @Schema(description = "알람 내용 (파라미터 치환 완료)", example = "인디밴드 콘서트가 시작되었습니다")
    private String content;

    @Schema(description = "알람 발생 시간", example = "2025-01-22T12:00:00")
    private LocalDateTime alarmTime;

    // MyBatis 매핑용 (내부 사용)
    @Schema(hidden = true)
    private String template;

    @Schema(hidden = true)
    private String alarmParams;
}
