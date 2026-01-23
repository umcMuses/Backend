package org.muses.backendbulidtest251228.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.alarm.service.AlarmSRV;
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
    
    /*
     * NOTE: 알람 서비스 사용 예시 (데모용)
     * - 실제 운영 시에는 이 부분을 제거하거나 적절한 위치로 이동해야 합니다.
     * - 알람 발송 방법은 AlarmSRV 인터페이스의 주석을 참고하세요.
     */
    private final AlarmSRV alarmSRV;

    // ==================== 프로젝트 생성 ====================
    @Override
    @Transactional
    public Long createProject(ProjectCreateRequestDT request) {
        // JWT에서 현재 로그인한 유저 ID 조회 (필수)
        Long userId = resolveCurrentMemberId();

        ProjectENT project = ProjectENT.builder()
                .userId(userId)
                .title(request.getTitle() != null ? request.getTitle() : "새 프로젝트")
                .status("DRAFT")
                .lastSavedStep(0)
                .fundingStatus(FundingStatus.PREPARING)
                .build();

        ProjectENT savedProject = projectRepo.save(project);

        // 알람 발송
        try {
            alarmSRV.send(
                    userId,
                    1L,
                    Map.of("projectName", savedProject.getTitle())
            );
        } catch (Exception e) {
            log.warn("[ALARM] 알람 발송 실패 - userId: {}, error: {}", userId, e.getMessage());
        }

        return savedProject.getId();
    }

    // ==================== 프로젝트 상세 조회 (임시저장 데이터 통합 조회) ====================
    // TODO: 컨버터 사용 여부 확인
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
                // 5단계: 정보
                .hostProfileImg(manager != null ? manager.getHostProfileImg() : null)
                .hostPhone(manager != null ? manager.getHostPhone() : null)
                .hostBio(manager != null ? manager.getHostBio() : null)
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

    // TODO: 컨버터 사용 여부 확인
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
                .projects(projects)
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

        project.updateFunding(request);

        project.updateLastSavedStep(2);
    }

    // ==================== 3단계: 리워드 ====================

    @Override
    @Transactional
    public void saveRewards(Long projectId, RewardsRequestDT request) {
        ProjectENT project = findProjectById(projectId);

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
    public String uploadImage(Long projectId, MultipartFile image) {
        // TODO: 실제 이미지 업로드 로직 구현해야됨
        // 현재는 임시 URL 반환
        String imageUrl = "https://storage.example.com/projects/" + projectId + "/images/" + image.getOriginalFilename();
        return imageUrl;
    }

    // ==================== 5단계: 정보 ====================
    @Override
    @Transactional
    public void saveInfo(Long projectId, InfoRequestDT request) {
        ProjectENT project = findProjectById(projectId);

        // ProjectManager 조회 또는 생성
        ProjectManagerENT manager = projectManagerRepo.findById(projectId)
                .orElseGet(() -> {
                    ProjectManagerENT newManager = ProjectManagerENT.of(
                            project.getUserId(),
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
    public List<String> uploadDocuments(Long projectId, MultipartFile idCard, MultipartFile bankbook) {
        // TODO: 실제 파일 업로드 로직 구현해야됨. 아직 어떻게 하는지 못들음
        List<String> documentUrls = new ArrayList<>();
        documentUrls.add("https://storage.example.com/projects/" + projectId + "/docs/" + idCard.getOriginalFilename());
        documentUrls.add("https://storage.example.com/projects/" + projectId + "/docs/" + bankbook.getOriginalFilename());
        return documentUrls;
    }

    // ==================== 프로젝트 제출 ====================
    @Override
    @Transactional
    public void submitProject(Long projectId) {
        ProjectENT project = findProjectById(projectId);

        // 유효성 검사
        validateProjectForSubmission(project);

        // 상태 변경: DRAFT → PENDING
        project.updateStatus("PENDING");
        project.updateFundingStatus(FundingStatus.SCHEDULED);
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
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. id=" + projectId));
    }

    private Member findMemberById(Long memberId) {
        return memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id=" + memberId));
    }

    /**
     * 현재 로그인한 유저 ID 조회 (필수 - 없으면 예외)
     * JWT 토큰에서 인증된 PrincipalDetails를 통해 memberId 추출
     */
    private Long resolveCurrentMemberId() {
        Long memberId = resolveCurrentMemberIdOrNull();
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return memberId;
    }

    private void validateProjectForSubmission(ProjectENT project) {
        if (project.getLastSavedStep() < 5) {
            throw new IllegalStateException("모든 단계를 완료해야 제출할 수 있습니다. 현재 단계: " + project.getLastSavedStep());
        }
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            throw new IllegalStateException("프로젝트 제목이 필요합니다.");
        }
        if (project.getTargetAmount() == null) {
            throw new IllegalStateException("목표 금액이 필요합니다.");
        }
        if (project.getDeadline() == null) {
            throw new IllegalStateException("펀딩 마감일이 필요합니다.");
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
