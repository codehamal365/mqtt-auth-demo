package com.example.service;

import com.example.dto.TopicDTO;

import java.util.List;

/**
 * @author xie.wei
 * @date created at 2021-11-16 13:35
 */
public interface WebhookService {

    boolean authenticateRegister(String username, String password);

    boolean authorizePublish(String topic);

    boolean authorizeSubscribe(List<TopicDTO> topics);
}
