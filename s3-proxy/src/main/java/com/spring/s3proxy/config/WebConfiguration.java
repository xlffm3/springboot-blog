package com.spring.s3proxy.config;

import com.spring.s3proxy.common.FileValidator;
import com.spring.s3proxy.common.ImageFileValidator;
import com.spring.s3proxy.config.resolver.ValidExtensionArgumentResolver;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    public ValidExtensionArgumentResolver fileExtensionValidator() {
        return new ValidExtensionArgumentResolver(fileValidators());
    }

    @Bean
    public List<FileValidator> fileValidators() {
        return Arrays.asList(new ImageFileValidator());
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(fileExtensionValidator());
    }
}
