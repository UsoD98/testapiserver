package org.zerock.testapiserver.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "memberRoleList") //ToString할 때 제외를 시켜 놓지 않으면 lazy loading이 발생할 수 있음
public class Member {

    @Id
    private String email;

    private String pw;

    private String nickname;

    //소셜로그인 구분
    private boolean social;

    //ELEMENT COLLECTION 사용
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default //처음부터 초기화 해주는 어노테이션
    private List<MemberRole> memberRoleList = new ArrayList<>();

    //권한 추가/삭제
    public void addRole(MemberRole memberRole){

        memberRoleList.add(memberRole);
    }

    public void clearRole(){
        memberRoleList.clear();
    }

    //회원정보 수정
    public void changeSocial(boolean social) {
        this.social = social;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePw(String pw) {
        this.pw = pw;
    }

}