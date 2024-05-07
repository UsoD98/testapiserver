package org.zerock.testapiserver.dto;

import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//시큐리티가 사용하는 타입에 맞추어서 DTO를 만들어야 함
@Getter
public class MemberDTO extends User {

    private String email, pw, nickname;

    private boolean social;

    private List<String> roleNames = new ArrayList<>();

    //우리는 문자열로 권한을 가지면 되지만 시큐리티는 SimpleGrantedAuthority 객체 타입을 사용하기 때문에 변환해주어야 함
    public MemberDTO(String email, String pw, String nickname, boolean social, List<String> roleNames) {

        super(
                email,
                pw,
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));

        this.email = email;
        this.pw = pw;
        this.nickname = nickname;
        this.social = social;
        this.roleNames = roleNames;
    }

    //위에서 만든 객체를 주고받는 것이 아닌 JWT 문자열을 주고받는 것이기 때문에 이를 변환하는 메서드가 필요함
    //JWT 문자열의 내용을 클레임이라고 한다. 이를 만들어주는 기능이 필요함
    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", email);
        dataMap.put("pw", pw);
        dataMap.put("nickname", nickname);
        dataMap.put("social", social);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }

}
