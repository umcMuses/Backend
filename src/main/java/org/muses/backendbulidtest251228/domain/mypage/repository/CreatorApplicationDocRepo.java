package org.muses.backendbulidtest251228.domain.mypage.repository;

import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationDocENT;
import org.muses.backendbulidtest251228.domain.mypage.enums.DocType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreatorApplicationDocRepo extends JpaRepository<CreatorApplicationDocENT, Long> {
    List<CreatorApplicationDocENT> findAllByApplication_AppId(Long appId);
    Optional<CreatorApplicationDocENT> findByApplication_AppIdAndDocType(Long appId, DocType docType);
    long countByApplication_AppId(Long appId);
}
