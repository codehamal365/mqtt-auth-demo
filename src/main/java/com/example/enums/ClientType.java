package com.example.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.example.apects.ClientTypeAspect;
import com.example.config.ConfigMap;
import com.example.constant.AuthConstants;
import com.example.exception.ClientTypeException;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static java.util.stream.Collectors.toList;

/**
 * @author xie.wei
 * @date created at 2021-11-16 13:51
 */
@AllArgsConstructor
@Slf4j
public enum ClientType {

    USER("user") {
        @Override
        boolean authJwt(String value, Map<String, Object> claims) {
            var sub = claims.getOrDefault("sub", "");
            log.info("user:sub is user:{}", sub);
            return Objects.equal(value, sub);
        }
    },
    DEVICE("device") {
        @Override
        boolean authJwt(String value, Map<String, Object> claims) {
            var join = Joiner
                    .on("@")
                    .join(claims.getOrDefault("given_name", ""),
                            claims.getOrDefault("family_name", ""));
            log.info("device:pdeviceId@pvin is device:{}", join);
            return Objects.equal(value, join);
        }
    },
    SERVICE("service") {
        @Override
        boolean authJwt(String value, Map<String, Object> claims) {
            var serviceName = claims.getOrDefault("preferred_username", "");
            log.info("service:serviceName is service:{}", serviceName);
            return Objects.equal(value, serviceName);
        }
    };

    @Getter
    String typeName;

    public static ClientType getInstance() {
        var holder = ClientTypeAspect.UserNameContext
                .getHolder();
        return Stream.of(ClientType.values()).filter(type -> Objects.equal(type.getTypeName(),
                Optional.ofNullable(holder).map(ClientTypeAspect.ClientTypeHolder::getType)
                        .orElseThrow(() -> new ClientTypeException("parse client from username error"))))
                .findAny().orElseThrow(() -> new ClientTypeException("client type is not correct"));
    }

    public static List<String> getClientTypeList() {
        return Stream.of(ClientType.values())
                .map(ClientType::getTypeName).collect(toList());
    }

    public final boolean authenticate(String password, List<String> configMapScopes) {
        try {
            var jwt = SignedJWT.parse(password);
            var claims = jwt.getJWTClaimsSet().getClaims();
            // check expiry
            var exp = Convert.toLong(claims.get("exp"), 0L);
            log.info("token expiry time is, 【{}】", exp);
            // check scopes
            var scope = Convert.toStr(claims.get("scope"), "");
            var claimScopes = Splitter.on(" ").splitToList(scope);
            log.info("claimScopes is【{}】and configMapScopes is 【{}】", claimScopes, configMapScopes);
            // check user name
            // todo >=
            return exp <= Calendar.getInstance().getTime().getTime()
                    && claimScopes.containsAll(configMapScopes)
                    && authJwt(ClientTypeAspect.UserNameContext
                    .getHolder().getValue(), claims);
        } catch (ParseException e) {
            log.error("jwt extract password error, please confirm");
            return false;
        }
    }

    public final boolean authorize(String topic, List<ConfigMap.TopicProperties> topics,
                                   BiPredicate<String[], String[]> predict) {
        if (isBlank(topic)) {
            return false;
        }
        // match client type and actions
        var filterList = topics
                .stream()
                .filter(item -> item.getClient().contains(getTypeName()) && item.getActions()
                        .contains(ClientTypeAspect.UserNameContext
                                .getHolder().getAction()))
                .map(ConfigMap.TopicProperties::getTopic)
                .collect(toList());
        if (CollUtil.isEmpty(filterList)) {
            return false;
        }
        return authTopic(topic, filterList, predict);
    }

    private boolean authTopic(String topic, List<String> filterList, BiPredicate<String[], String[]> predict) {
        var origin = topic.split(AuthConstants.SLASH);
        for (String configTopic : filterList) {
            var split = configTopic.split(AuthConstants.SLASH);
            // todo here
//            if (predict.test(origin, split)) {
//                return true;
//            }

            // pub e.g
            /**
             *  user /user
             *
             *
             */
            out:
            for (String or : origin) {
                for (String config : split) {
                    if (or.equals(config)) {
                        continue out;
                    } else {
//                        if(){
//
//                        }
                    }


                }
            }

        }
        return false;
    }

    public static void main(String[] args) {
        String s = "user";
        String s1 = "/user";
        final String[] split = s.split("/");

        final String[] split1 = s1.split("/");
        System.out.println("========");
    }

    abstract boolean authJwt(String value, Map<String, Object> claims);

}
