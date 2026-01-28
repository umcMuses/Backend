package org.muses.backendbulidtest251228.domain.project.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.alarm.service.AlarmSRV;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectLikeRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 프로젝트 오픈 알람 스케줄러
 * 
 * - APPROVED 상태이면서 opening 시간이 된 프로젝트를 찾아서
 * - 해당 프로젝트에 좋아요 누른 멤버들에게 알람 발송
 * - 프로젝트 상태를 FUNDING으로 변경
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectOpeningScheduler {

    private final ProjectRepo projectRepo;
    private final ProjectLikeRepo projectLikeRepo;
    private final AlarmSRV alarmSRV;

    /**
     * 1분마다 실행 - 오픈 시간 체크
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void checkProjectOpening() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);

        // opening 시간이 방금 지난 APPROVED 프로젝트 조회
        List<ProjectENT> openingProjects = projectRepo.findByStatusApprovedAndOpeningBetween(oneMinuteAgo, now);

        if (openingProjects.isEmpty()) {
            return;
        }

        log.info("[SCHEDULER] 오픈 예정 프로젝트 {}개 발견", openingProjects.size());

        for (ProjectENT project : openingProjects) {
            try {
                processProjectOpening(project);
            } catch (Exception e) {
                log.error("[SCHEDULER] 프로젝트 오픈 처리 실패 - projectId: {}, error: {}", 
                    project.getId(), e.getMessage());
            }
        }
    }

    /**
     * 프로젝트 펀딩 여는 내부함수
     * @param project
     */
    private void processProjectOpening(ProjectENT project) {
        // PURPOSE 좋아요 누른 멤버들 조회
        List<Long> memberIds = projectLikeRepo.findMemberIdsByProjectId(project.getId());

        if (!memberIds.isEmpty()) {
            // PURPOSE 알람 발송 (템플릿 ID: 3 = 펀딩 시작 알람)
            alarmSRV.sendToMany(
                memberIds,
                3L,
                Map.of("projectName", project.getTitle())
            );

            log.info("[SCHEDULER] 오픈 알람 발송 완료 - projectId: {}, memberCount: {}", 
                project.getId(), memberIds.size());
        }

        // PURPOSE 상태 변경: APPROVED -> FUNDING
        project.updateStatus("FUNDING");
        project.updateFundingStatus(FundingStatus.FUNDING);

        log.info("[SCHEDULER] 프로젝트 오픈 완료 - projectId: {}, title: {}", 
            project.getId(), project.getTitle());
    }
}
