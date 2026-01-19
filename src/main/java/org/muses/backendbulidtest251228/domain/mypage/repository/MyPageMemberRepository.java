package org.muses.backendbulidtest251228.domain.mypage.repository;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyPageMemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickName(String nickName);
}
