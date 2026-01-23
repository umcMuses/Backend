package org.muses.backendbulidtest251228.domain.project.repository;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectContentENT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectContentRepo extends JpaRepository<ProjectContentENT, Long> {
}
