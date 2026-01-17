package org.muses.backendbulidtest251228.domain.billingAuth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.billingAuth.dto.BillingAuthPrepareResDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingAuthSRV implements BillingAuthImpl{

    @Transactional(readOnly = true)
    public BillingAuthPrepareResDTO prepare(String baseSuccessUrl, String baseFailUrl) {

        // 일단 UUID 만 사용해 customerKey 가 종속되지 않게 설계
        String customerKey = "muses_" + UUID.randomUUID();

        log.info("[Billing] Prepared new request. customerKey: {}", customerKey);

        return BillingAuthPrepareResDTO.builder()
                .customerKey(customerKey)
                .successUrl(baseSuccessUrl)
                .failUrl(baseFailUrl)
                .build();
    }


}
