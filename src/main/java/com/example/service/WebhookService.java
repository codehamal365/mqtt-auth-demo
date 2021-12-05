package com.example.service;

import com.example.dto.TopicDTO;

import java.util.List;

/**
 * auth service
 *
 * @author xie.wei
 * @date created at 2021-11-16 13:35
 */
public interface WebhookService {

    boolean authenticateRegister(String username, String password);

    boolean authorizePublish(String username, String topic);

    boolean authorizeSubscribe(String username, List<TopicDTO> topics);
}
