package org.muses.backendbulidtest251228.global.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.global.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
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

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");

		Member member = memberRepo.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));

		String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());

		// TODO: 추후 프론트 리다이렉트 URL 변경
		String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login/success")
			.queryParam("accessToken", accessToken)
			.queryParam("role", member.getRole().name())
			.build()
			.encode(StandardCharsets.UTF_8)
			.toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
