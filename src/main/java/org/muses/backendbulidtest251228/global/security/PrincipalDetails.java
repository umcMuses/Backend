package org.muses.backendbulidtest251228.global.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {

	private final Member member;
	private Map<String, Object> attributes;

	// 일반 로그인
	public PrincipalDetails(Member member) {
		this.member = member;
	}
	// 소셜 로그인
	public PrincipalDetails(Member member, Map<String, Object> attributes) {
		this.member = member;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return member.getProviderId();
	}

	public Long getId() {
		return member.getId();
	}

	public Long getMemberId() {
		return member.getId();
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
