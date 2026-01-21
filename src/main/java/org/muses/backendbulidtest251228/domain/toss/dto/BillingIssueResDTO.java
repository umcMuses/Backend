package org.muses.backendbulidtest251228.domain.toss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssueResDTO {

    private String mId;
    private String customerKey;
    private String authenticatedAt;
    private String method;
    private String billingKey;
    private Card card;
    private String cardCompany;
    private String cardNumber;

    @Getter @Setter
    public static class Card {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private String cardType;
        private String ownerType;
    }
}
