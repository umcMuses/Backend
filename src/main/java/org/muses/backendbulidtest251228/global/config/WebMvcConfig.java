package org.muses.backendbulidtest251228.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 정적 리소스 설정
 * - 업로드된 파일을 /files/** 경로로 접근 가능하게 설정
 */
@Configuration
@Profile("local")
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${storage.local.path:./uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /files/** 요청을 로컬 uploads 폴더로 매핑
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
