package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.MyProfileResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateMyProfileReqDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateProfileImageResDT;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageSRV {
    MyProfileResDT getMe();
    MyProfileResDT updateProfile(UpdateMyProfileReqDT request);
    UpdateProfileImageResDT updateProfileImage(MultipartFile image);
}
