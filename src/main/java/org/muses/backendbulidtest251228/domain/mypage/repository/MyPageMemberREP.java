package org.muses.backendbulidtest251228.domain.mypage.repository;

import java.util.Optional;

import java.util.List;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyPageMemberREP extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByNickName(String nickName);

    List<Member> findAllByIdIn(List<Long> ids);
}
