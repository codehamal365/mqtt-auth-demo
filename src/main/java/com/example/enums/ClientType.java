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
import org.apache.catalina.User;
import org.springframework.util.AntPathMatcher;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static java.util.stream.Collectors.toList;

/**
 * client type class
 *
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

    /**
     * authenticate the registration
     *
     * @param password        token password
     * @param configMapScopes scopes need be checked from configuration
     * @return true or false
     */
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

    /**
     * @param topic   original topic form client for publishing or subscribing
     * @param topics  topics need be checked from configuration
     * @param predict function to validate topic either publish or subscribe function
     * @return true or false
     */
    public final boolean authorize(String topic, List<ConfigMap.TopicProperties> topics,
                                   BiPredicate<AntPathMatcher, String> predict) {
        if (isBlank(topic)) {
            return false;
        }
        // match client type and actions
        var filterList = topics
                .stream()
                .filter(item -> item.getClient().contains(getTypeName()) && item.getActions()
                        .contains(ClientTypeAspect.UserNameContext.getHolder().getAction()))
                .collect(toList());
        if (CollUtil.isEmpty(filterList)) {
            return false;
        }
        return authTopic(topic, filterList, predict);
    }

    private boolean authTopic(String topic, List<ConfigMap.TopicProperties> filterList,
                              BiPredicate<AntPathMatcher, String> predict) {
        // todo can be cached?
        AntPathMatcher pathMatcher = new AntPathMatcher();
        ConfigMap.TopicProperties matchedTopic = null;
        for (ConfigMap.TopicProperties configTopic : filterList) {
            String regTopic = configTopic.getTopic();
            // always matched the first one and pre-condition all the configuration topics is unique
            if (topic.equals(regTopic) || predict.test(pathMatcher, regTopic)) {
                matchedTopic = configTopic;
                break;
            }
        }
        if (matchedTopic == null) {
            return false;
        }
        if (this == ClientType.USER) {
            Map<String, String> stringMap = pathMatcher
                    .extractUriTemplateVariables(matchedTopic.getTopic().replaceAll("\\+", "*")
                            .replaceAll("#", "**"), topic);
            if (!stringMap.containsKey(AuthConstants.PVIN_PLACEHOLDER)) {
                return true;
            }
            if (CollUtil.isEmpty(matchedTopic.getPermissions())) {
                return true;
            }
            // call vehicle-Management api to check permission
            // todo..
        }
        return true;
    }

    abstract boolean authJwt(String value, Map<String, Object> claims);

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
}
