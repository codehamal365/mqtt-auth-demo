package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.config.ConfigMap;
import com.example.constant.AuthConstants;
import com.example.dto.TopicDTO;
import com.example.enums.ClientType;
import com.example.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xie.wei
 * @date created at 2021-11-16 13:43
 */
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final ConfigMap configMap;

    @Override
    public boolean authenticateRegister(String username, String password) {
        return ClientType.getInstance().authenticate(password, configMap.getScopes());
    }

    @Override
    public boolean authorizePublish(String topic) {
        var topics = configMap.getTopics();
        if (StrUtil.isEmpty(topic)) {
            return false;
        }
        if (!topic.contains("/")) {
            final List<ConfigMap.@Valid TopicProperties> collect = topics.stream()
                    .filter(pro -> StrUtil.equals(topic, pro.getTopic()) ||
                            StrUtil.equalsAny(pro.getTopic(), AuthConstants.POUND_KEY, AuthConstants.PLUS))
                    .collect(Collectors.toList());
            if(CollUtil.isEmpty(collect)){
                return false;
            }
            collect.forEach(pro ->{


            });

        }

        return true;
    }

//    private boolean checkTopic(){
//
//    }

    public static void main(String[] args) {
        final String[] split = "/users/{userId}/vehicles/{pvin}/#".split("/");
        System.out.println(split);
    }

    @Override
    public boolean authorizeSubscribe(List<TopicDTO> topics) {
        return true;
    }
}
