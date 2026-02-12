package org.muses.backendbulidtest251228.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.alarm.service.AlarmSRV;
import org.muses.backendbulidtest251228.domain.storage.dto.AttachmentResponseDT;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.domain.storage.service.AttachmentSRV;
import org.muses.backendbulidtest251228.domain.project.dto.request.FundingRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.request.InfoRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.request.OutlineRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectCreateRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectSearchRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.request.RewardsRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.request.StoryRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectDetailResponseDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectLikeResponseDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectListResponseDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.RewardResponseDT;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectContentENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectLikeENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectManagerENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectTagENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.mapper.ProjectMapper;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectContentRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectLikeRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectManagerRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectTagRepo;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.global.security.PrincipalDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectSRVI implements ProjectSRV {

    private static final Logger log = LoggerFactory.getLogger(ProjectSRVI.class);

    private final ProjectRepo projectRepo;
    private final ProjectTagRepo projectTagRepo;
    private final ProjectContentRepo projectContentRepo;
    private final ProjectManagerRepo projectManagerRepo;
    private final RewardRepo rewardRepo;
    private final ProjectLikeRepo projectLikeRepo;
    private final MemberRepo memberRepo;
    private final ProjectMapper projectMapper;

    private final AlarmSRV alarmSRV;
    private final AttachmentSRV attachmentSRV;

    // ==================== 프로젝트 생성 ====================
    @Override
    @Transactional
    public Long createProject(ProjectCreateRequestDT request) {
        // JWT에서 현재 로그인한 유저 ID 조회 (필수)
        Long memberId = resolveCurrentMemberId();
        Member member = findMemberById(memberId);

        ProjectENT project = ProjectENT.builder()
                .member(member)
                .title(request.getTitle() != null ? request.getTitle() : "새 프로젝트")
                .status("DRAFT")
                .lastSavedStep(0)
                .fundingStatus(FundingStatus.PREPARING)
                .build();

        ProjectENT savedProject = projectRepo.save(project);

        // 알람 발송 (테스트 목적)
//        try {
//            alarmSRV.send(
//                    memberId,
//                    1L,
//                    Map.of("projectName", savedProject.getTitle())
//            );
//        } catch (Exception e) {
//            log.warn("[ALARM] 알람 발송 실패 - memberId: {}, error: {}", memberId, e.getMessage());
//        }

        return savedProject.getId();
    }

    // ==================== 프로젝트 상세 조회 (임시저장 데이터 통합 조회) ====================
    @Override
    public ProjectDetailResponseDT getProjectDetail(Long projectId) {
        ProjectENT project = findProjectById(projectId);

        // 태그 조회
        List<String> tags = projectTagRepo.findByProjectId(projectId)
                .stream()
                .map(ProjectTagENT::getTagName)
                .collect(Collectors.toList());

        // 리워드 조회
        List<RewardResponseDT> rewards = rewardRepo.findByProjectId(projectId)
                .stream()
                .map(RewardResponseDT::from)
                .collect(Collectors.toList());

        // 스토리 조회
        ProjectContentENT content = projectContentRepo.findById(projectId).orElse(null);

        // 진행자 정보 조회
        ProjectManagerENT manager = projectManagerRepo.findById(projectId).orElse(null);

        // 첨부파일 조회
        List<AttachmentResponseDT> attachments = attachmentSRV.getAttachments("PROJECT", projectId)
                .stream()
                .map(AttachmentResponseDT::from)
                .collect(Collectors.toList());

        // 정산 서류 조회
        List<AttachmentResponseDT> documents = attachmentSRV.getAttachments("PROJECT_DOC", projectId)
                .stream()
                .map(AttachmentResponseDT::from)
                .collect(Collectors.toList());

        // 메이커 서류 조회
        List<AttachmentResponseDT> makerDocuments = attachmentSRV.getAttachments("MAKER_DOC", projectId)
                .stream()
                .map(AttachmentResponseDT::from)
                .collect(Collectors.toList());

        return ProjectDetailResponseDT.builder()
                .projectId(project.getId())
                .status(project.getStatus())
                .lastSavedStep(project.getLastSavedStep())
                // 1단계: 개요
                .title(project.getTitle())
                .description(project.getDescription())
                .thumbnailUrl(project.getThumbnailUrl())
                .tags(tags)
                .ageLimit(project.getAgeLimit())
                .region(project.getRegion())
                // 2단계: 펀딩
                .targetAmount(project.getTargetAmount())
                .opening(project.getOpening())
                .deadline(project.getDeadline())
                .fundingStatus(project.getFundingStatus())
                // 3단계: 리워드
                .rewards(rewards)
                // 4단계: 스토리
                .storyHtml(content != null ? content.getStoryHtml() : null)
                .refundPolicy(content != null ? content.getRefundPolicy() : null)
                .attachments(attachments)
                // 5단계: 정보
                .creatorName(project.getMember().getName())
                .creatorNickName(project.getMember().getNickName())
                .hostProfileImg(manager != null ? manager.getHostProfileImg() : null)
                .hostPhone(manager != null ? manager.getHostPhone() : null)
                .hostBio(manager != null ? manager.getHostBio() : null)
                .managerName(manager != null ? manager.getManagerName() : null)
                .managerPhone(manager != null ? manager.getManagerPhone() : null)
                .documents(documents)
                .makerDocuments(makerDocuments)
                // 통계
                .achieveRate(project.getAchieveRate())
                .supporterCount(project.getSupporterCount())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    // ==================== 프로젝트 목록 조회 (카드용) ====================
    @Override
    public List<ProjectCardResponseDT> getProjectList() {
        // DRAFT, PENDING 제외 (승인된 프로젝트만 노출)
        List<ProjectENT> projects = projectRepo.findByStatusNotIn(List.of("DRAFT", "PENDING"));

        return projects.stream()
                .map(this::toCardDTO)
                .collect(Collectors.toList());
    }

    private ProjectCardResponseDT toCardDTO(ProjectENT project) {
        LocalDateTime now = LocalDateTime.now();
        Long dDay = null;
        Boolean isScheduled = false;

        if (project.getDeadline() != null) {
            dDay = ChronoUnit.DAYS.between(now.toLocalDate(), project.getDeadline().toLocalDate());
        }

        if (project.getFundingStatus() != null) {
            isScheduled = project.getFundingStatus().name().equals("SCHEDULED");
        }

        // 첨부파일 중 첫 번째 이미지 URL 조회
        String attachmentImageUrl = attachmentSRV.getFirstImageUrl("PROJECT", project.getId());

        // 태그 조회
        List<String> tags = project.getProjectTags().stream()
                .map(ProjectTagENT::getTagName)
                .collect(Collectors.toList());

        return ProjectCardResponseDT.builder()
                .projectId(project.getId())
                .thumbnailUrl(project.getThumbnailUrl())
                .title(project.getTitle())
                .achieveRate(project.getAchieveRate())
                .deadline(project.getDeadline())
                .dDay(dDay)
                .fundingStatus(project.getFundingStatus() != null ? project.getFundingStatus().name() : null)
                .isScheduled(isScheduled)
                .opening(project.getOpening())
                .attachmentImageUrl(attachmentImageUrl)
                .region(project.getRegion() != null ? project.getRegion().name() : null)
                .tags(tags)
                .build();
    }

    // ==================== 프로젝트 목록 조회 (필터링 + 페이징) - MyBatis ====================

    @Override
    public ProjectListResponseDT searchProjects(ProjectSearchRequestDT request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        int offset = page * size;

        // 목록 조회
        List<ProjectCardResponseDT> projects = projectMapper.findProjectCards(
                request.getRegion(),
                request.getFundingStatus(),
                request.getTag(),
                request.getKeyword(),
                size,
                offset
        );

        // 각 프로젝트에 첨부 이미지 URL, 태그 추가
        List<ProjectCardResponseDT> projectsWithImages = projects.stream()
                .map(card -> {
                    String attachmentImageUrl = attachmentSRV.getFirstImageUrl("PROJECT", card.getProjectId());
                    List<String> tags = projectTagRepo.findByProjectId(card.getProjectId())
                            .stream()
                            .map(ProjectTagENT::getTagName)
                            .collect(Collectors.toList());
                    return ProjectCardResponseDT.builder()
                            .projectId(card.getProjectId())
                            .thumbnailUrl(card.getThumbnailUrl())
                            .title(card.getTitle())
                            .achieveRate(card.getAchieveRate())
                            .deadline(card.getDeadline())
                            .dDay(card.getDDay())
                            .fundingStatus(card.getFundingStatus())
                            .isScheduled(card.getIsScheduled())
                            .opening(card.getOpening())
                            .attachmentImageUrl(attachmentImageUrl)
                            .region(card.getRegion())
                            .tags(tags)
                            .build();
                })
                .collect(Collectors.toList());

        // 전체 개수 조회
        int totalCount = projectMapper.countProjectCards(
                request.getRegion(),
                request.getFundingStatus(),
                request.getTag(),
                request.getKeyword()
        );

        int totalPages = (int) Math.ceil((double) totalCount / size);
        boolean hasNext = page < totalPages - 1;

        return ProjectListResponseDT.builder()
                .projects(projectsWithImages)
                .totalCount(totalCount)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .build();
    }

    // ==================== 1단계: 개요 ====================

    @Override
    @Transactional
    public void saveOutline(Long projectId, OutlineRequestDT request) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        // 프로젝트 기본 정보 업데이트
        project.updateOutline(request);

        // 기존 태그 삭제 후 새로 저장
        projectTagRepo.deleteByProjectId(projectId);
        if (request.getTags() != null) {
            request.getTags().forEach(tagName -> {
                ProjectTagENT tag = ProjectTagENT.of(tagName);
                project.addTag(tag);
                projectTagRepo.save(tag);
            });
        }

        // 저장 단계 업데이트
        project.updateLastSavedStep(1);
    }

    // ==================== 2단계: 펀딩 ====================

    @Override
    @Transactional
    public void saveFunding(Long projectId, FundingRequestDT request) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        project.updateFunding(request);

        project.updateLastSavedStep(2);
    }

    // ==================== 3단계: 리워드 ====================

    @Override
    @Transactional
    public void saveRewards(Long projectId, RewardsRequestDT request) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        // 기존 리워드 삭제
        rewardRepo.deleteByProjectId(projectId);

        // 새 리워드 저장
        if (request.getRewards() != null) {
            request.getRewards().forEach(rewardDTO -> {
                RewardENT reward = RewardENT.of(
                        rewardDTO.getRewardName(),
                        rewardDTO.getPrice(),
                        rewardDTO.getDescription(),
                        rewardDTO.getTotalQuantity(),
                        rewardDTO.getType()
                );
                project.addReward(reward);
                rewardRepo.save(reward);
            });
        }

        project.updateLastSavedStep(3);
    }

    // ==================== 4단계: 스토리 ====================
    @Override
    @Transactional
    public void saveStory(Long projectId, StoryRequestDT request) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        // ProjectContent 조회 또는 생성
        ProjectContentENT content = projectContentRepo.findById(projectId)
                .orElseGet(() -> {
                    ProjectContentENT newContent = ProjectContentENT.of(
                            request.getStoryHtml(),
                            request.getRefundPolicy()
                    );
                    project.setProjectContent(newContent);
                    return newContent;
                });

        content.updateStoryHtml(request.getStoryHtml());
        content.updateRefundPolicy(request.getRefundPolicy());
        projectContentRepo.save(content);

        project.updateLastSavedStep(4);
    }

    @Override
    @Transactional
    public List<String> uploadImages(Long projectId, List<MultipartFile> images, List<Long> deleteIds) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        // 삭제할 첨부파일 DB에서 삭제 (실제 파일은 남겨둠)
        if (deleteIds != null && !deleteIds.isEmpty()) {
            for (Long attachmentId : deleteIds) {
                attachmentSRV.deleteFromDb(attachmentId);
            }
        }

        // 새 파일 업로드
        List<String> fileUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<AttachmentENT> attachments = attachmentSRV.uploadAll("PROJECT", projectId, images);
            fileUrls = attachments.stream()
                    .map(AttachmentENT::getFileUrl)
                    .collect(Collectors.toList());
        }
        
        return fileUrls;
    }

    // ==================== 5단계: 정보 ====================
    @Override
    @Transactional
    public void saveInfo(Long projectId, InfoRequestDT request) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        // ProjectManager 조회 또는 생성
        ProjectManagerENT manager = projectManagerRepo.findById(projectId)
                .orElseGet(() -> {
                    ProjectManagerENT newManager = ProjectManagerENT.of(
                            project.getMember().getId(),
                            request.getHostProfileImg(),
                            request.getHostPhone(),
                            request.getHostBirth(),
                            request.getHostAddress(),
                            request.getHostBio(),
                            request.getManagerName(),
                            request.getManagerPhone(),
                            request.getManagerEmail()
                    );
                    project.setProjectManager(newManager);
                    return newManager;
                });

        manager.updateHostInfo(
                request.getHostProfileImg(),
                request.getHostPhone(),
                request.getHostBirth(),
                request.getHostAddress(),
                request.getHostBio()
        );
        manager.updateManagerInfo(
                request.getManagerName(),
                request.getManagerPhone(),
                request.getManagerEmail()
        );
        projectManagerRepo.save(manager);

        project.updateLastSavedStep(5);
    }

    @Override
    @Transactional
    public List<String> uploadDocuments(Long projectId, List<MultipartFile> documents, List<Long> deleteIds) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        // 삭제할 첨부파일 DB에서 삭제 (실제 파일은 남겨둠)
        if (deleteIds != null && !deleteIds.isEmpty()) {
            for (Long attachmentId : deleteIds) {
                attachmentSRV.deleteFromDb(attachmentId);
            }
        }

        // 새 파일 업로드
        List<String> fileUrls = new ArrayList<>();
        if (documents != null && !documents.isEmpty()) {
            List<AttachmentENT> attachments = attachmentSRV.uploadAll("PROJECT_DOC", projectId, documents);
            fileUrls = attachments.stream()
                    .map(AttachmentENT::getFileUrl)
                    .collect(Collectors.toList());
        }
        
        return fileUrls;
    }

    @Override
    @Transactional
    public List<String> uploadMakerDocuments(Long projectId, List<MultipartFile> documents, List<Long> deleteIds) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        // 삭제할 첨부파일 DB에서 삭제 (실제 파일은 남겨둠)
        if (deleteIds != null && !deleteIds.isEmpty()) {
            for (Long attachmentId : deleteIds) {
                attachmentSRV.deleteFromDb(attachmentId);
            }
        }

        // 새 파일 업로드
        List<String> fileUrls = new ArrayList<>();
        if (documents != null && !documents.isEmpty()) {
            List<AttachmentENT> attachments = attachmentSRV.uploadAll("MAKER_DOC", projectId, documents);
            fileUrls = attachments.stream()
                    .map(AttachmentENT::getFileUrl)
                    .collect(Collectors.toList());
        }

        return fileUrls;
    }

    // ==================== 프로젝트 제출 ====================
    @Override
    @Transactional
    public void submitProject(Long projectId) {
        ProjectENT project = findProjectById(projectId);

        // 본인 프로젝트인지 확인
        Long currentMemberId = resolveCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 제출할 수 있습니다.");
        }

        // 유효성 검사
        validateProjectForSubmission(project);

        // 상태 변경: DRAFT → PENDING
        project.updateStatus("PENDING");
    }

    // ==================== 좋아요 ====================
    @Override
    @Transactional
    public ProjectLikeResponseDT toggleLike(Long projectId) {
        Long memberId = resolveCurrentMemberId();
        Member member = findMemberById(memberId);
        ProjectENT project = findProjectById(projectId);

        boolean liked;

        // 이미 좋아요 했으면 삭제, 아니면 추가
        if (projectLikeRepo.existsByMemberIdAndProjectId(memberId, projectId)) {
            projectLikeRepo.deleteByMemberIdAndProjectId(memberId, projectId);
            liked = false;
        } else {
            ProjectLikeENT like = ProjectLikeENT.of(member, project);
            projectLikeRepo.save(like);
            liked = true;
        }

        long likeCount = projectLikeRepo.countByProjectId(projectId);

        return ProjectLikeResponseDT.builder()
                .projectId(projectId)
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }

    @Override
    public ProjectLikeResponseDT getLikeStatus(Long projectId) {
        // 프로젝트 존재 확인
        findProjectById(projectId);

        Long memberId = resolveCurrentMemberIdOrNull();
        boolean liked = false;

        // 로그인한 경우에만 좋아요 여부 확인
        if (memberId != null) {
            liked = projectLikeRepo.existsByMemberIdAndProjectId(memberId, projectId);
        }

        long likeCount = projectLikeRepo.countByProjectId(projectId);

        return ProjectLikeResponseDT.builder()
                .projectId(projectId)
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }

    // ==================== Private Methods ====================
    private ProjectENT findProjectById(Long projectId) {
        return projectRepo.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다. id=" + projectId));
    }

    private Member findMemberById(Long memberId) {
        return memberRepo.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다. id=" + memberId));
    }

    /**
     * 현재 로그인한 유저 ID 조회 (필수 - 없으면 예외)
     * JWT 토큰에서 인증된 PrincipalDetails를 통해 memberId 추출
     */
    private Long resolveCurrentMemberId() {
        Long memberId = resolveCurrentMemberIdOrNull();
        if (memberId == null) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED, "로그인이 필요합니다.");
        }
        return memberId;
    }

    private void validateProjectForSubmission(ProjectENT project) {
        if (project.getLastSavedStep() < 5) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "모든 단계를 완료해야 제출할 수 있습니다. 현재 단계: " + project.getLastSavedStep());
        }
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "프로젝트 제목이 필요합니다.");
        }
        if (project.getTargetAmount() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "목표 금액이 필요합니다.");
        }
        if (project.getDeadline() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "펀딩 마감일이 필요합니다.");
        }
    }

    // ==================== JWT 기반 유저 식별 ====================

    /**
     * 현재 로그인한 유저 ID 조회 (선택 - 없으면 null)
     * JWT 토큰에서 인증된 PrincipalDetails를 통해 memberId 추출
     */
    private Long resolveCurrentMemberIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof PrincipalDetails principalDetails) {
            return principalDetails.getMemberId();
        }

        return null;
    }
}
