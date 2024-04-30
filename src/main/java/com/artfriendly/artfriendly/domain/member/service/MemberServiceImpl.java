package com.artfriendly.artfriendly.domain.member.service;

import com.artfriendly.artfriendly.domain.auth.dto.OAuth2Attributes;
import com.artfriendly.artfriendly.domain.auth.dto.OAuth2LoginDto;
import com.artfriendly.artfriendly.domain.member.dto.MemberDetailsRspDto;
import com.artfriendly.artfriendly.domain.member.dto.MemberUpdateReqDto;
import com.artfriendly.artfriendly.domain.member.dto.ProfileDto;
import com.artfriendly.artfriendly.domain.member.entity.Member;
import com.artfriendly.artfriendly.domain.member.entity.MemberImage;
import com.artfriendly.artfriendly.domain.member.mapper.MemberMapper;
import com.artfriendly.artfriendly.domain.member.repository.MemberRepository;
import com.artfriendly.artfriendly.domain.s3.service.S3Service;
import com.artfriendly.artfriendly.global.exception.common.BusinessException;
import com.artfriendly.artfriendly.global.exception.common.ErrorCode;
import com.artfriendly.artfriendly.global.utils.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Primary
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final MemberMapper memberMapper;
    private final S3Service s3Service;

    @Value("${profile.default-image}")
    String defaultImageUrl;

    @Override
    @Transactional
    public OAuth2LoginDto oauth2Login(String provider, Authentication authentication) {
        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(provider, authentication);
        Optional<Member> member = findOptionalMemberByEmail(oAuth2Attributes.getEmail());
        // 멤버가 존재 하지 않을 때 회원 가입, 있을 경우 Member 객체 반환
        if(member.isPresent()) {
            return new OAuth2LoginDto(member.get(), true);
        }
        else {
            return new OAuth2LoginDto(createMember(
                    oAuth2Attributes.getEmail(),
                    oAuth2Attributes.getNickName(),
                    defaultImageUrl
            ),
                    false);
        }
    }

    @Override
    @Transactional
    public Member createMember(String email, String nickname, String imageUrl) {
        Member member = Member.builder()
                .email(email)
                .nickName(nickname)
                .role(customAuthorityUtils.createUserRoles())
                .build();

        MemberImage memberImage = MemberImage.builder()
                .fileName("Default_Image")
                .imageUrl(imageUrl)
                .member(member)
                .build();

        member.setImage(memberImage);
        return memberRepository.save(member);
    }

    @Override
    public Member findById(Long memberId) {
        Optional<Member> member = memberRepository.findOptionalMemberById(memberId);
        return member.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public MemberDetailsRspDto getMemberDetailsRspDto(long memberId) {
        Member member = findById(memberId);
        return memberMapper.memberToMemberDetailsRspDto(member);
    }

    @Override
    public ProfileDto getProfileDto(long memberId) {
        Member member = findById(memberId);
        return memberMapper.memberToProfileDto(member);
    }

    @Override
    @Transactional
    public void updateMember(MemberUpdateReqDto memberUpdateReqDto, long memberId) {
        Member member = findById(memberId);
        member.updateForm(memberMapper.memberUpdateReqDtoToMember(memberUpdateReqDto));
    }

    @Override
    @Transactional
    public void updateMemberImage(MultipartFile image, long memberId) throws IOException {
        Member member = findById(memberId);
        MemberImage memberImage = member.getImage();

        // 기본 이미지 삭제(카카오톡 프로필 사진 제외)
        if(!memberImage.getFileName().equals("Default_Image")) {
            s3Service.deleteImageByFileName(memberImage.getFileName());
        }

        String fileName = s3Service.fileUpLoad(image);
        String imageUrl = s3Service.getImageUrl(fileName);

        memberImage.updateForm(imageUrl, fileName);
    }

    private Optional<Member> findOptionalMemberByEmail(String email) {
        return memberRepository.findOptionalMemberByEmail(email);
    }
}
