package org.muses.backendbulidtest251228.domain.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTxSRV {

    private final ProjectRepo projectRepo;

    //선점 쿼리 실행
    @Transactional
    public boolean tryAcquireClosing(Long projectId) {
        return projectRepo.tryAcquireClosing(projectId, LocalDateTime.now()) == 1;
    }


    @Transactional
    public ProjectENT tryAcquireClosingAndGet(Long projectId) {
        int updated = projectRepo.tryAcquireClosing(projectId, LocalDateTime.now());
        if (updated == 0) {
            return null;  // 선점 실패
        }
        return projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("선점 후 조회 실패"));
    }

    //CLOSING -> SUCCESS/FAILED 확정
    @Transactional
    public boolean finalizeStatusFromClosing(Long projectId, FundingStatus status) {
        return projectRepo.finalizeFromClosing(projectId, status, LocalDateTime.now()) == 1;
    }

    // 재시도 프로젝트 찾기
    public List<ProjectENT> findStuckClosing(LocalDateTime threshold, int limit) {
        return projectRepo.findStuckClosing(threshold,  PageRequest.of(0, limit));
    }


}
