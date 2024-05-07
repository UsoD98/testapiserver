package org.zerock.testapiserver.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.testapiserver.domain.Member;
import org.zerock.testapiserver.dto.MemberDTO;
import org.zerock.testapiserver.dto.MemberModifyDTO;

import java.util.stream.Collectors;

import static org.zerock.testapiserver.domain.QMember.member;

@Transactional
public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);

    void modifyMember(MemberModifyDTO memberModifyDTO);

    void registerMember(MemberDTO memberDTO);

    default MemberDTO entityToDTO(Member member){

        MemberDTO dto = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList()));

        return dto;
    }

    default Member dtoToEntity(MemberDTO memberDTO){

        Member member = Member.builder()
                .email(memberDTO.getEmail())
                .pw(memberDTO.getPw())
                .nickname(memberDTO.getNickname())
                .social(memberDTO.isSocial())
                .build();

        return member;
    }



}
