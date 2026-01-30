package org.muses.backendbulidtest251228.domain.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyProfileResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateMyProfileReqDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateProfileImageResDT;
import org.muses.backendbulidtest251228.domain.mypage.service.MyPageSRV;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "마이페이지 - 프로필", description = "마이페이지의 프로필을 조회/수정하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MyPageCTL {

    private final MyPageSRV myPageSRV;

    @Operation(summary = "마이페이지 - 프로필 조회", description = "마이페이지의 프로필을 조회하는 API (크리에이터/메이커 모두 사용 가능")
    @GetMapping
    public MyProfileResDT getMe() {
        return myPageSRV.getMe();
    }

    @Operation(summary = "마이페이지 - 프로필 수정", description = "마이페이지의 프로필을 수정하는 API")
    @PostMapping("/profile")
    public MyProfileResDT updateProfile(@Valid @RequestBody UpdateMyProfileReqDT request) {
        return myPageSRV.updateProfile(request);
    }

    @Operation(summary = "마이페이지 - 이미지 수정", description = "마이페이지의 이미지를 수정하는 API")
    @PatchMapping(
            value = "/profile/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UpdateProfileImageResDT updateProfileImage(
            @RequestPart("image") MultipartFile image
    ) {
        return myPageSRV.updateProfileImage(image);
    }
}
