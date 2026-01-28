package org.muses.backendbulidtest251228.domain.landing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.landing.dto.LandingResDTO;
import org.muses.backendbulidtest251228.domain.landing.service.LandingSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "Landing", description = "랜딩 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/landing")
public class LandingCTL {

    public final LandingSRV landingSRV;

    @Operation(
            summary = "랜딩 페이지 프로젝트 목록 조회",
            description = "펀딩중(FUNDING) 상태의 프로젝트를 후원자 수 기준으로 상위 6개 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "랜딩 페이지 프로젝트 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = """
                        {
                          "success": true,
                          "data": [
                            {
                              "projectId": 1,
                              "thumbnailUrl": "https://cdn.muses.com/project/thumbnail.png",
                              "title": "인디밴드 단독 콘서트",
                              "achieveRate": 127,
                              "deadline": "2025-03-01T23:59:59",
                              "dDay": 15,
                              "fundingStatus": "FUNDING",
                              "region": "SEOUL",
                              "tags": ["콘서트", "인디", "라이브"]
                            }
                          ]
                        }
                        """
                    )
            )
    )
    @PostMapping
    public ApiResponse<List<LandingResDTO>> landing(){

        List<LandingResDTO> project = landingSRV.getProject();

        return ApiResponse.success(project);
    }
}
