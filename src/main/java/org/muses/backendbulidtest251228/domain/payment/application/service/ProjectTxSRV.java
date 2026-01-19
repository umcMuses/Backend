package org.muses.backendbulidtest251228.domain.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.temp.FundingStatus;
import org.muses.backendbulidtest251228.domain.temp.Project;
import org.muses.backendbulidtest251228.domain.temp.ProjectREP;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTxSRV {

    private final ProjectREP projectREP;

    //선점 쿼리 실행
    @Transactional
    public boolean tryAcquireClosing(Long projectId) {
        return projectREP.tryAcquireClosing(projectId, LocalDateTime.now()) == 1;
    }


    @Transactional
    public Project tryAcquireClosingAndGet(Long projectId) {
        int updated = projectREP.tryAcquireClosing(projectId, LocalDateTime.now());
        if (updated == 0) {
            return null;  // 선점 실패
        }
        return projectREP.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("선점 후 조회 실패"));
    }

    //CLOSING -> SUCCESS/FAILED 확정
    @Transactional
    public boolean finalizeStatusFromClosing(Long projectId, FundingStatus status) {
        return projectREP.finalizeFromClosing(projectId, status, LocalDateTime.now()) == 1;
    }

    // 재시도 프로젝트 찾기
    public List<Project> findStuckClosing(LocalDateTime threshold, int limit) {
        return projectREP.findStuckClosing(threshold,  PageRequest.of(0, limit));
    }


}
