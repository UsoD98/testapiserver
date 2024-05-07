package org.zerock.testapiserver.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JWTUtil {

    // 토큰 생성을 위한 키 : 30자 이상
    // 프로퍼티로 관리하려면 스프링 빈으로 등록해서 사용, 현재는 테스트를 위해 직접 작성
    private static final String KEY = "1234567890123456789012345678901234567890";

    // 토큰 생성 => min: 토큰 유효시간(분)
    public static String generateToken(Map<String, Object> valueMap, int min){

        // 키 생성
        SecretKey key = null;

        // HMAC SHA-256 알고리즘을 사용하는 키 생성
        try{
            key = Keys.hmacShaKeyFor(JWTUtil.KEY.getBytes(StandardCharsets.UTF_8));
        }catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        // 토큰 생성 및 생성된 토큰 반환
        return Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();
    }

    // 토큰 검증
    public static Map<String, Object> validateToken(String token){

        Map<String, Object> claim = null;

        try{

            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.KEY.getBytes(StandardCharsets.UTF_8));

            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();

        }catch(MalformedJwtException malformedJwtException){
            throw new CustomJWTException("MalFormed");
        }catch(ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("Expired");
        }catch(InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("Invalid");
        }catch(JwtException jwtException) {
            throw new CustomJWTException("JWTError");
        }catch(Exception e) {
            throw new CustomJWTException("Error");
        }
        return claim;
    }

}