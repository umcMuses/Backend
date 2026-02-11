package org.muses.backendbulidtest251228.domain.mypage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QrStatus {
    NONE("해당 없음"),
    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String description;
}
