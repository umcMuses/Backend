package org.muses.backendbulidtest251228.domain.member.service;

import java.util.Map;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.enums.Provider;
import org.muses.backendbulidtest251228.global.security.PrincipalDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserSV extends DefaultOAuth2UserService {

	private final AuthSV authSV;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		// GOOGLE, KAKAO
		String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

		String providerId = null;
		String email = null;
		String name = null;

		if ("GOOGLE".equals(registrationId)) {
			providerId = oAuth2User.getName();
			email = oAuth2User.getAttribute("email");
			name = oAuth2User.getAttribute("name");

		} else if ("KAKAO".equals(registrationId)) {
			providerId = String.valueOf(oAuth2User.getAttributes().get("id"));
			Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");

			if (kakaoAccount != null) {
				email = (String) kakaoAccount.get("email");
				Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
				if (profile != null) {
					name = (String) profile.get("nickname");
				}
			}
		}
		log.debug("Social Login Info - Provider: {}, Email: {}, Name: {}", registrationId, email, name);

		if (email == null || email.isBlank()) {
			throw new OAuth2AuthenticationException("소셜 로그인 이메일 정보가 없습니다.");
		}

		Member member = authSV.socialLoginProcess(email, name, Provider.valueOf(registrationId), providerId);

		return new PrincipalDetails(member, oAuth2User.getAttributes());
	}
}
