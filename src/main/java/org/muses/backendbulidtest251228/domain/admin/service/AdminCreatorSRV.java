package org.muses.backendbulidtest251228.domain.admin.service;

import org.muses.backendbulidtest251228.domain.admin.dto.AdminCreatorDT;
import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;

public interface AdminCreatorSRV {

	AdminCreatorDT.ApplicationListResponse getApplicationList(ApplicationStatus status, int page, int size);

}
