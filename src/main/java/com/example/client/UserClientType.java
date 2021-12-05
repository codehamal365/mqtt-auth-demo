package com.example.client;

import cn.hutool.core.collection.CollUtil;
import com.example.config.ConfigMap;
import com.example.constant.AuthConstants;
import com.google.common.base.Objects;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author xie.wei
 * @date created at 2021-12-04 20:20
 */
@Component
@Slf4j
public class UserClientType extends AbstractClient {
    static final String TYP = "user";

    public UserClientType(@Lazy ConfigMap configMap) {
        super(configMap, TYP);
    }

    @Override
    protected boolean doAuthenticate(String userName, Map<String, Object> claims) {
        var sub = claims.getOrDefault("sub", "");
        log.info("user:sub is user:{}", sub);
        return Objects.equal(userName, sub);
    }

    @Override
    protected boolean doAuthorize(String username, ConfigMap.TopicProperties matchedTopic,
                                  Map<String, String> pathMap) {
        if (pathMap.containsKey(AuthConstants.USERID_PLACEHOLDER) &&
                !Objects.equal(username, pathMap.get(AuthConstants.USERID_PLACEHOLDER))) {
            return false;
        }

        if (!pathMap.containsKey(AuthConstants.PVIN_PLACEHOLDER)) {
            return true;
        }
        if (CollUtil.isEmpty(matchedTopic.getPermissions())) {
            return true;
        }
        // call vehicle-Management api to check permission
        // todo..
        return true;
    }

}
