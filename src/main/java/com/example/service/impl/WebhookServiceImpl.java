package com.example.service.impl;

import com.example.config.ConfigMap;
import com.example.dto.TopicDTO;
import com.example.exception.AuthorizationException;
import com.example.service.WebhookService;
import com.example.web.UserNameContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.constant.AuthConstants.PLUS;
import static com.example.constant.AuthConstants.POUND_KEY;

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
        return UserNameContext.getHolder().getClientType()
                .authenticate(password, configMap.getScopes());
    }

    @Override
    public boolean authorizePublish(String topic) {
        // pub topic without # and + due to mqtt rule
        log.debug("publish topic {}, config-topics {}", topic, configMap.getTopics());
        if (topic.contains(PLUS) || topic.contains(POUND_KEY)) {
            throw new AuthorizationException("topic with '+' or '#' is not allowed");
        }
        return UserNameContext.getHolder().getClientType()
                .authorize(topic, configMap.getTopics());
    }

    @Override
    public boolean authorizeSubscribe(List<TopicDTO> topics) {
        // sub topic can contains # or +
        var clientType = UserNameContext.getHolder().getClientType();
        return topics.stream()
//                .peek(topic -> {
//                    if (!clientType.authorize(topic.getTopic(), configMap.getTopics())) {
//                        log.error("{} can not be validated by configuration topics map", topic.getTopic());
//                    }
//                })
                .allMatch(topic -> clientType.authorize(topic.getTopic(), configMap.getTopics()));
    }
}
