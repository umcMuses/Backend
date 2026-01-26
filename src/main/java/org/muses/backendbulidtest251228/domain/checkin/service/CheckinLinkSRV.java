package org.muses.backendbulidtest251228.domain.checkin.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.checkin.generator.TokenGenerator;
import org.muses.backendbulidtest251228.domain.checkin.entity.ProjectCheckinLinkENT;
import org.muses.backendbulidtest251228.domain.checkin.repository.ProjectCheckinLinkRepo;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckinLinkSRV {

    private final ProjectCheckinLinkRepo linkRepo;
    private final ProjectRepo projectRepo;
    private final TokenGenerator tokenGenerator;

    //프로젝트에 대한 체크인 전용 링크를 생성하거나 이미 있으면 그대로 반환한다
    @Transactional
    public String createOrGetLink(Long projectId, String baseUrl) {

        ProjectENT project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트"));

        // 토큰으로 링크 생성
        return linkRepo.findByProject_Id(projectId)
                .map(link -> buildUrl(baseUrl, link.getToken()))
                .orElseGet(() -> {
                    ProjectCheckinLinkENT link = ProjectCheckinLinkENT.builder()
                            .project(project)
                            .token(tokenGenerator.generate())
                            .build();
                    linkRepo.save(link);
                    return buildUrl(baseUrl, link.getToken());
                });
    }

    // baseUrl과 token을 조합해 체크인 URL을 만든다
    private String buildUrl(String baseUrl, String token) {
        return baseUrl + "/checkin/" + token;
    }
}
