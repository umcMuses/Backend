package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectReqDT;
import org.springframework.security.core.userdetails.UserDetails;

public interface CreatorCenterProjectSRV {
    CreatorCenterProjectResDT.MyProjectListResponse getMyProjects(UserDetails userDetails);

    CreatorCenterProjectResDT.ProjectSettingsResponse getProjectSettings(UserDetails userDetails, Long projectId);

    CreatorCenterProjectResDT.ProjectSettingsResponse updateProjectSettings(
            UserDetails userDetails,
            Long projectId,
            CreatorCenterProjectReqDT.UpdateProjectSettingsRequest request
    );

    CreatorCenterProjectResDT.MakerListResponse getProjectMakers(UserDetails userDetails, Long projectId);
}
