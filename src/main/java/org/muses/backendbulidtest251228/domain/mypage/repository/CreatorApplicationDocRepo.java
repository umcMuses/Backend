package org.muses.backendbulidtest251228.domain.mypage.repository;

import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationDocENT;
import org.muses.backendbulidtest251228.domain.mypage.enums.DocType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CreatorApplicationDocRepo extends JpaRepository<CreatorApplicationDocENT, Long> {
    List<CreatorApplicationDocENT> findAllByApplication_AppId(Long appId);
    Optional<CreatorApplicationDocENT> findByApplication_AppIdAndDocType(Long appId, DocType docType);
    long countByApplication_AppId(Long appId);

    // 특정 신청의 서류 이미지 조회
    @Query("SELECT d FROM CreatorApplicationDocENT d " +
        "JOIN FETCH d.attachment " +
        "WHERE d.application.appId = :appId " +
        "ORDER BY d.createdAt ASC")
    List<CreatorApplicationDocENT> findAllByAppIdWithAttachment(@Param("appId") Long appId);
}
