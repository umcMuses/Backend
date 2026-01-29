package org.muses.backendbulidtest251228.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.checkin.generator.TokenGenerator;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;

import org.muses.backendbulidtest251228.domain.ticket.enums.TicketStatus;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j // 로그 추적을 위한 어노테이션 추가
@Service
@RequiredArgsConstructor
public class TicketIssueSRV {

    private final TicketRepo ticketRepo;
    private final TokenGenerator tokenGenerator;

    @Transactional
    public void issueIfAbsent(OrderItemENT orderItem) {
        log.info("[티켓 발행 시작] OrderItemID: {}", orderItem.getId());

        // 1. 중복 체크 로그
        if (ticketRepo.findByOrderItem(orderItem).isPresent()) {
            log.warn("[티켓 발행 건너뜀] 이미 해당 주문 항목에 대한 티켓이 존재합니다. OrderItemID: {}", orderItem.getId());
            return;
        }

        // 2. 티켓 생성 및 토큰 생성 로그
        String generatedToken = tokenGenerator.generate();
        log.info("[티켓 생성 중] 신규 토큰 생성 완료: {}", generatedToken);

        TicketENT ticket = TicketENT.builder()
                .orderItem(orderItem)
                .ticketToken(generatedToken)
                .status(TicketStatus.UNUSED)
                .build();

        // 3. 저장 결과 로그
        ticketRepo.save(ticket);
        log.info("[티켓 발행 완료] 티켓 저장 성공. Token: {}, OrderItemID: {}", generatedToken, orderItem.getId());
    }
}