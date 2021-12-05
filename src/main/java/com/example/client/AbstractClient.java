package com.example.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.example.config.ConfigMap;
import com.example.exception.AuthenticationException;
import com.example.exception.AuthorizationException;
import com.google.common.base.Splitter;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import static com.example.constant.AuthConstants.PLUS;
import static com.example.constant.AuthConstants.POUND_KEY;
import static java.util.stream.Collectors.toList;

/**
 * @author xie.wei
 * @date created at 2021-12-04 20:04
 */
@Slf4j
public abstract class AbstractClient implements IClient {
    private final ConfigMap configMap;
    private final String type;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    protected AbstractClient(ConfigMap configMap, String type) {
        this.configMap = configMap;
        this.type = type;
    }

    @Override
    public final boolean authenticate(String username, String password) {
        try {
            var jwt = SignedJWT.parse(password);
            var claims = jwt.getJWTClaimsSet().getClaims();
            // check expiry
            var exp = Convert.toLong(claims.get("exp"), 0L);
            log.info("token expiry time is, 【{}】", exp);
            // check scopes
            var scope = Convert.toStr(claims.get("scope"), "");
            var claimScopes = Splitter.on(" ").splitToList(scope);
            log.info("claimScopes is【{}】and configMapScopes is 【{}】", claimScopes, getConfig().getScopes());
            // check user name
            return exp >= Calendar.getInstance().getTime().getTime()
                    && claimScopes.containsAll(getConfig().getScopes())
                    && doAuthenticate(username, claims);
        } catch (ParseException e) {
            log.error("jwt extract password error, please confirm");
            throw new AuthenticationException("password is invalid");
        }
    }

    @Override
    public boolean authorize(String username, String topic, String action) {
        var filterList = getConfig().getTopics()
                .stream()
                .filter(item -> item.getClient().contains(type)
                        && item.getActions().contains(action))
                .collect(toList());
        if (CollUtil.isEmpty(filterList)) {
            return false;
        }
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
        Map<String, String> pathMap = pathMatcher
                .extractUriTemplateVariables(matchedTopic.getTopic()
                        .replace(PLUS, "*")
                        .replace(POUND_KEY, "**"), topic);
        return doAuthorize(username, matchedTopic, pathMap);
    }

    @Override
    public final ConfigMap getConfig() {
        return configMap;
    }

    @Override
    public final String type() {
        return type;
    }

    protected abstract boolean doAuthenticate(String userNameVal, Map<String, Object> claims);

    protected abstract boolean doAuthorize(String username, ConfigMap.TopicProperties matchedTopic,
                                           Map<String, String> pathMap);

}
