package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.MyProfileResponse;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateMyProfileRequest;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateProfileImageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
    MyProfileResponse getMe();
    MyProfileResponse updateProfile(UpdateMyProfileRequest request);
    UpdateProfileImageResponse updateProfileImage(MultipartFile image);
}
