package org.muses.backendbulidtest251228.domain.alarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.alarm.dto.AlarmResDT;
import org.muses.backendbulidtest251228.domain.alarm.mapper.AlarmMapper;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.global.security.PrincipalDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AlarmSRVI implements AlarmSRV {

    private final AlarmMapper alarmMapper;
    private final MemberRepo memberRepo;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long memberId, Long alarmId, Map<String, String> params) {
        // 회원 존재 확인
        if (!memberRepo.existsById(memberId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다.",
                    Map.of("memberId", memberId));
        }

        // 알람 템플릿 존재 확인
        if (!alarmMapper.existsAlarmById(alarmId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "알람 템플릿을 찾을 수 없습니다.",
                    Map.of("alarmId", alarmId));
        }

        String paramsJson = toJson(params);
        alarmMapper.insertMemberAlarm(memberId, alarmId, paramsJson);

        log.info("[ALARM] 알람 발송 완료 - memberId: {}, alarmId: {}", memberId, alarmId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendToMany(List<Long> memberIds, Long alarmId, Map<String, String> params) {
        // 알람 템플릿 존재 확인
        if (!alarmMapper.existsAlarmById(alarmId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "알람 템플릿을 찾을 수 없습니다.",
                    Map.of("alarmId", alarmId));
        }

        String paramsJson = toJson(params);

        for (Long memberId : memberIds) {
            try {
                if (memberRepo.existsById(memberId)) {
                    alarmMapper.insertMemberAlarm(memberId, alarmId, paramsJson);
                }
            } catch (Exception e) {
                log.warn("[ALARM] 알람 발송 실패 - memberId: {}, error: {}", memberId, e.getMessage());
            }
        }

        log.info("[ALARM] 다중 알람 발송 완료 - memberCount: {}, alarmId: {}", memberIds.size(), alarmId);
    }

    @Override
    public List<AlarmResDT> getMyAlarms() {
        Long memberId = resolveCurrentMemberId();

        List<AlarmResDT> alarms = alarmMapper.findActiveAlarmsByMemberId(memberId, 20, 0);

        // 템플릿 + 파라미터 → content 변환
        for (AlarmResDT alarm : alarms) {
            String content = replaceParams(alarm.getTemplate(), alarm.getAlarmParams());
            alarm.setContent(content);
            // 내부 필드 숨김
            alarm.setTemplate(null);
            alarm.setAlarmParams(null);
        }

        return alarms;
    }

    @Override
    @Transactional
    public void deleteAlarm(Long memberAlarmId) {
        Long memberId = resolveCurrentMemberId();

        // 알람 소유자 확인
        Long ownerId = alarmMapper.findMemberIdByMemberAlarmId(memberAlarmId);
        if (ownerId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "알람을 찾을 수 없습니다.",
                    Map.of("memberAlarmId", memberAlarmId));
        }

        if (!ownerId.equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 알람만 삭제할 수 있습니다.");
        }

        int updated = alarmMapper.deactivateAlarm(memberAlarmId, memberId);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "알람을 찾을 수 없습니다.",
                    Map.of("memberAlarmId", memberAlarmId));
        }

        log.info("[ALARM] 알람 삭제 완료 - memberAlarmId: {}", memberAlarmId);
    }

    @Override
    public int countMyAlarms() {
        Long memberId = resolveCurrentMemberId();
        return alarmMapper.countActiveAlarmsByMemberId(memberId);
    }

    // ==================== Private Methods ====================

    // NOTE : 템플릿에서 ${key}를 실제 값으로 치환
    private String replaceParams(String template, String paramsJson) {
        if (template == null) {
            return "";
        }
        if (paramsJson == null || paramsJson.isBlank()) {
            return processEscapes(template);
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> params = objectMapper.readValue(paramsJson, Map.class);

            String result = template;
            result = result.replace("\\\\", "##BACKSLASH##");
            result = result.replace("\\$", "##DOLLAR##");
            result = result.replace("\\{", "##LBRACE##");
            result = result.replace("\\}", "##RBRACE##");

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result = result.replace("${" + entry.getKey() + "}", entry.getValue());
            }

            result = result.replace("##BACKSLASH##", "\\");
            result = result.replace("##DOLLAR##", "$");
            result = result.replace("##LBRACE##", "{");
            result = result.replace("##RBRACE##", "}");

            return result;
        } catch (JsonProcessingException e) {
            log.warn("[ALARM] 파라미터 파싱 실패: {}", e.getMessage());
            return processEscapes(template);
        }
    }

    private String processEscapes(String template) {
        String result = template;
        result = result.replace("\\\\", "\\");
        result = result.replace("\\$", "$");
        result = result.replace("\\{", "{");
        result = result.replace("\\}", "}");
        return result;
    }

    private String toJson(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            log.warn("[ALARM] JSON 변환 실패: {}", e.getMessage());
            return null;
        }
    }

    // ==================== JWT 기반 유저 식별 ====================

    private Long resolveCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof PrincipalDetails principalDetails) {
            return principalDetails.getMemberId();
        }

        throw new BusinessException(ErrorCode.AUTH_INVALID,
                "principal에서 memberId를 찾을 수 없습니다.",
                Map.of("principalClass", principal.getClass().getName()));
    }
}
