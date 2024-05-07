package org.zerock.testapiserver.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.testapiserver.dto.MemberDTO;
import org.zerock.testapiserver.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

//Config에서 추가하여 사용할 것이기 때문에 스프링의 빈으로 등록할 필요가 없다.
@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //동작 확인
        log.info("--------------------");
        log.info(authentication);
        log.info("--------------------");

        //MemberDTO 끄집어내서 가공(추후 JWT 생성을 위해)
        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        //MemberDTO의 getClaims()를 이용하여 Map<String, Object> 타입의 객체를 가져온다.
        //추가적인 정보를 넣기 위해(access token, refresh token 등)
        Map<String, Object> claims = memberDTO.getClaims();

        // access token 생성(짧게 주면 10분, 길게 주면 하루 정도 주기도 함)
        String accessToken = JWTUtil.generateToken(claims, 10);
        // refresh token 생성(하루 주기도 함)
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken",accessToken);
        claims.put("refreshToken",refreshToken);

        //JSON 형태로 변환
        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);

        //지금부터 내가 보낸 건 JSON이고, charset은 UTF-8이다. 잘 알아둬라 범부여
        //UTF-8로 설정하는 이유는 닉네임 등 한글이 포함될 수 있기 때문이다.
        response.setContentType("application/json; charset=UTF-8");

        //JSON 문자열을 출력(java.io가 필요)
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();

        /*
        출력 결과 예시
{
    "social": false,
    "pw": "$2a$10$I1KPznaj6j/gzrk1k8DFxuXMDiCuBwLFLXgEWLApVhuM8CsFoHw92",
    "nickname": "USER9",
    "accessToken": "",
    "roleNames": [
        "USER",
        "MANAGER",
        "ADMIN"
    ],
    "email": "user9@aaa.com",
    "refreshToken": ""
}
         */
        //어 이러면 JWT 인증하면 토큰만 있으면 되는 것 아닌가? ㅇㅇ맞음
        //하지만 로그인을 리액트에서 호출해서 가져가서 사용자 정보를 화면에 출력할 때 암호화된 문자열만 있으면 알아보기가 힘들다
        //그래서 추가적인 정보를 넣어주는 것이다.
        //이 정보가 나중에 쿠키 같은 걸로 보관해야 되는 정보가 되는거다 알겠나?

    }
}
