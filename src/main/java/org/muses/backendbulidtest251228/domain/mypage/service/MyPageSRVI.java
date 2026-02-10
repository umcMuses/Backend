package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyProfileResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateMyProfileReqDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateProfileImageResDT;
import org.muses.backendbulidtest251228.domain.mypage.repository.MyPageMemberREP;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.domain.storage.service.AttachmentSRV;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import static org.muses.backendbulidtest251228.global.utils.DateUtil.normalizeBirthday;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageSRVI implements MyPageSRV {

    private final MyPageMemberREP memberREP;
    private final AttachmentSRV attachmentSRV;

    private static final String PROFILE_TARGET_TYPE = "member";
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    @Override
    public MyProfileResDT getMe() {
        Member m = getCurrentMember();
        return toResponse(m);
    }

    @Override
    @Transactional
    public MyProfileResDT updateProfile(UpdateMyProfileReqDT request) {
        Member m = getCurrentMember();

        // 닉네임 중복 체크 (본인 닉네임이면 통과)
        if (request.getNickName() != null
                && (m.getNickName() == null || !m.getNickName().equals(request.getNickName()))
                && memberREP.existsByNickName(request.getNickName())) {
            throw new BusinessException(ErrorCode.DUPLICATE, "이미 사용 중인 닉네임입니다.",
                    Map.of("nickName", request.getNickName()));
        }

        // gender 값 검증 (0/1만)
        if (request.getGender() != null && request.getGender() != 0 && request.getGender() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "gender는 0(남자) 또는 1(여자)만 가능합니다.",
                    Map.of("gender", request.getGender()));
        }

        String birthday = normalizeBirthday(request.getBirthday());

        m.changeProfile(
                request.getNickName(),
                request.getIntroduction(),
                birthday,
                request.getGender()
        );

        return toResponse(m);
    }

    @Override
    @Transactional
    public UpdateProfileImageResDT updateProfileImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미지 파일이 필요합니다.");
        }

        validateImageExtension(image);

        Member m = getCurrentMember();

        // 프로필 이미지는 1개 유지: 기존 전부 삭제 후 업로드
        attachmentSRV.deleteAll(PROFILE_TARGET_TYPE, m.getId());

        AttachmentENT saved = attachmentSRV.upload(PROFILE_TARGET_TYPE, m.getId(), image);
        String url = saved.getFileUrl();

        m.changeProfileImage(url);

        return UpdateProfileImageResDT.builder()
                .profileImgUrl(url)
                .build();
    }

    private void validateImageExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original == null || !original.contains(".")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "파일 확장자가 필요합니다.");
        }
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();

        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "허용되지 않는 이미지 확장자입니다.",
                    Map.of("allowed", ALLOWED_EXT, "extension", ext)
            );
        }
    }

    private MyProfileResDT toResponse(Member m) {
        return MyProfileResDT.builder()
                .memberId(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .nickName(m.getNickName())
                .introduction(m.getIntroduction())
                .birthday(m.getBirthday())
                .gender(m.getGender())
                .profileImgUrl(m.getProfileImgUrl())
                .ticketCount(m.getTicketCount())
                .supportCount(m.getSupportCount())
                .supportLevel(m.getSupportLevel())
                .build();
    }

    private Member getCurrentMember() {
        String email = resolveCurrentEmail();
        return memberREP.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "회원 정보를 찾을 수 없습니다.",
                        Map.of("email", email)
                ));
    }

    private String resolveCurrentEmail() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }

        // 가장 일반적인 케이스: UserDetails.getUsername() == email
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            String email = ud.getUsername();
            if (email == null || email.isBlank()) throw new BusinessException(ErrorCode.AUTH_INVALID);
            return email;
        }

        // 일부 설정에서는 principal이 String(email)로 들어올 수 있음
        if (principal instanceof String s) {
            if (s.isBlank() || "anonymousUser".equalsIgnoreCase(s)) {
                throw new BusinessException(ErrorCode.AUTH_REQUIRED);
            }
            return s;
        }

        // 그 외 타입이면 지금 프로젝트 계약(AuthCTL)과 맞지 않으므로 에러
        throw new BusinessException(
                ErrorCode.AUTH_INVALID,
                "principal에서 email을 찾을 수 없습니다.",
                Map.of("principalClass", principal.getClass().getName())
        );
    }
}
