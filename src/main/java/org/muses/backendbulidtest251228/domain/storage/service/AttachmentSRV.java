package org.muses.backendbulidtest251228.domain.storage.service;

import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 첨부파일 관리 서비스 인터페이스
 */
public interface AttachmentSRV {

    /**
     * 단일 파일 업로드 및 DB 저장
     */
    AttachmentENT upload(String targetType, Long targetId, MultipartFile file);

    /**
     * 다중 파일 업로드 및 DB 저장
     */
    List<AttachmentENT> uploadAll(String targetType, Long targetId, List<MultipartFile> files);

    /**
     * 특정 대상의 첨부파일 목록 조회
     */
    List<AttachmentENT> getAttachments(String targetType, Long targetId);

    /**
     * 첨부파일 삭제 (파일 + DB)
     */
    void delete(Long attachmentId);

    /**
     * 특정 대상의 모든 첨부파일 삭제
     */
    void deleteAll(String targetType, Long targetId);

    /**
     * 특정 대상의 이미지 중 가장 먼저 업로드된 것 1개 URL 조회
     * @return 이미지 URL (없으면 null)
     */
    String getFirstImageUrl(String targetType, Long targetId);

    /**
     * 첨부파일 DB에서만 삭제 (실제 파일은 유지)
     */
    void deleteFromDb(Long attachmentId);
}
