package com.example.apects;

import com.example.constant.AuthConstants;
import com.example.dto.BaseDTO;
import com.example.dto.ResponseDTO;
import com.google.common.base.Splitter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

/**
 * @author xie.wei
 * @date created at 2021-11-16 14:31
 */
@Component
@Aspect
@Slf4j
public class ClientTypeAspect {

    @Autowired
    private HttpServletRequest request;

    @Around("execution(* com.example.controller.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();
        Stream.of(args).filter(BaseDTO.class::isInstance).findFirst().ifPresent(arg -> {
            BaseDTO baseDTO = (BaseDTO) arg;
            var username = baseDTO.getUsername();
            try {
                var strings = Splitter.on(":").splitToList(username);
                var type = strings.get(0);
                var value = strings.get(1);
                var header = request.getHeader(AuthConstants.AUTH_HEADER_KEY);
                if (header.contains(AuthConstants.SUB_ACTION)) {
                    header = AuthConstants.SUB_ACTION;
                } else if (header.contains(AuthConstants.PUB_ACTION)) {
                    header = AuthConstants.PUB_ACTION;
                }
                UserNameContext.setHolder(new ClientTypeHolder(username, type, value, header));
            } catch (Exception e) {
                log.error("username:【{}】param is invalid, it should like user:userId",
                        username);
            }
        });
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage());
            return ResponseDTO.errorDefault();
        } finally {
            UserNameContext.close();
        }
    }

    public static class UserNameContext {

        private UserNameContext() {
        }

        static final ThreadLocal<ClientTypeHolder> ctx = new ThreadLocal<>();

        public static void close() {
            ctx.remove();
        }

        public static ClientTypeHolder getHolder() {
            return ctx.get();
        }

        public static void setHolder(ClientTypeHolder holder) {
            ctx.set(holder);
        }
    }

    @Data
    @AllArgsConstructor
    public static class ClientTypeHolder {
        private String username;
        private String type;
        private String value;
        private String action;
    }
}
