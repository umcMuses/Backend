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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MyPageCTL {

    private final MyPageSRV myPageSRV;

    @GetMapping
    public MyProfileResDT getMe() {
        return myPageSRV.getMe();
    }

    @PostMapping("/profile")
    public MyProfileResDT updateProfile(@Valid @RequestBody UpdateMyProfileReqDT request) {
        return myPageSRV.updateProfile(request);
    }

    // 마지막에 건들 예정
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
