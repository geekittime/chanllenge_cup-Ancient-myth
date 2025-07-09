package com.example.myth.configure;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 在SpringBoot过滤器中配置跨域，
 * 跨域配置不能和拦截器写一起，
 * 会造成冲突，
 * 需要在过滤器中配置跨域，
 * 过滤器在拦截器前进行。
 * 在“Access-Control-Allow-Headers”中，
 * 需要添加上token，
 * 因为前端要传输token到后端，不能过滤掉。
 */


@Component//将此过滤器声明为Spring组件
@Order(Ordered.HIGHEST_PRECEDENCE)//将该过滤器的优先级设置为最高
@WebFilter("/*")//此过滤器应用于所有请求
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");//允许所有来源的跨域请求
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, PATCH");//允许的 HTTP 方法
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type,token,Cookie");//允许的自定义请求头
        response.setHeader("Access-Control-Max-Age", "3600");//预检请求的结果缓存时间为 3600 秒
        response.setHeader("Access-Control-Allow-Credentials","true");
        //检查请求方法是否为 OPTIONS（预检请求）：
        //如果是，设置状态码为 200 并结束处理。
        //否则，继续调用 chain.doFilter(req, res) 以便后续过滤器或目标资源进行处理。
        if("OPTIONS".equalsIgnoreCase(((HttpServletRequest)req).getMethod())){
            response.setStatus(200);
        }else{
            chain.doFilter(req, res);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) {}
}
