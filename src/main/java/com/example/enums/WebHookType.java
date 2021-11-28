package com.example.enums;

import com.example.exception.ClientTypeException;
import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * @author xie.wei
 * @date created at 2021-11-27 15:49
 */
@AllArgsConstructor
@Getter
public enum WebHookType {
    AUTH_ON_REGISTER("register"),
    AUTH_ON_REGISTER_M5("register"),
    AUTH_ON_PUBLISH("pub"),
    AUTH_ON_PUBLISH_M5("pub"),
    AUTH_ON_SUBSCRIBE("sub"),
    AUTH_ON_SUBSCRIBE_M5("sub");

    String action;

    public static WebHookType getInstance(String headerValue) {
        return Stream
                .of(WebHookType.values())
                .filter(item ->
                        Objects.equal(item.name().toLowerCase(Locale.ROOT), headerValue))
                .findAny()
                .orElseThrow(() -> new ClientTypeException("webhook type is not correct"));
    }
}
