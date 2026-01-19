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
