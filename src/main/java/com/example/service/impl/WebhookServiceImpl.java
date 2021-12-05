package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.example.client.IClient;
import com.example.constant.AuthConstants;
import com.example.dto.TopicDTO;
import com.example.exception.AuthorizationException;
import com.example.exception.ClientTypeException;
import com.example.service.WebhookService;
import com.example.util.Tuple;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
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

    private final List<IClient> clients;

    @Override
    public boolean authenticateRegister(String username, String password) {
        var clientType = getClientType(username);
        return clientType.getFirst().authenticate(clientType.getSecond(), password);
    }

    @Override
    public boolean authorizePublish(String username, String topic) {
        // pub topic without # and + due to mqtt rule
        if (topic.contains(PLUS) || topic.contains(POUND_KEY)) {
            throw new AuthorizationException("topic with '+' or '#' is not allowed");
        }
        var clientType = getClientType(username);
        return clientType.getFirst().authorize(clientType.getSecond(), topic,
                AuthConstants.PUB_ACTION);
    }

    @Override
    public boolean authorizeSubscribe(String username, List<TopicDTO> topics) {
        // sub topic can contains # or +
        var clientType = getClientType(username);
        return topics.stream()
                .allMatch(topic -> clientType.getFirst().authorize(clientType.getSecond(), topic.getTopic(),
                        AuthConstants.SUB_ACTION));
    }

    private Tuple<IClient, String> getClientType(String username) {
        var strings = Splitter.on(":").splitToList(username);
        if (CollUtil.isEmpty(strings) || strings.size() != 2) {
            throw new ClientTypeException("username format is not correct");
        }
        var type = strings.get(0);
        var value = strings.get(1);
        // validate client type
        var client = clients.stream().filter(item ->
                Objects.equal(item.type(), type))
                .findAny()
                .orElseThrow(() -> new ClientTypeException("client type is not correct"));
        return new Tuple<>(client, value);
    }
}
