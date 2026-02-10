package org.muses.backendbulidtest251228.domain.event.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "이벤트 상세 + 이전/다음 ID 응답 DTO")
public class EventDetailResDTO {

    @Schema(description = "이벤트 상세 정보")
    private EventResDTO event;

    @Schema(description = "이전 이벤트 ID (더 최근 글) 없으면 null", example = "10", nullable = true)
    private Long prevId;

    @Schema(description = "다음 이벤트 ID (더 오래된 글) 없으면 null", example = "2", nullable = true)
    private Long nextId;
}
