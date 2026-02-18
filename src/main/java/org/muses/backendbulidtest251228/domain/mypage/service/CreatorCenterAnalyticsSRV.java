package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorSettlementResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorSummaryResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorDashboardResDT;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface CreatorCenterAnalyticsSRV {
    CreatorSummaryResDT getMySummary(UserDetails userDetails);
    CreatorDashboardResDT getProjectDashboard(UserDetails userDetails, Long projectId);
    CreatorSettlementResDT getSettlement(UserDetails userDetails, Long projectId);
}
