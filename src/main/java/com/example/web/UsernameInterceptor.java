package com.example.web;

import cn.hutool.core.collection.CollUtil;
import com.example.constant.AuthConstants;
import com.example.dto.BaseDTO;
import com.example.enums.ClientType;
import com.example.enums.WebHookType;
import com.example.exception.ClientTypeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xie.wei
 * @date created at 2021-11-27 14:57
 */
public class UsernameInterceptor implements HandlerInterceptor {
    static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        var base = mapper.readValue(request.getInputStream(), BaseDTO.class);
        var strings = Splitter.on(":").splitToList(base.getUsername());
        if (CollUtil.isEmpty(strings) || strings.size() != 2) {
            throw new ClientTypeException("username format is not correct");
        }
        var type = strings.get(0);
        var value = strings.get(1);
        var header = request.getHeader(AuthConstants.AUTH_HEADER_KEY);
        // validate client type
        var clientType = ClientType.getInstance(type);
        // validate vernemq header
        var webHookType = WebHookType.getInstance(header);

        UserNameContext.setHolder(new UserNameContext.ClientTypeHolder(value, clientType, webHookType));
        response.setHeader("Cache-Control", AuthConstants.CACHE_CONTROL.getHeaderValue());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        UserNameContext.close();
    }
}
