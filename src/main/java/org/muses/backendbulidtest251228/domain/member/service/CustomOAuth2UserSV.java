package org.muses.backendbulidtest251228.domain.member.service;

import org.muses.backendbulidtest251228.domain.member.enums.Provider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserSV extends DefaultOAuth2UserService {

	private final AuthSV authSV;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
		String providerId = oAuth2User.getName();
		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");

		if (email == null || email.isBlank()) {
			throw new OAuth2AuthenticationException("이메일 정보가 없는 사용자입니다.");
		}

		// DB 저장/조회 로직 호출
		// TODO

		// DB 동기화 및 반환
		authSV.socialLoginProcess(email, name, Provider.valueOf(provider), providerId);

		return oAuth2User;
	}
}
