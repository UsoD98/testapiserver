package org.zerock.testapiserver.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.testapiserver.dto.MemberDTO;
import org.zerock.testapiserver.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    // 어떤 경로로 들어오면 필터를 타게 할 것인가 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        //true == not checking

        String path = request.getRequestURI();

        log.info("check uri----------" + path);

        // /api/member/로 시작하는 경로는 필터를 타지 않게 설정
        if(path.startsWith("/api/member/")|| path.startsWith("/member/kakao/")) {
            return true;
        }

        //false == check
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("--------------------");

        log.info("--------------------");

        log.info("--------------------");

        // 헤더에 있는 Authorization 정보를 가져온다.
        String authHeaderStr = request.getHeader("Authorization");

        // 현재는 클라이언트 쪽에 accesstoken에 문제가 있을 시 메세지를 전달하는 수준까지만 처리
        // 실제로는 예외를 던지거나, 필터를 탈출하는 방식으로 처리해야 한다.
        try {
            //Bearer //7 JWT문자열(앞의 7글자 제외)
            String accessToken = authHeaderStr.substring(7);
            //JWTUtil로 검증
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            // claims에는 토큰에 담긴 정보가 담겨있다.
            log.info(claims);

            //dest : 다음 목적지로 가기 위한 것
            // 성공하면 다음 필터로, 실패하면 예외 발생
//            filterChain.doFilter(request, response);

            //남은 try 안의 코드 요약
            // claims에서 email, pw, nickname, social, roleNames를 가져와서 MemberDTO를 만들고
            // 이를 이용하여 AuthenticationToken을 만들어서 SecurityContextHolder에 넣어주는 작업
            String email = (String) claims.get("email");
            String pw = (String) claims.get("pw");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social.booleanValue(), roleNames);

            log.info("------------------------------");
            log.info(memberDTO);
            log.info(memberDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        }catch(Exception e) {

            log.error("JWT Check Error............");
            log.error(e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();

        }
    }
}
