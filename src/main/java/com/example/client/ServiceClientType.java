package com.example.client;

import com.example.config.ConfigMap;
import com.example.constant.AuthConstants;
import com.google.common.base.Objects;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author xie.wei
 */
@Component
@Slf4j
public class ServiceClientType extends AbstractClient {

    static final String TYP = "service";

    public ServiceClientType(@Lazy ConfigMap configMap) {
        super(configMap, TYP);
    }

    @Override
    protected boolean doAuthenticate(String userName, Map<String, Object> claims) {
        var serviceName = claims.getOrDefault("clientId", "");
        log.info("service:serviceName is service:{}", serviceName);
        return Objects.equal(userName, serviceName);
    }

    @Override
    protected boolean doAuthorize(String username, ConfigMap.TopicProperties matchedTopic,
                                  Map<String, String> pathMap) {
        return !pathMap.containsKey(AuthConstants.SERVICENAME_PLACEHOLDER) ||
                Objects.equal(username, pathMap.get(AuthConstants.SERVICENAME_PLACEHOLDER));
    }

}
