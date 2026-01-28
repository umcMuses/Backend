package org.muses.backendbulidtest251228.domain.settlement.service;


import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.settlement.dto.SettlementListResDTO;
import org.muses.backendbulidtest251228.domain.settlement.entity.SettlementENT;
import org.muses.backendbulidtest251228.domain.settlement.enums.SettlementStatus;
import org.muses.backendbulidtest251228.domain.settlement.repository.SettlementRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementSRV {

    private final SettlementRepo settlementRepo;


    public List<SettlementListResDTO> list(SettlementStatus status) {

        List<SettlementENT> settlements =
                (status == null)
                        ? settlementRepo.findAll()
                        : settlementRepo.findByStatus(status);

        return settlements.stream()
                .map(this::toResDTO)
                .toList();
    }


    private SettlementListResDTO toResDTO(SettlementENT settlement) {
        return SettlementListResDTO.builder()
                .totalAmount(settlement.getTotalAmount())
                .feeAmount(settlement.getFeeAmount())
                .payoutAmount(settlement.getPayoutAmount())
                .settlementStatus(settlement.getStatus())
                .title(settlement.getProject().getTitle())
                .id(settlement.getId())
                .build();
    }

    @Transactional
    public void payout(Long id) {

        SettlementENT settlement = settlementRepo.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.BAD_REQUEST,
                        "정산 정보를 찾을 수 없습니다.",
                        Map.of("settlementId", id)
                ));





        settlement.updateAmountAndStatus(
                settlement.getTotalAmount(),
                settlement.getFeeAmount(),
                settlement.getPayoutAmount(),
                SettlementStatus.COMPLETED
        );
    }
}
