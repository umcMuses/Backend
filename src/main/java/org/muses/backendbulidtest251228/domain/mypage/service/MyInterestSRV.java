package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDT;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface MyInterestSRV {
    List<ProjectCardResponseDT> getLikedProjects(UserDetails userDetails, int page, int size);
}
