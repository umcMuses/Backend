package org.muses.backendbulidtest251228.domain.project.service;

import org.muses.backendbulidtest251228.domain.project.dto.request.*;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectSearchRequestDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectDetailResponseDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectLikeResponseDT;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectListResponseDT;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectSRV {

    // 프로젝트 생성
    Long createProject(ProjectCreateRequestDT request);

    // 프로젝트 상세 조회 (임시저장 데이터 통합 조회)
    ProjectDetailResponseDT getProjectDetail(Long projectId);

    // 프로젝트 목록 조회 (카드용)
    List<ProjectCardResponseDT> getProjectList();

    // 프로젝트 목록 조회 (필터링, 페이징)
    ProjectListResponseDT searchProjects(ProjectSearchRequestDT request);

    // 1단계: 개요
    void saveOutline(Long projectId, OutlineRequestDT request);

    // 2단계: 펀딩
    void saveFunding(Long projectId, FundingRequestDT request);

    // 3단계: 리워드
    void saveRewards(Long projectId, RewardsRequestDT request);

    // 4단계: 스토리
    void saveStory(Long projectId, StoryRequestDT request);
    String uploadImage(Long projectId, MultipartFile image);

    // 5단계: 정보
    void saveInfo(Long projectId, InfoRequestDT request);
    List<String> uploadDocuments(Long projectId, MultipartFile idCard, MultipartFile bankbook);

    // 프로젝트 제출
    void submitProject(Long projectId);

    // 좋아요 토글 (추가/취소)
    ProjectLikeResponseDT toggleLike(Long projectId);

    // 좋아요 상태 조회
    ProjectLikeResponseDT getLikeStatus(Long projectId);
}
