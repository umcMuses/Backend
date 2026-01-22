package org.muses.backendbulidtest251228.domain.alarm.service;

import org.muses.backendbulidtest251228.domain.alarm.dto.AlarmResDT;

import java.util.List;
import java.util.Map;

/**
 * 알람 서비스 인터페이스
 * 
 * NOTE: 알람 전송기능 사용방법
 * =========================================
 * 
 * 1. 알람 템플릿 작성법 (alarm 테이블의 alarm_context 컬럼)
 *    - 템플릿 본문에 ${key} 형태로 치환 가능한 변수를 삽입합니다.
 *    - 예시: "${projectName} 프로젝트가 시작되었습니다"
 *    - 예시: "${userName}님이 ${projectName}에 후원했습니다"
 * 
 * 2. 알람 발송 시 params 전달 방법
 *    - Map<String, String> 형태로 key-value 쌍을 전달합니다.
 *    - key는 템플릿의 ${key}와 일치해야 합니다.
 *    
 *    예시 코드:
 *    ```java
 *    // 단일 유저에게 발송
 *    alarmSRV.send(
 *        memberId,                                    // 수신자 ID
 *        1L,                                          // 알람 템플릿 ID
 *        Map.of("projectName", "인디밴드 콘서트")       // 치환할 파라미터
 *    );
 *    
 *    // 여러 파라미터 전달
 *    alarmSRV.send(
 *        memberId,
 *        2L,
 *        Map.of(
 *            "userName", "홍길동",
 *            "projectName", "인디밴드 콘서트",
 *            "amount", "50,000원"
 *        )
 *    );
 *    
 *    // 여러 유저에게 동시 발송
 *    alarmSRV.sendToMany(
 *        List.of(1L, 2L, 3L),                         // 수신자 ID 목록
 *        1L,
 *        Map.of("projectName", "인디밴드 콘서트")
 *    );
 *    ```
 * 
 * 3. 이스케이프 규칙 (특수문자를 그대로 출력하고 싶을 때)
 *    - 백슬래시(\)를 특수문자 앞에 붙이면 해당 문자는 키워드로 인식되지 않습니다.
 *    
 *    | 입력      | 출력   | 설명                          |
 *    |-----------|--------|-------------------------------|
 *    | \\        | \      | 백슬래시 출력                  |
 *    | \$        | $      | 달러 기호 출력 (치환 방지)      |
 *    | \{        | {      | 여는 중괄호 출력               |
 *    | \}        | }      | 닫는 중괄호 출력               |
 *    | ${key}    | (값)   | params에서 key에 해당하는 값   |
 *    
 *    예시:
 *    - 템플릿: "가격은 \${price}입니다"     → 결과: "가격은 ${price}입니다"
 *    - 템플릿: "경로: C:\\Users\\test"     → 결과: "경로: C:\Users\test"
 *    - 템플릿: "JSON: \{\"name\": \"test\"\}" → 결과: "JSON: {"name": "test"}"
 * 
 * 4. 치환 예시
 *    템플릿: "${projectName} 프로젝트가 시작되었습니다"
 *    params: {"projectName": "인디밴드 콘서트"}
 *    결과:   "인디밴드 콘서트 프로젝트가 시작되었습니다"
 *    
 *    템플릿: "${userName}님, \${amount} 쿠폰이 지급되었습니다"
 *    params: {"userName": "홍길동"}
 *    결과:   "홍길동님, ${amount} 쿠폰이 지급되었습니다"
 * 
 * 5. 주의사항
 *    - params에 없는 key는 ${key} 그대로 출력됩니다.
 *    - params가 null이거나 비어있어도 이스케이프 처리는 동작합니다.
 *    - 알람은 members_alarm 테이블에 저장되며, 조회 시점에 치환됩니다.
 * =========================================
 */
public interface AlarmSRV {

    /**
     * 알람 발송 (다른 도메인에서 호출)
     * 
     * @param memberId 수신자 회원 ID
     * @param alarmId 알람 템플릿 ID (alarm 테이블의 PK)
     * @param params 템플릿에 삽입할 파라미터 (key-value), null 가능
     * 
     * @see #sendToMany(List, Long, Map) 여러 유저에게 발송 시
     */
    void send(Long memberId, Long alarmId, Map<String, String> params);

    /**
     * 여러 유저에게 알람 발송
     * 
     * @param memberIds 수신자 회원 ID 목록
     * @param alarmId 알람 템플릿 ID
     * @param params 템플릿에 삽입할 파라미터
     */
    void sendToMany(List<Long> memberIds, Long alarmId, Map<String, String> params);

    /**
     * 내 알람 목록 조회 (최신 20개)
     * - SecurityContext에서 현재 로그인한 유저 정보를 가져옵니다.
     * - is_active = 1인 알람만 조회됩니다.
     * 
     * @return 알람 목록 (템플릿 치환 완료된 상태)
     */
    List<AlarmResDT> getMyAlarms();

    /**
     * 알람 삭제 (soft delete)
     * - is_active를 0으로 변경합니다.
     * - 본인의 알람만 삭제 가능합니다.
     * 
     * @param memberAlarmId 유저 알람 ID (members_alarm 테이블의 PK)
     */
    void deleteAlarm(Long memberAlarmId);

    /**
     * 내 활성 알람 개수 조회
     * 
     * @return is_active = 1인 알람 개수
     */
    int countMyAlarms();
}
