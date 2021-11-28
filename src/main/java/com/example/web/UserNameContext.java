package com.example.web;

import com.example.enums.ClientType;
import com.example.enums.WebHookType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xie.wei
 * @date created at 2021-11-27 16:07
 */
public class UserNameContext {

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


    @Data
    @AllArgsConstructor
    public static class ClientTypeHolder {
        private String value;
        private ClientType clientType;
        private WebHookType webHookType;
    }
}
