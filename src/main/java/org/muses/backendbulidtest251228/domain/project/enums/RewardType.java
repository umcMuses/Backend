package org.muses.backendbulidtest251228.domain.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RewardType {
    TICKET("QR 티켓 발급"),
    NONE("QR 티켓 미발급");

    private final String description;
}
