package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author xie.wei
 * @date created at 2021-11-16 10:51
 */
@SpringBootApplication
public class MqttAuthApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MqttAuthApplication.class, args);
    }
}
