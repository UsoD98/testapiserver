package org.zerock.testapiserver.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zerock.testapiserver.controller.formatter.LocalDateFormatter;



@Configuration
@Log4j2
public class CustomServletConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {

        log.info("------------------------");
        log.info("addFormatters");

        registry.addFormatter(new LocalDateFormatter());
    }

//    //CORS 설정 : Cross-Origin Resource Sharing (간단히 말하면 다른 도메인에서 요청을 허용하는 것) => security로 넘김
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .maxAge(500)
//                .allowedMethods("GET", "POST", "PUT", "DELETE","HEAD","OPTIONS")
//                .allowedOrigins("*");
//    }
}
