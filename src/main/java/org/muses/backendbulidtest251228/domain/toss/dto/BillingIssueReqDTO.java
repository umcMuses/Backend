package org.muses.backendbulidtest251228.domain.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BillingIssueReqDTO {

    private String authKey;
    private String customerKey;
}

