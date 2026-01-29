package org.muses.backendbulidtest251228.domain.mypage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocType {
	ID_CARD("신분증"),
	BANKBOOK("통장 사본"),
	BRC("사업자등록증 사본"),
	COMP_SEAL("법인 인감증명서"),
	COMP_REGISTRY("법인 등기부등본");

	private final String description;
}
