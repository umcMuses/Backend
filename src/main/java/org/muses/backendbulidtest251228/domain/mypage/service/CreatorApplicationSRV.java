package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyReqDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyResDT;
import org.springframework.security.core.userdetails.UserDetails;

public interface CreatorApplicationSRV {
    CreatorApplyResDT apply(UserDetails userDetails, CreatorApplyReqDT req);
    CreatorApplyResDT getMyApplication(UserDetails userDetails);
}
