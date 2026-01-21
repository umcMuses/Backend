package org.muses.backendbulidtest251228.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.dto.request.FundingRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.InfoRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.OutlineRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectCreateRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectSearchRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.RewardsRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.StoryRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectDetailResponseDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectListResponseDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.RewardResponseDTO;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectContentENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectManagerENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectTagENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.mapper.ProjectMapper;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectContentRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectManagerRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectTagRepo;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectSRVI implements ProjectSRV {

    private final ProjectRepo projectRepo;
    private final ProjectTagRepo projectTagRepo;
    private final ProjectContentRepo projectContentRepo;
    private final ProjectManagerRepo projectManagerRepo;
    private final RewardRepo rewardRepo;
    private final ProjectMapper projectMapper;

    // ==================== 프로젝트 생성 ====================
    @Override
    @Transactional
    public Long createProject(ProjectCreateRequestDTO request) {
        ProjectENT project = ProjectENT.builder()
                .userId(request.getUserId())
                .title(request.getTitle() != null ? request.getTitle() : "새 프로젝트")
                .status("DRAFT")
                .lastSavedStep(0)
                .fundingStatus(FundingStatus.PREPARING)
                .build();

        return projectRepo.save(project).getId();
    }

    // ==================== 프로젝트 상세 조회 (임시저장 데이터 통합 조회) ====================
    // TODO: 컨버터 사용 여부 확인
    @Override
    public ProjectDetailResponseDTO getProjectDetail(Long projectId) {
        ProjectENT project = findProjectById(projectId);

        // 태그 조회
        List<String> tags = projectTagRepo.findByProjectId(projectId)
                .stream()
                .map(ProjectTagENT::getTagName)
                .collect(Collectors.toList());

        // 리워드 조회
        List<RewardResponseDTO> rewards = rewardRepo.findByProjectId(projectId)
                .stream()
                .map(RewardResponseDTO::from)
                .collect(Collectors.toList());

        // 스토리 조회
        ProjectContentENT content = projectContentRepo.findById(projectId).orElse(null);

        // 진행자 정보 조회
        ProjectManagerENT manager = projectManagerRepo.findById(projectId).orElse(null);

        return ProjectDetailResponseDTO.builder()
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
                .fundingType(project.getFundingType())
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
    public List<ProjectCardResponseDTO> getProjectList() {
        // DRAFT, PENDING 제외 (승인된 프로젝트만 노출)
        List<ProjectENT> projects = projectRepo.findByStatusNotIn(List.of("DRAFT", "PENDING"));

        return projects.stream()
                .map(this::toCardDTO)
                .collect(Collectors.toList());
    }

    // TODO: 컨버터 사용 여부 확인
    private ProjectCardResponseDTO toCardDTO(ProjectENT project) {
        LocalDateTime now = LocalDateTime.now();
        Long dDay = null;
        Boolean isScheduled = false;

        if (project.getDeadline() != null) {
            dDay = ChronoUnit.DAYS.between(now.toLocalDate(), project.getDeadline().toLocalDate());
        }

        if (project.getFundingStatus() != null) {
            isScheduled = project.getFundingStatus().name().equals("SCHEDULED");
        }

        return ProjectCardResponseDTO.builder()
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
    public ProjectListResponseDTO searchProjects(ProjectSearchRequestDTO request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        int offset = page * size;

        // 목록 조회
        List<ProjectCardResponseDTO> projects = projectMapper.findProjectCards(
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

        return ProjectListResponseDTO.builder()
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
    public void saveOutline(Long projectId, OutlineRequestDTO request) {
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
    public void saveFunding(Long projectId, FundingRequestDTO request) {
        ProjectENT project = findProjectById(projectId);

        project.updateFunding(request);

        project.updateLastSavedStep(2);
    }

    // ==================== 3단계: 리워드 ====================

    @Override
    @Transactional
    public void saveRewards(Long projectId, RewardsRequestDTO request) {
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
    public void saveStory(Long projectId, StoryRequestDTO request) {
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
    public void saveInfo(Long projectId, InfoRequestDTO request) {
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

    // ==================== Private Methods ====================
    private ProjectENT findProjectById(Long projectId) {
        return projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. id=" + projectId));
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
}
