package org.muses.backendbulidtest251228.domain.storage.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.domain.storage.repository.AttachmentRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 첨부파일 관리 서비스 (로컬 저장 - 개발용)
 * 운영 환경에서는 S3AttachmentSRVI 구현체 사용
 */
@Slf4j
@Service
@Profile("local")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentSRVI implements AttachmentSRV {

    private final AttachmentRepo attachmentRepo;

    @Value("${storage.local.path:./uploads}")
    private String uploadPath;

    @Value("${server.port:8080}")
    private String serverPort;

    private String baseUrl;

    @PostConstruct
    public void init() {
        // baseUrl 동적 생성
        this.baseUrl = "http://localhost:" + serverPort + "/files";

        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("[Attachment] 업로드 디렉토리 생성: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("[Attachment] 업로드 디렉토리 생성 실패", e);
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
        }
    }

    // ==================== 파일 업로드 ====================

    @Override
    @Transactional
    public AttachmentENT upload(String targetType, Long targetId, MultipartFile file) {
        // 파일 저장
        String directory = targetType.toLowerCase() + "/" + targetId;
        String fileUrl = saveFile(file, directory);

        // 원본 파일명, 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        // DB 저장
        AttachmentENT attachment = AttachmentENT.of(
                targetType,
                targetId,
                fileUrl,
                originalFilename,
                extension
        );

        return attachmentRepo.save(attachment);
    }

    @Override
    @Transactional
    public List<AttachmentENT> uploadAll(String targetType, Long targetId, List<MultipartFile> files) {
        List<AttachmentENT> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                attachments.add(upload(targetType, targetId, file));
            }
        }

        return attachments;
    }

    // ==================== 파일 조회 ====================

    @Override
    public List<AttachmentENT> getAttachments(String targetType, Long targetId) {
        return attachmentRepo.findByTargetTypeAndTargetId(targetType, targetId);
    }

    @Override
    public String getFirstImageUrl(String targetType, Long targetId) {
        List<String> imageExtensions = List.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");

        return attachmentRepo
                .findFirstByTargetTypeAndTargetIdAndExtensionInOrderByCreatedAtAsc(targetType, targetId, imageExtensions)
                .map(AttachmentENT::getFileUrl)
                .orElse(null);
    }

    // ==================== 파일 삭제 ====================

    @Override
    @Transactional
    public void delete(Long attachmentId) {
        AttachmentENT attachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "첨부파일을 찾을 수 없습니다. id=" + attachmentId));

        // 실제 파일 삭제
        deleteFile(attachment.getFileUrl());

        // DB 삭제
        attachmentRepo.delete(attachment);
    }

    @Override
    @Transactional
    public void deleteAll(String targetType, Long targetId) {
        List<AttachmentENT> attachments = attachmentRepo.findByTargetTypeAndTargetId(targetType, targetId);

        // 실제 파일들 삭제
        for (AttachmentENT attachment : attachments) {
            deleteFile(attachment.getFileUrl());
        }

        // DB 삭제
        attachmentRepo.deleteByTargetTypeAndTargetId(targetType, targetId);
    }

    @Override
    @Transactional
    public void deleteFromDb(Long attachmentId) {
        // DB에서만 삭제 (실제 파일은 유지)
        attachmentRepo.deleteById(attachmentId);
        log.info("[Attachment] DB에서 첨부파일 삭제: id={}", attachmentId);
    }

    // ==================== Private: 파일 저장/삭제 로직 ====================

    private String saveFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "파일이 비어있습니다.");
        }

        try {
            // 디렉토리 생성
            Path dirPath = Paths.get(uploadPath, directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 파일명 생성 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = getExtensionWithDot(originalFilename);
            String savedFilename = UUID.randomUUID().toString() + extension;

            // 파일 저장
            Path filePath = dirPath.resolve(savedFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("[Attachment] 파일 업로드 완료: {}", filePath.toAbsolutePath());

            // URL 반환
            return baseUrl + "/" + directory + "/" + savedFilename;

        } catch (IOException e) {
            log.error("[Attachment] 파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = Paths.get(uploadPath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("[Attachment] 파일 삭제 완료: {}", filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("[Attachment] 파일 삭제 실패: {}", fileUrl, e);
        }
    }

    // ==================== Private: 유틸 ====================

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String getExtensionWithDot(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}
