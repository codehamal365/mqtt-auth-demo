package com.example.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xie.wei
 * @date created at 2021-11-27 14:55
 */
//@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new UsernameInterceptor());
        registration.addPathPatterns("/webhooks/auth/**");
    }

    @Bean
    public FilterRegistrationBean<RequestBodyFilter> bodyFilter() {
        FilterRegistrationBean<RequestBodyFilter> filter = new FilterRegistrationBean<>(new RequestBodyFilter());
        filter.addUrlPatterns("/*");
        filter.setName("bodyFilter");
        filter.setOrder(1);
        return filter;
    }

}
