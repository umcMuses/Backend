package org.muses.backendbulidtest251228.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
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
import org.muses.backendbulidtest251228.domain.project.service.ProjectSRV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Project", description = "프로젝트 개설 API")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectCTL {

    private static final Logger log = LoggerFactory.getLogger(ProjectCTL.class);
    private final ProjectSRV projectSRV;

    // ==================== 프로젝트 초기 생성 ====================

    @Operation(
            summary = "프로젝트 초기 생성 (최초 진입)",
            description = """
                    빈 프로젝트를 생성하고 projectId를 발급합니다. (JWT 인증 필수)
                    
                    **응답 필드:**
                    - `projectId`: 생성된 프로젝트 고유 ID
                    - `status`: 프로젝트 상태 (DRAFT)
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PostMapping("/draft")
    public ApiResponse<Map<String, Object>> createDraft(
            @RequestBody ProjectCreateRequestDT request
    ) {
        Long projectId = projectSRV.createProject(request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "status", "DRAFT",
                "message", "프로젝트 생성 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 프로젝트 상세 조회 (임시저장 데이터 통합 조회) ====================

    @Operation(
            summary = "프로젝트 상세 조회",
            description = """
                    프로젝트의 전체 정보를 조회합니다. (임시저장 데이터 포함)
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 고유 ID
                    - `status`: 프로젝트 심사/진행 상태 (DRAFT, PENDING, APPROVED, REJECTED, FUNDING, SUCCESS, FAIL, CANCELLED)
                    - `lastSavedStep`: 현재까지 저장 완료된 단계 (1~5)
                    - **[1단계 개요]** `title`, `description`, `thumbnailUrl`, `tags`, `ageLimit`, `region`
                    - **[2단계 펀딩]** `targetAmount`, `opening`, `deadline`, `fundingType`, `fundingStatus`
                    - **[3단계 리워드]** `rewards`: 리워드 목록 (rewardId, rewardName, price, description, totalQuantity, soldQuantity, remainingQuantity, type)
                    - **[4단계 스토리]** `storyHtml`, `refundPolicy`
                    - **[5단계 정보]** `hostProfileImg`, `hostPhone`, `hostBio`
                    - **[통계]** `achieveRate`, `supporterCount`, `createdAt`, `updatedAt`
                    """
    )
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectDetailResponseDT> getProjectDetail(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId
    ) {
        ProjectDetailResponseDT response = projectSRV.getProjectDetail(projectId);
        return ApiResponse.success(response);
    }

    // ==================== 프로젝트 목록 조회 (카드용) ====================

    @Operation(
            summary = "프로젝트 목록 조회",
            description = """
                    공개된 프로젝트 카드 목록을 조회합니다. (DRAFT, PENDING 제외)
                    
                    **응답 필드 (각 프로젝트 카드):**
                    - `projectId`: 프로젝트 고유 ID
                    - `thumbnailUrl`: 대표 이미지 URL
                    - `title`: 프로젝트 제목
                    - `achieveRate`: 목표 금액 대비 달성률 (%)
                    - `deadline`: 펀딩 마감 일시
                    - `dDay`: 펀딩 마감까지 남은 일수 (음수면 마감됨)
                    - `fundingStatus`: 펀딩 진행 상태 (PREPARING, SCHEDULED, FUNDING, SUCCESS, FAIL, CANCELLED)
                    - `isScheduled`: 오픈 예정 프로젝트 여부
                    - `opening`: 펀딩 시작(오픈) 일시
                    """
    )
    @GetMapping
    public ApiResponse<List<ProjectCardResponseDT>> getProjectList() {
        List<ProjectCardResponseDT> response = projectSRV.getProjectList();
        return ApiResponse.success(response);
    }

    // ==================== 프로젝트 검색 (필터링 + 페이징) ====================

    @Operation(
            summary = "프로젝트 검색",
            description = """
                    필터링 + 페이징을 지원하는 프로젝트 검색입니다.
                    
                    **응답 필드:**
                    - `projects`: 프로젝트 카드 목록 (현재 페이지에 해당하는 프로젝트들)
                    - `totalCount`: 검색 조건에 맞는 전체 프로젝트 개수
                    - `page`: 현재 페이지 번호 (0부터 시작)
                    - `size`: 한 페이지당 프로젝트 개수
                    - `totalPages`: 전체 페이지 수
                    - `hasNext`: 다음 페이지 존재 여부
                    
                    **각 프로젝트 카드 필드:**
                    - `projectId`, `thumbnailUrl`, `title`, `achieveRate`, `deadline`, `dDay`, `fundingStatus`, `isScheduled`, `opening`
                    """
    )
    @GetMapping("/search")
    public ApiResponse<ProjectListResponseDT> searchProjects(
            @Parameter(description = "지역 필터 (SEOUL, GYEONGGI, BUSAN, DAEGU, INCHEON, GWANGJU, DAEJEON, ULSAN, SEJONG, GANGWON, CHUNGBUK, CHUNGNAM, JEONBUK, JEONNAM, GYEONGBUK, GYEONGNAM, JEJU)")
            @RequestParam(required = false) String region,

            @Parameter(description = "펀딩 상태 필터 (PREPARING: 준비중, SCHEDULED: 오픈 예정, FUNDING: 펀딩중, SUCCESS: 성공, FAIL: 실패, CANCELLED: 취소)")
            @RequestParam(required = false) String fundingStatus,

            @Parameter(description = "태그 검색 - 부분 일치 (예: '음악' 입력 시 '음악', '음악회' 등 매칭)")
            @RequestParam(required = false) String tag,

            @Parameter(description = "키워드 검색 - 제목과 설명에서 부분 일치 검색")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "페이지 크기 (기본값: 10)")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        ProjectSearchRequestDT request = new ProjectSearchRequestDT(region, fundingStatus, tag, keyword, page, size);
        ProjectListResponseDT response = projectSRV.searchProjects(request);
        return ApiResponse.success(response);
    }

    // ==================== 1단계: 개요 ====================

    @Operation(
            summary = "1단계: 개요 저장",
            description = """
                    프로젝트 개요 정보를 저장합니다. (제목, 태그, 대표이미지 등)
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content)
    })
    @PutMapping("/{projectId}/outline")
    public ApiResponse<Map<String, Object>> saveOutline(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody OutlineRequestDT request
    ) {
        projectSRV.saveOutline(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "개요 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 2단계: 펀딩 ====================

    @Operation(
            summary = "2단계: 펀딩 저장",
            description = """
                    펀딩 설정을 저장합니다. (목표금액, 기간, 펀딩방식)
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PutMapping("/{projectId}/funding")
    public ApiResponse<Map<String, Object>> saveFunding(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody FundingRequestDT request
    ) {
        projectSRV.saveFunding(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "펀딩 설정 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 3단계: 리워드 ====================

    @Operation(
            summary = "3단계: 리워드 저장",
            description = """
                    리워드 목록을 저장합니다. (기존 리워드 전체 삭제 후 새로 저장)
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PutMapping("/{projectId}/rewards")
    public ApiResponse<Map<String, Object>> saveRewards(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody RewardsRequestDT request
    ) {
        projectSRV.saveRewards(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "리워드 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 4단계: 스토리 ====================

    @Operation(
            summary = "4단계: 스토리 본문 저장",
            description = """
                    프로젝트 스토리 본문과 환불 정책을 저장합니다.
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PutMapping("/{projectId}/story")
    public ApiResponse<Map<String, Object>> saveStory(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody StoryRequestDT request
    ) {
        projectSRV.saveStory(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "스토리 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @Operation(
            summary = "4단계: 파일 업로드 (다중)",
            description = """
                    스토리 본문용 파일을 업로드합니다. (이미지, 문서 등 여러 파일 가능)
                    삭제할 첨부파일 ID 목록도 함께 전달할 수 있습니다.
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `fileUrls`: 업로드된 파일 URL 목록
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PostMapping(value = "/{projectId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> uploadImages(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @Parameter(description = "업로드할 파일 목록")
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "삭제할 첨부파일 ID 목록")
            @RequestParam(value = "deleteIds", required = false) List<Long> deleteIds
    ) {
        List<String> fileUrls = projectSRV.uploadImages(projectId, files, deleteIds);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "fileUrls", fileUrls,
                "message", "파일 처리 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 5단계: 정보 ====================

    @Operation(
            summary = "5단계: 정보 저장",
            description = """
                    프로젝트 진행자 및 담당자 정보를 저장합니다.
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PutMapping("/{projectId}/info")
    public ApiResponse<Map<String, Object>> saveInfo(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody InfoRequestDT request
    ) {
        projectSRV.saveInfo(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "정보 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @Operation(
            summary = "5단계: 정산 서류 업로드 (다중)",
            description = """
                    정산용 서류를 업로드합니다. (신분증, 통장 사본 등 여러 파일 가능)
                    삭제할 첨부파일 ID 목록도 함께 전달할 수 있습니다.
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `documentUrls`: 업로드된 서류 URL 목록
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PostMapping(value = "/{projectId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> uploadDocuments(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @Parameter(description = "업로드할 파일 목록")
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "삭제할 첨부파일 ID 목록")
            @RequestParam(value = "deleteIds", required = false) List<Long> deleteIds
    ) {
        List<String> documentUrls = projectSRV.uploadDocuments(projectId, files, deleteIds);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "documentUrls", documentUrls,
                "message", "정산 서류 처리 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 프로젝트 제출 ====================

    @Operation(
            summary = "프로젝트 제출하기",
            description = """
                    프로젝트를 심사 요청합니다. (모든 단계 완료 필수)
                    
                    **유효성 검사:**
                    - 5단계까지 모두 저장 완료되어야 함
                    - 제목, 목표금액, 마감일 필수
                    
                    **상태 변경:** DRAFT → PENDING
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `status`: 변경된 상태 (PENDING)
                    - `message`: 처리 결과 메시지
                    - `timestamp`: 처리 시각
                    """
    )
    @PostMapping("/{projectId}/submit")
    public ApiResponse<Map<String, Object>> submitProject(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId
    ) {
        projectSRV.submitProject(projectId);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "status", "PENDING",
                "message", "프로젝트가 심사 요청되었습니다",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 좋아요 ====================

    @Operation(
            summary = "좋아요 토글",
            description = """
                    프로젝트에 좋아요를 추가하거나 취소합니다. (JWT 인증 필수)
                    
                    - 좋아요가 없으면 → 추가 (liked: true)
                    - 좋아요가 있으면 → 취소 (liked: false)
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `liked`: 현재 좋아요 상태 (true: 좋아요 누름, false: 좋아요 취소됨)
                    - `likeCount`: 해당 프로젝트의 총 좋아요 수
                    """
    )
    @PostMapping("/{projectId}/like")
    public ApiResponse<ProjectLikeResponseDT> toggleLike(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId
    ) {
        ProjectLikeResponseDT response = projectSRV.toggleLike(projectId);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "좋아요 상태 조회",
            description = """
                    프로젝트의 좋아요 상태를 조회합니다.
                    
                    - 비로그인 유저도 조회 가능 (liked는 항상 false)
                    - 로그인 유저는 본인의 좋아요 여부 확인 가능
                    
                    **응답 필드:**
                    - `projectId`: 프로젝트 ID
                    - `liked`: 현재 로그인 유저의 좋아요 여부 (비로그인 시 항상 false)
                    - `likeCount`: 해당 프로젝트의 총 좋아요 수
                    """
    )
    @GetMapping("/{projectId}/like")
    public ApiResponse<ProjectLikeResponseDT> getLikeStatus(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId
    ) {
        ProjectLikeResponseDT response = projectSRV.getLikeStatus(projectId);
        return ApiResponse.success(response);
    }
}
