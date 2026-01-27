package org.muses.backendbulidtest251228.domain.checkin.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.checkin.generator.TokenGenerator;
import org.muses.backendbulidtest251228.domain.checkin.entity.ProjectCheckinLinkENT;
import org.muses.backendbulidtest251228.domain.checkin.repository.ProjectCheckinLinkRepo;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectCheckinIssueSRV {

    private final ProjectCheckinLinkRepo repo;
    private final TokenGenerator tokenGenerator;

    // 해당 프로젝트의 체크인 링크가 없으면 생성하고, 있으면 조회한다
    @Transactional
    public void issueIfAbsent(ProjectENT project) {
        repo.findByProject_Id(project.getId())
                .orElseGet(() -> {
                    ProjectCheckinLinkENT link = ProjectCheckinLinkENT.builder()
                            .project(project)
                            .token(tokenGenerator.generate())
                            .build();
                    return repo.save(link);
                });
    }
}

