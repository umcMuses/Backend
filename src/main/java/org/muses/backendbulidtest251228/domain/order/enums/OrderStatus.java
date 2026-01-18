package org.muses.backendbulidtest251228.domain.order.enums;

public enum OrderStatus {
    RESERVED,    // 결제 예약됨
    CANCELLED,   // 후원 취소됨
    PAYING,      // 결제 처리 중
    PAID,        // 결제 완료
    PAY_FAILED,  // 결제 실패
    VOID         // 펀딩 실패로 무효 처리
}