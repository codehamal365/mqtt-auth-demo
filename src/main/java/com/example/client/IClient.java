package com.example.client;

import com.example.config.ConfigMap;

/**
 * @author xie.wei
 * @date created at 2021-12-04 19:50
 */
public interface IClient {

    boolean authenticate(String username, String password);

    boolean authorize(String username, String topic, String action);

    String type();

    ConfigMap getConfig();
}
