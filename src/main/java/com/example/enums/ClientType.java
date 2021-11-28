package com.example.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.example.config.ConfigMap;
import com.example.constant.AuthConstants;
import com.example.exception.AuthenticationException;
import com.example.exception.AuthorizationException;
import com.example.exception.ClientTypeException;
import com.example.web.UserNameContext;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.constant.AuthConstants.PLUS;
import static com.example.constant.AuthConstants.POUND_KEY;
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

        @Override
        boolean authorizeClientValue(ConfigMap.TopicProperties matchedTopic,
                                     Map<String, String> stringMap,
                                     String typeValue) {
            if (stringMap.containsKey(AuthConstants.USERID_PLACEHOLDER) &&
                    !Objects.equal(typeValue, stringMap.get(AuthConstants.USERID_PLACEHOLDER))) {
                return false;
            }

            if (!stringMap.containsKey(AuthConstants.PVIN_PLACEHOLDER)) {
                return true;
            }
            if (CollUtil.isEmpty(matchedTopic.getPermissions())) {
                return true;
            }
            // call vehicle-Management api to check permission
            // todo..
            return true;
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

        @Override
        boolean authorizeClientValue(ConfigMap.TopicProperties matchedTopic,
                                     Map<String, String> stringMap,
                                     String typeValue) {
            List<String> list = Splitter.on("@").splitToList(typeValue);
            if (CollUtil.isEmpty(list) && list.size() != 2) {
                throw new ClientTypeException("device username with pDeviceId and pVin is not correct");
            }
            String pDeviceId = list.get(0);
            String pVin = list.get(1);
            if (stringMap.containsKey(AuthConstants.PDEVICEID_PLACEHOLDER) &&
                    !Objects.equal(pDeviceId, stringMap.get(AuthConstants.PDEVICEID_PLACEHOLDER))) {
                return false;
            }
            return !stringMap.containsKey(AuthConstants.PVIN_PLACEHOLDER) ||
                    Objects.equal(pVin, stringMap.get(AuthConstants.PVIN_PLACEHOLDER));
        }
    },
    SERVICE("service") {
        @Override
        boolean authJwt(String value, Map<String, Object> claims) {
            var serviceName = claims.getOrDefault("clientId", "");
            log.info("service:serviceName is service:{}", serviceName);
            return Objects.equal(value, serviceName);
        }

        @Override
        boolean authorizeClientValue(ConfigMap.TopicProperties matchedTopic, Map<String, String> stringMap,
                                     String typeValue) {
            return !stringMap.containsKey(AuthConstants.SERVICENAME_PLACEHOLDER) ||
                    Objects.equal(typeValue, stringMap.get(AuthConstants.SERVICENAME_PLACEHOLDER));
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
            return exp >= Calendar.getInstance().getTime().getTime()
                    && claimScopes.containsAll(configMapScopes)
                    && authJwt(UserNameContext.getHolder().getValue(), claims);
        } catch (ParseException e) {
            log.error("jwt extract password error, please confirm");
            throw new AuthenticationException("password is invalid");
        }
    }

    /**
     * @param topic  original topic form client for publishing or subscribing
     * @param topics topics need be checked from configuration
     * @return true or false
     */
    public final boolean authorize(String topic, List<ConfigMap.TopicProperties> topics) {
        // match client type and actions
        var filterList = topics
                .stream()
                .filter(item -> item.getClient().contains(getTypeName()) && item.getActions()
                        .contains(UserNameContext.getHolder().getWebHookType().getAction()))
                .collect(toList());
        if (CollUtil.isEmpty(filterList)) {
            return false;
        }
        return authTopic(topic, filterList);
    }

    private boolean authTopic(String topic, List<ConfigMap.TopicProperties> filterList) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        // topic from client should replace placeholder
        if (pathMatcher.isPattern(topic)) {
            log.error("topic: {} with placeholder is invalid, please check", topic);
            throw new AuthorizationException("topic with placeholder is in valid");
        }
        ConfigMap.TopicProperties matchedTopic = null;
        for (ConfigMap.TopicProperties configTopic : filterList) {
            String regTopic = configTopic.getTopic();
            // always matched the first one and pre-condition all the configuration topics is unique
            if (topic.equals(regTopic) || pathMatcher.match(regTopic
                    .replace(PLUS, "*")
                    .replace(POUND_KEY, "**"), topic)) {
                matchedTopic = configTopic;
                break;
            }
        }
        if (matchedTopic == null) {
            return false;
        }
        Map<String, String> stringMap = pathMatcher
                .extractUriTemplateVariables(matchedTopic.getTopic()
                        .replace(PLUS, "*")
                        .replace(POUND_KEY, "**"), topic);
        return authorizeClientValue(matchedTopic, stringMap, UserNameContext.getHolder().getValue());
    }

    abstract boolean authJwt(String value, Map<String, Object> claims);

    abstract boolean authorizeClientValue(ConfigMap.TopicProperties matchedTopic,
                                          Map<String, String> stringMap, String typeValue);

    public static ClientType getInstance(String type) {
        return Stream.of(ClientType.values()).filter(item ->
                Objects.equal(item.getTypeName(), type))
                .findAny()
                .orElseThrow(() -> new ClientTypeException("client type is not correct"));
    }

    public static List<String> getClientTypeList() {
        return Stream.of(ClientType.values())
                .map(ClientType::getTypeName).collect(toList());
    }
}
