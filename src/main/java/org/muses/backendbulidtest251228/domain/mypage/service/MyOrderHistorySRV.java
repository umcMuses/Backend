package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.MyOrderHistoryResDT;
import org.springframework.security.core.userdetails.UserDetails;

public interface MyOrderHistorySRV {
    MyOrderHistoryResDT.OrderHistoryListResponse getMyOrders(UserDetails userDetails);
    MyOrderHistoryResDT.OrderHistoryDetailResponse getMyOrderDetail(UserDetails userDetails, Long orderId);
}