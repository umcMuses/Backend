package org.muses.backendbulidtest251228.global.security;

import java.util.Collection;
import java.util.Collections;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class PrincipalDetails implements UserDetails {

	private final Member member;

	public PrincipalDetails(Member member) {
		this.member = member;
	}

	public Member getMember() {
		return member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Role Enum -> Spring Security 권한으로 변환
		return Collections.singletonList(
			new SimpleGrantedAuthority("ROLE_" + member.getRole().name())
		);
	}

	@Override
	public String getPassword() {
		return member.getPasswd();
	}

	@Override
	public String getUsername() {
		return member.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() { return true; }

	@Override
	public boolean isAccountNonLocked() { return true; }

	@Override
	public boolean isCredentialsNonExpired() { return true;	}

	@Override
	public boolean isEnabled() { return true; }
}
