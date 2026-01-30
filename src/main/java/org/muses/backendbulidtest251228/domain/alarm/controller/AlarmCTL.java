package org.muses.backendbulidtest251228.domain.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.alarm.dto.AlarmResDT;
import org.muses.backendbulidtest251228.domain.alarm.service.AlarmSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Alarm", description = "알람 API")
@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmCTL {

    private final AlarmSRV alarmSRV;

    @Operation(summary = "내 알람 목록 조회", description = "활성 상태인 알람 최신 20개를 조회 합니ㅏㄷ")
    @GetMapping
    public ApiResponse<List<AlarmResDT>> getMyAlarms() {
        List<AlarmResDT> alarms = alarmSRV.getMyAlarms();
        return ApiResponse.success(alarms);
    }

    @Operation(summary = "내 알람 개수 조회", description = "활성 상태인 알람 개수를 조회합니다")
    @GetMapping("/count")
    public ApiResponse<Integer> countMyAlarms() {
        int count = alarmSRV.countMyAlarms();
        return ApiResponse.success(count);
    }

    @Operation(summary = "알람 닫기", description = "알람을 닫기(비활성화)합니다")
    @DeleteMapping("/{memberAlarmId}")
    public ApiResponse<Void> deleteAlarm(@PathVariable Long memberAlarmId) {
        alarmSRV.deleteAlarm(memberAlarmId);
        return ApiResponse.success(null);
    }
}
