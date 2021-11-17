package com.example.service.impl;

import com.example.config.ConfigMap;
import com.example.dto.TopicDTO;
import com.example.enums.ClientType;
import com.example.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * auth service
 *
 * @author xie.wei
 * @date created at 2021-11-16 13:43
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookService {

    private final ConfigMap configMap;

    @Override
    public boolean authenticateRegister(String username, String password) {
        return ClientType.getInstance().authenticate(password, configMap.getScopes());
    }

    @Override
    public boolean authorizePublish(String topic) {
        // pub topic without # and +
        return ClientType.getInstance().authorize(topic, configMap.getTopics(), (antPathMatcher, regTopic) ->
                antPathMatcher.match(regTopic
                        .replaceAll("\\+", "*").replaceAll("#", "**"), topic)
        );
    }

    @Override
    public boolean authorizeSubscribe(List<TopicDTO> topics) {
        // sub topic can contains # or +
        for (TopicDTO topic : topics) {
            final String originTopic = topic.getTopic();
            boolean authorize = ClientType.getInstance().authorize(originTopic, configMap.getTopics(),
                    (antPathMatcher, regTopic) -> {
                        boolean matchAll = antPathMatcher.match(regTopic
                                .replaceAll("\\+", "*")
                                .replaceAll("#", "**"), originTopic);
                        boolean matchPoundKey = antPathMatcher.match(regTopic
                                .replaceAll("\\+", "*"), originTopic);
                        boolean matchPlus = antPathMatcher.match(regTopic
                                .replaceAll("#", "**"), originTopic);
                        return matchAll || matchPoundKey || matchPlus;
                    });
            if (!authorize) {
                log.error("{} can not be validated by configuration topics map", originTopic);
                return false;
            }
        }
        return true;
    }
}
