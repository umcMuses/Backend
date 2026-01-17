package org.muses.backendbulidtest251228.domain.billingAuth.service;

import org.muses.backendbulidtest251228.domain.billingAuth.dto.BillingAuthPrepareResDTO;

public interface BillingAuthImpl {


    BillingAuthPrepareResDTO prepare(String baseSuccessUrl, String baseFailUrl);

}
