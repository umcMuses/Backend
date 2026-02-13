package org.muses.backendbulidtest251228.global.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.global.jwt.JwtTokenProvider;
import org.muses.backendbulidtest251228.global.security.PrincipalDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepo memberRepo;

	@Value("${app.base-url}")
	private String baseUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

		String registrationId = ((OAuth2AuthenticationToken) authentication)
			.getAuthorizedClientRegistrationId().toUpperCase();

		String email = extractEmail(oAuth2User, registrationId);
		logger.info("OAuth2 Login Success");

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		Member member = principalDetails.getMember();

		String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());

		String targetUrl = UriComponentsBuilder.fromUriString(baseUrl)
			.path("/auth/callback")
			.fragment("accessToken=" + accessToken + "&role=" + member.getRole().name())
			.build()
			.encode(StandardCharsets.UTF_8)
			.toUriString();

		clearAuthenticationAttributes(request);
		logger.info("Generated Redirect URL: " + targetUrl);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String extractEmail(OAuth2User oAuth2User, String registrationId) {
		String email = null;

		if ("GOOGLE".equals(registrationId)) {
			email = oAuth2User.getAttribute("email");

		} else if ("KAKAO".equals(registrationId)) {
			Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
			if (kakaoAccount != null) {
				email = (String) kakaoAccount.get("email");
			}
		}
		if (email == null || email.isBlank()) {
			logger.error("Failed to extract email from OAuth2User.");
			throw new IllegalArgumentException("소셜 로그인에서 이메일 정보를 가져올 수 없습니다.");
		}

		return email;
	}
}
