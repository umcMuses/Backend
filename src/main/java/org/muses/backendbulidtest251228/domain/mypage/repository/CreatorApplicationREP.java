package org.muses.backendbulidtest251228.domain.mypage.repository;

import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationENT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreatorApplicationREP
        extends JpaRepository<CreatorApplicationENT, Long> {

    boolean existsByMember_IdAndStatus(Long memberId, ApplicationStatus status);

    Optional<CreatorApplicationENT>
    findTopByMember_IdOrderByCreatedAtDesc(Long memberId);
}
