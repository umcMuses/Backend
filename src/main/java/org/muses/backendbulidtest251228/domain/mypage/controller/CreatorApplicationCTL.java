package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.*;
import org.muses.backendbulidtest251228.domain.mypage.service.CreatorApplicationSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Tag(name = "마이페이지 - 크리에이터 전환", description = "메이커 > 크리에이터 전환 신청")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/creators/applications")
public class CreatorApplicationCTL {

    private final CreatorApplicationSRV creatorApplicationSRV;

    @Operation(summary = "크리에이터 전환 신청", description = "로그인한 사용자가 크리에이터 전환을 신청 (사용법 schema 확인)")
    @PostMapping
    public ApiResponse<CreatorApplyResDT> apply(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreatorApplyReqDT req
    ) {
        return ApiResponse.success(
                creatorApplicationSRV.apply(userDetails, req)
        );
    }

    @Operation(summary = "내 크리에이터 전환 신청 조회", description = "로그인한 사용자의 크리에이터 전환 신청 상태를 조회")
    @GetMapping("/me")
    public ApiResponse<CreatorApplyResDT> myApplication(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(
                creatorApplicationSRV.getMyApplication(userDetails)
        );
    }

    // 서류 업로드/조회/제출

    @Operation(summary = "전환 신청 서류 업로드", description = "docType + file를 함께 전송. 크리에이터 전환 신청을 위해 증빙 서류를 업로드")
    @PostMapping(value = "/me/docs", consumes = "multipart/form-data")
    public ApiResponse<CreatorApplicationDocResDT> uploadDoc(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(
                    description = """
                업로드하는 서류 종류
                가능한 값: ID_CARD(개인/개인사업자/법인사업자-신분증 사본), BANKBOOK(개인/개인사업자/법인사업자-통장 사본), BRC(개인사업자/법인사업자-사업자등록증), COMP_REGISTRY(법인사업자-법인등기부등본), COMP_SEAL(법인사업자-법인 인감증명서)
                """,
                    required = true,
                    schema = @Schema(type = "string", example = "ID_CARD")
            )
            @RequestPart("docType") String docType,

            @Parameter(
                    description = "업로드할 파일",
                    required = true
            )
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.success(creatorApplicationSRV.uploadDoc(userDetails, docType, file));
    }

    @Operation(summary = "내 전환 신청 서류 목록 조회", description = "업로드된 서류 목록 조회")
    @GetMapping("/me/docs")
    public ApiResponse<List<CreatorApplicationDocResDT>> myDocs(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(creatorApplicationSRV.getMyDocs(userDetails));
    }

    @Operation(summary = "전환 신청 제출하기(검증)", description = "필수 서류 누락 여부 검증 후 결과 반환")
    @PostMapping("/me/submit")
    public ApiResponse<CreatorApplicationSubmitResDT> submit(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(creatorApplicationSRV.submit(userDetails));
    }
}
