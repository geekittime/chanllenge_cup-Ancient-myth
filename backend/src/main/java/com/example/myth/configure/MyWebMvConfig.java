package com.example.myth.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 这个类实现了WebMvcConfigurer接口，
 * 表示会被SpringBoot接受，
 * 这个类的作用是配置拦截器。
 * addInterceptors方法配置了拦截器，
 * 添加了loginInterceptor作为拦截器，
 * 并且设置除了register和login的所有接口都需要通过该拦截器。
 */


@Configuration
public class MyWebMvConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/api/accounts")
//                .excludePathPatterns("/api/accounts/login")
//                //.excludePathPatterns("/api/accounts/**")
//                .excludePathPatterns("/api/orders/{orderId}/pay")
//                .excludePathPatterns("/api/orders/notify")
//                .excludePathPatterns("/api/stream")
//                .excludePathPatterns("/api/chat/session")
//                .excludePathPatterns("/api/chat/sessions")
//                .order(1);
//    }
}
