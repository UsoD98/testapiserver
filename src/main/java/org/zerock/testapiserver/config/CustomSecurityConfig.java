package org.zerock.testapiserver.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zerock.testapiserver.security.filter.JWTCheckFilter;
import org.zerock.testapiserver.security.handler.APILoginFailHandler;
import org.zerock.testapiserver.security.handler.APILoginSuccessHandler;
import org.zerock.testapiserver.security.handler.CustomAccessDeniedHandler;

import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("------------security config------------");
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            // 세션 생성 정책 설정(세션을 생성하지 않음)
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        //CSRF 설정 : Cross-Site Request Forgery (간단히 말하면 사용자가 의도하지 않은 요청을 보내는 것)
        //일반적으로 API 서버를 만들 때에는 안쓰려고 노력함 -> disable
        http.csrf(AbstractHttpConfigurer::disable);

        // 로그인 경로 설정
        http.formLogin(config -> {
            config.loginPage("/api/member/login");
            config.successHandler(new APILoginSuccessHandler()); //로그인 성공 핸들러
            config.failureHandler(new APILoginFailHandler()); //로그인 실패 핸들러
        });

        // 필터 설정
        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        // 예외 처리 설정(403) : 권한 없음
        http.exceptionHandling(config -> {

            config.accessDeniedHandler(new CustomAccessDeniedHandler());

        });

        return http.build();
    }

    //PasswordEncoder 설정 : 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //CORS 설정 : Cross-Origin Resource Sharing (간단히 말하면 다른 도메인에서 요청을 허용하는 것) => security로 넘김
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;


    }

}
