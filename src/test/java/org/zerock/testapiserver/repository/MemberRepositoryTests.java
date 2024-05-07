package org.zerock.testapiserver.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.testapiserver.domain.Member;
import org.zerock.testapiserver.domain.MemberRole;


@SpringBootTest
@Log4j2
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; //bcrypt password encoder

    @Test
    public void testInsertMember(){

        for (int i = 0; i < 10; i++) {
            Member member = Member.builder()
                    .email("user" + i + "@aaa.com")
                    .pw(passwordEncoder.encode("1111"))
                    .nickname("USER" + i)
                    .build();

            member.addRole(MemberRole.USER);
            if (i > 5) {
                member.addRole(MemberRole.MANAGER);
            }
            if (i >= 8) {
                member.addRole(MemberRole.ADMIN);
            }

            memberRepository.save(member);
        }

    }
    // 어떤 권한의 사용자를 읽어오기
    @Test
    public void testRead() {

        String email = "user9@aaa.com";

        // email에 해당하는 멤버를 가져오면서 권한 정보도 함께 가져오기
        Member member = memberRepository.getWithRoles(email);

        log.info("---------------------------");
        log.info(member);
        //처음에 toString으로 권한 조회를 빼놨기 때문에 권한 조회 추가
        log.info(member.getMemberRoleList()); //user9@aaa.com은 USER, MANAGER, ADMIN 권한을 가지고 있음

    }

    //회원가입
    @Test
    public void testRegister(){


    }
}
