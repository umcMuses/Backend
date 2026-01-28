package org.muses.backendbulidtest251228.domain.mypage.service;

import org.muses.backendbulidtest251228.domain.mypage.dto.MyTicketResDT;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface MyPageTicketQuerySRV {
    List<MyTicketResDT> getMyTickets(UserDetails userDetails);
}
