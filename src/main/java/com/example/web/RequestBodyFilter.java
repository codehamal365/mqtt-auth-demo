package com.example.web;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xie.wei
 * @date created at 2021-11-27 14:40
 */
public class RequestBodyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest use = httpServletRequest;
        if (!(httpServletRequest instanceof BodyCachingRequestWrapper)) {
            use = new BodyCachingRequestWrapper(httpServletRequest);
        }
        filterChain.doFilter(use, httpServletResponse);
    }
}
