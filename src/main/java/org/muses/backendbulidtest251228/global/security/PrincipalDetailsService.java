package org.muses.backendbulidtest251228.global.security;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

	private final MemberRepo memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 유저가 없습니다."));

		return new PrincipalDetails(member);
	}
}
