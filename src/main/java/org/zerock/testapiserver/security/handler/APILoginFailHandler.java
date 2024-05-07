package org.zerock.testapiserver.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

//FailHandler 를 만들어주는 이유 = ex) 신용카드 결제를 했는데 결과가 제대로 안오고있음
// 서버에서 내부 오류가 떴대
// 근데 뭐 어디서 떴는지 알 수가 없잖아
// 그래서 FailHandler 를 만들어주는 거임 ㅇㅇ
//API 서버는 인증이 잘못됐을 때 어떻게든 메세지 전달을 해줘야 함
//그래야 그쪽에서 받아가지고 아~ 이거 잘못됐구나 그러면서 이제 처리를 할 수 있겠지?
//404 처리 / 200 처리 논쟁이 있지만 구글/카카오 등은 200대로 처리하고 다만 "You Login Fucked Up" 이런 형태로 옴
@Log4j2
public class APILoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.info("Login fail......" + exception);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("error", "ERROR_LOGIN"));

        response.setContentType("application/json"); //상태 코드 : 200 => 정상적으로 응답은 해주되 로그인에 실패했다는 메세지만 전송하겠다는 뜻
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();

        /*
        출력 예시
{
    "error": "ERROR_LOGIN"
}
         */

    }

}
