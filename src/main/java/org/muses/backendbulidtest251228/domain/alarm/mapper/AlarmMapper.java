package org.muses.backendbulidtest251228.domain.alarm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.muses.backendbulidtest251228.domain.alarm.dto.AlarmResDT;

import java.util.List;

@Mapper
public interface AlarmMapper {

    // 알람 템플릿 조회
    String findAlarmContextById(@Param("alarmId") Long alarmId);

    // 알람 템플릿 존재 여부 확인
    boolean existsAlarmById(@Param("alarmId") Long alarmId);

    // 회원 알람 저장
    void insertMemberAlarm(
            @Param("memberId") Long memberId,
            @Param("alarmId") Long alarmId,
            @Param("alarmParams") String alarmParams
    );

    // 특정 유저의 활성 알람 조회 (최신순, 페이징)
    List<AlarmResDT> findActiveAlarmsByMemberId(
            @Param("memberId") Long memberId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 특정 유저의 활성 알람 개수
    int countActiveAlarmsByMemberId(@Param("memberId") Long memberId);

    // 알람 비활성화 (삭제)
    int deactivateAlarm(
            @Param("memberAlarmId") Long memberAlarmId,
            @Param("memberId") Long memberId
    );

    // 회원 알람 존재 및 소유자 확인
    Long findMemberIdByMemberAlarmId(@Param("memberAlarmId") Long memberAlarmId);
}
