package org.muses.backendbulidtest251228.domain.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Schema(description = "알람 발송 요청 DTO (내부 호출용)")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmSendReqDT {

    @Schema(description = "수신자 회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "알람 템플릿 ID", example = "1")
    private Long alarmId;

    @Schema(description = "템플릿에 삽입할 파라미터", example = "{\"projectName\": \"인디밴드 콘서트\"}")
    private Map<String, String> params;
}
