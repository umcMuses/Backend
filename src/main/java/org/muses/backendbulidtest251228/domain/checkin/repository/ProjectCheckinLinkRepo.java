package org.muses.backendbulidtest251228.domain.checkin.repository;

import org.muses.backendbulidtest251228.domain.checkin.entity.ProjectCheckinLinkENT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectCheckinLinkRepo
        extends JpaRepository<ProjectCheckinLinkENT, Long> {

    Optional<ProjectCheckinLinkENT> findByProject_Id(Long projectId);

    Optional<ProjectCheckinLinkENT> findByToken(String token);
}
