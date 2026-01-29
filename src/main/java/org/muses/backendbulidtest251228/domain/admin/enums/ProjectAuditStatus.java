package org.muses.backendbulidtest251228.domain.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectAuditStatus {

	DRAFT("작성중"),
	PENDING("검토중"),
	REVISION_REQUEST("수정요청"),
	APPROVED("승인됨"),
	REJECTED("반려됨");

	private final String description;
}
