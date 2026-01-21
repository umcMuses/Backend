package org.muses.backendbulidtest251228.domain.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeLimit {
    ALL("전체 이용가"),
    ADULT("19세 이상");

    private final String description;
}
