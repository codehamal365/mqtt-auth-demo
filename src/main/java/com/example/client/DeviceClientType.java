package com.example.client;

import cn.hutool.core.collection.CollUtil;
import com.example.config.ConfigMap;
import com.example.constant.AuthConstants;
import com.example.exception.ClientTypeException;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author xie.wei
 * @date created at 2021-12-04 20:37
 */
@Component
@Slf4j
public class DeviceClientType extends AbstractClient {
    static final String TYP = "device";

    protected DeviceClientType(@Lazy ConfigMap configMap) {
        super(configMap, TYP);
    }

    @Override
    protected boolean doAuthenticate(String userName, Map<String, Object> claims) {
        var join = Joiner
                .on("@")
                .join(claims.getOrDefault("given_name", ""),
                        claims.getOrDefault("family_name", ""));
        log.info("device:pdeviceId@pvin is device:{}", join);
        return Objects.equal(userName, join);
    }

    @Override
    protected boolean doAuthorize(String username, ConfigMap.TopicProperties matchedTopic,
                                  Map<String, String> pathMap) {
        List<String> list = Splitter.on("@").splitToList(username);
        if (CollUtil.isEmpty(list) && list.size() != 2) {
            throw new ClientTypeException("device username with pDeviceId and pVin is not correct");
        }
        String pDeviceId = list.get(0);
        String pVin = list.get(1);
        if (pathMap.containsKey(AuthConstants.PDEVICEID_PLACEHOLDER) &&
                !Objects.equal(pDeviceId, pathMap.get(AuthConstants.PDEVICEID_PLACEHOLDER))) {
            return false;
        }
        return !pathMap.containsKey(AuthConstants.PVIN_PLACEHOLDER) ||
                Objects.equal(pVin, pathMap.get(AuthConstants.PVIN_PLACEHOLDER));
    }
}
