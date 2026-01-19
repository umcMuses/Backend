package org.muses.backendbulidtest251228.domain.member.repository;

import java.util.Optional;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);
}
