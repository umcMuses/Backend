package org.muses.backendbulidtest251228.domain.storage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.domain.storage.repository.AttachmentRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.global.config.AwsProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * 첨부파일 관리 서비스 (S3 저장 - 운영용)
 */
@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3AttachmentSRVI implements AttachmentSRV{

	private final AttachmentRepo attachmentRepo;
	private final S3Client s3Client;
	private final AwsProperties awsProperties;

	// ==================== 파일 업로드(S3) ====================

	@Override
	@Transactional
	public AttachmentENT upload(String targetType, Long targetId, MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "파일이 비어있습니다.");
		}
		String originalFilename = file.getOriginalFilename();
		String extension = getExtension(originalFilename);

		// S3 키 생성: "targetType/targetId/UUID.extension"
		String s3Key = buildS3Key(targetType, targetId, extension);
		String fileUrl = null;

		try {
			// S3 업로드
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(awsProperties.getS3().getBucket())
				.key(s3Key)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putObjectRequest,
				RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			// S3 URL 생성 - "https://{bucket-name}.s3.{region}.amazonaws.com/{s3Key}"
			fileUrl = buildS3Url(s3Key);
			log.info("[S3] 파일 업로드 완료: {}", fileUrl);

			// DB 저장
			AttachmentENT attachment = AttachmentENT.of(
				targetType,
				targetId,
				fileUrl,
				originalFilename,
				extension
			);

			try {
				return attachmentRepo.save(attachment);
			} catch (RuntimeException e) {
				log.error("[S3] DB 저장 실패, S3 파일 삭제 시도: {}", fileUrl);
				deleteFileS3(fileUrl);
				throw new BusinessException(ErrorCode.SERVER_ERROR, "첨부파일 저장 실패");
			}

		} catch (IOException e) {
			log.error("[S3] 파일 읽기 실패: {}", originalFilename, e);
			throw new BusinessException(ErrorCode.SERVER_ERROR, "파일 처리 실패");
		} catch (SdkException e) {
			log.error("[S3] S3 업로드 실패: {}", originalFilename, e);
			throw new BusinessException(ErrorCode.SERVER_ERROR, "파일 업로드 실패");
		}
	}

	@Override
	@Transactional
	public List<AttachmentENT> uploadAll(String targetType, Long targetId, List<MultipartFile> files) {
		List<AttachmentENT> attachments = new ArrayList<>();
		List<String> uploadedUrls = new ArrayList<>();

		try {
			for (MultipartFile file : files) {
				if (file != null && !file.isEmpty()) {
					AttachmentENT attachment = upload(targetType, targetId, file);
					attachments.add(attachment);
					uploadedUrls.add(attachment.getFileUrl());
				}
			}
			return attachments;
		} catch (Exception e) {
			log.error("[S3] 다중 업로드 실패, 업로드된 {} 개 파일 삭제", uploadedUrls.size());
			uploadedUrls.forEach(this::deleteFileS3);
			throw e;
		}
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

	// ==================== 파일 삭제(S3) ====================

	@Override
	@Transactional
	public void delete(Long attachmentId) {
		AttachmentENT attachment = attachmentRepo.findById(attachmentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,
				"첨부파일을 찾을 수 없습니다. id=" + attachmentId));

		// S3에서 파일 삭제
		deleteFileS3(attachment.getFileUrl());
		// DB 삭제
		attachmentRepo.delete(attachment);
	}

	@Override
	@Transactional
	public void deleteAll(String targetType, Long targetId) {
		List<AttachmentENT> attachments = attachmentRepo.findByTargetTypeAndTargetId(targetType, targetId);

		// 실제 파일들 삭제
		for (AttachmentENT attachment : attachments) {
			deleteFileS3(attachment.getFileUrl());
		}

		// DB 삭제
		attachmentRepo.deleteByTargetTypeAndTargetId(targetType, targetId);
	}

	@Override
	@Transactional
	public void deleteFromDb(Long attachmentId) {
		// DB에서만 삭제 (실제 파일은 유지)
		attachmentRepo.deleteById(attachmentId);
		log.info("[S3] DB에서 첨부파일 삭제: id={}", attachmentId);
	}

	// ==================== Private ====================
	private void deleteFileS3(String fileUrl) {
		if (fileUrl == null || fileUrl.isBlank()) {
			return;
		}
		try {
			String s3Key = extractS3Key(fileUrl);

			DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
				.bucket(awsProperties.getS3().getBucket())
				.key(s3Key)
				.build();

			s3Client.deleteObject(deleteRequest);
			log.info("[S3] 파일 삭제 완료: {}", s3Key);

		} catch (Exception e) {
			log.error("[S3] 파일 삭제 실패: {}", fileUrl, e);
		}
	}

	private String buildS3Key(String targetType, Long targetId, String extension) {
		String uuid = UUID.randomUUID().toString();
		if (extension == null || extension.isBlank()) {
			return String.format("%s/%d/%s", targetType.toLowerCase(), targetId, uuid);
		}
		return String.format("%s/%d/%s.%s", targetType.toLowerCase(), targetId, uuid, extension);
	}

	private String buildS3Url(String s3Key) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s",
			awsProperties.getS3().getBucket(),
			awsProperties.getRegion(),
			s3Key);
	}

	private String extractS3Key(String fileUrl) {
		// https://bucket.s3.region.amazonaws.com/key
		String prefix = String.format("https://%s.s3.%s.amazonaws.com/",
			awsProperties.getS3().getBucket(), awsProperties.getRegion());
		return fileUrl.replace(prefix, "");
	}

	private String getExtension(String filename) {
		if (filename == null || filename.isBlank()) {
			return "";
		}
		int lastDotIndex = filename.lastIndexOf('.');
		int lastSeparatorIndex = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
		// 점이 없거나, 점이 경로 구분자보다 앞에 있으면 확장자 없음
		if (lastDotIndex <= 0 || lastDotIndex < lastSeparatorIndex) {
			return "";
		}
		// 점이 마지막 문자면 확장자 없음
		if (lastDotIndex == filename.length() - 1) {
			return "";
		}
		return filename.substring(lastDotIndex + 1).toLowerCase();
	}
}
