package org.zerock.testapiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.testapiserver.util.CustomJWTException;
import org.zerock.testapiserver.util.JWTUtil;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    //사실은 POST로 처리해야 하지만 테스트를 위해 GET으로 처리
    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            String refreshToken
    ) {

        // refresh token이 없는 경우
        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }

        // Authorization 헤더가 없는 경우
        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID STRING");
        }
        // Bearer xxxx
        String accessToken = authHeader.substring(7);

        //AccessToken의 만료여부 확인
        //만료되지 않았다면 AccessToken을 그대로 반환
        if(!checkExpiredToken(accessToken)){
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // Refresh 토큰 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        log.info("refresh ... claims : " + claims);

        String newAccessToken = JWTUtil.generateToken(claims, 10);

        String newRefreshToken = checkTime((Integer)claims.get("exp")) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

    }


    //시간이 1시간 이하로 남았는지 확인
    private boolean checkTime(Integer exp){

        // JWT exp를 날짜로 변환
        java.util.Date expDate = new java.util.Date( (long)exp * (1000 ));

        // 현재 시간과의 차이 계산 - 밀리세컨즈
        long gap = expDate.getTime() - System.currentTimeMillis();

        // 분단위 계산
        long leftMin = gap / (1000 * 60);

        //1시간 미만으로 남았는지 확인
        return leftMin < 60;
    }

    //AccessToken의 만료여부 확인
    private boolean checkExpiredToken(String token) {

        try{
            JWTUtil.validateToken(token);
        }catch(CustomJWTException ex){
            if(ex.getMessage().equals("Expired")){
                return true;
            }
        }
        return false;
    }
}
