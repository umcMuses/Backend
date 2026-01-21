package org.muses.backendbulidtest251228.domain.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyProfileResponse;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateMyProfileRequest;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateProfileImageResponse;
import org.muses.backendbulidtest251228.domain.mypage.service.MyPageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public MyProfileResponse getMe() {
        return myPageService.getMe();
    }

    @PostMapping("/profile")
    public MyProfileResponse updateProfile(@Valid @RequestBody UpdateMyProfileRequest request) {
        return myPageService.updateProfile(request);
    }

    // 마지막에 건들 예정
    @PatchMapping(
            value = "/profile/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UpdateProfileImageResponse updateProfileImage(
            @RequestPart("image") MultipartFile image
    ) {
        return myPageService.updateProfileImage(image);
    }
}
