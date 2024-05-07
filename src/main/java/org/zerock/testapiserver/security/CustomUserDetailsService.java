package org.zerock.testapiserver.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.testapiserver.domain.Member;
import org.zerock.testapiserver.dto.MemberDTO;
import org.zerock.testapiserver.repository.MemberRepository;

import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    // MemberRepository 주입
    private final MemberRepository memberRepository;

    //username은 우리에게 아이디에 해당하는 값(지금은 이메일)이다.
    //리턴타입인 UserDetails는 우리에게 MemberDTO에 해당하는 객체이다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 메소드 동작 확인
        log.info("----------loadUserByUsername----------" + username);

        // MemberRepository의 getWithRoles 메소드를 이용하여 username에 해당하는 Member 객체를 가져온다.
        Member member = memberRepository.getWithRoles(username);

        // 가져온 Member 객체가 null이라면 UsernameNotFoundException을 발생시킨다.
        if(member == null) {
            throw new UsernameNotFoundException("NOT FOUND");
        }

        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList()
                        .stream()
                        .map(memberRole -> memberRole.name()).collect(Collectors.toList()));
// 위에서 memberRole은 MemberRole 객체이다. 이를 name() 메소드를 이용하여 문자열로 변환한다.
        log.info(memberDTO);

        return memberDTO;
    }


}
