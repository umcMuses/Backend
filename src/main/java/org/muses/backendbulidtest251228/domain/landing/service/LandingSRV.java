package org.muses.backendbulidtest251228.domain.landing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.landing.dto.LandingResDTO;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectTagENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LandingSRV {

    private final ProjectRepo projectRepo;

    public List<LandingResDTO> getProject(){
        List<ProjectENT> projects = projectRepo
                .findTop6ByFundingStatusOrderBySupporterCountDesc(FundingStatus.FUNDING);

        return projects.stream()
                .map(project -> LandingResDTO.builder()
                        .projectId(project.getId())
                        .thumbnailUrl(project.getThumbnailUrl())
                        .title(project.getTitle())
                        .achieveRate(project.getAchieveRate())
                        .deadline(project.getDeadline())
                        .dDay(
                                project.getDeadline() == null
                                        ? null
                                        : ChronoUnit.DAYS.between(LocalDateTime.now(), project.getDeadline())
                        )
                        .fundingStatus(project.getFundingStatus().name())
                        .region(project.getRegion())
                        .tags(
                                project.getProjectTags() == null
                                        ? List.of()
                                        : project.getProjectTags().stream()
                                        .map(ProjectTagENT::getTagName)
                                        .toList()
                        )
                        .build()
                )
                .toList();

    }
}
