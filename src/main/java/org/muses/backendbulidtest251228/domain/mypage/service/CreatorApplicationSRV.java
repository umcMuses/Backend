package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyReqDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyResDT;
import org.springframework.security.core.userdetails.UserDetails;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplicationDocResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplicationSubmitResDT;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CreatorApplicationSRV {
    CreatorApplyResDT apply(UserDetails userDetails, CreatorApplyReqDT req);
    CreatorApplyResDT getMyApplication(UserDetails userDetails);
    CreatorApplicationDocResDT uploadDoc(UserDetails userDetails, String docType, MultipartFile file);
    List<CreatorApplicationDocResDT> getMyDocs(UserDetails userDetails);
    CreatorApplicationSubmitResDT submit(UserDetails userDetails);
}
