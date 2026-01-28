package org.muses.backendbulidtest251228.domain.storage.repository;

import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepo extends JpaRepository<AttachmentENT, Long> {

    // 특정 대상의 첨부파일 목록 조회
    List<AttachmentENT> findByTargetTypeAndTargetId(String targetType, Long targetId);

    // 특정 대상의 첨부파일 삭제
    void deleteByTargetTypeAndTargetId(String targetType, Long targetId);

    // 특정 대상의 첨부파일 개수
    long countByTargetTypeAndTargetId(String targetType, Long targetId);

    // 특정 대상의 이미지 파일 중 가장 먼저 업로드된 것 1개 조회
    Optional<AttachmentENT> findFirstByTargetTypeAndTargetIdAndExtensionInOrderByCreatedAtAsc(
            String targetType, Long targetId, List<String> extensions
    );
}
