package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectReqDT;
import org.springframework.security.core.userdetails.UserDetails;

public interface CreatorCenterProjectSRV {
    CreatorCenterProjectDT.MyProjectListResponse getMyProjects(UserDetails userDetails);

    CreatorCenterProjectDT.ProjectSettingsResponse getProjectSettings(UserDetails userDetails, Long projectId);

    CreatorCenterProjectDT.ProjectSettingsResponse updateProjectSettings(
            UserDetails userDetails,
            Long projectId,
            CreatorCenterProjectReqDT.UpdateProjectSettingsRequest request
    );

    CreatorCenterProjectDT.MakerListResponse getProjectMakers(UserDetails userDetails, Long projectId);
}
