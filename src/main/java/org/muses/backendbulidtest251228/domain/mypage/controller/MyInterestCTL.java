package org.muses.backendbulidtest251228.domain.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDT;
import org.muses.backendbulidtest251228.domain.mypage.service.MyInterestSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "마이페이지 - 관심있는 프로젝트", description = "좋아요(관심) 프로젝트 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MyInterestCTL {

    private final MyInterestSRV myInterestSRV;

    @Operation(summary = "관심 있는 프로젝트 리스트 조회", description = "내가 좋아요 누른 프로젝트 카드 리스트 조회")
    @GetMapping("/likes/projects")
    public ApiResponse<List<ProjectCardResponseDT>> getLikedProjects(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(myInterestSRV.getLikedProjects(userDetails, page, size));
    }
}
