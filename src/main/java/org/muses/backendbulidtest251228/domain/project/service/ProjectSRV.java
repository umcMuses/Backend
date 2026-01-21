package org.muses.backendbulidtest251228.domain.project.service;

import org.muses.backendbulidtest251228.domain.project.dto.request.*;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectSearchRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectDetailResponseDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectListResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectSRV {

    // 프로젝트 생성
    Long createProject(ProjectCreateRequestDTO request);

    // 프로젝트 상세 조회 (임시저장 데이터 통합 조회)
    ProjectDetailResponseDTO getProjectDetail(Long projectId);

    // 프로젝트 목록 조회 (카드용)
    List<ProjectCardResponseDTO> getProjectList();

    // 프로젝트 목록 조회 (필터링, 페이징)
    ProjectListResponseDTO searchProjects(ProjectSearchRequestDTO request);

    // 1단계: 개요
    void saveOutline(Long projectId, OutlineRequestDTO request);

    // 2단계: 펀딩
    void saveFunding(Long projectId, FundingRequestDTO request);

    // 3단계: 리워드
    void saveRewards(Long projectId, RewardsRequestDTO request);

    // 4단계: 스토리
    void saveStory(Long projectId, StoryRequestDTO request);
    String uploadImage(Long projectId, MultipartFile image);

    // 5단계: 정보
    void saveInfo(Long projectId, InfoRequestDTO request);
    List<String> uploadDocuments(Long projectId, MultipartFile idCard, MultipartFile bankbook);

    // 프로젝트 제출
    void submitProject(Long projectId);
}
