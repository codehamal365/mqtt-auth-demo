package com.example.controller;

import com.example.dto.AuthOnPublishDTO;
import com.example.dto.AuthOnRegisterDTO;
import com.example.dto.AuthOnSubscribeDTO;
import com.example.dto.ResponseDTO;
import com.example.service.WebhookService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * webhooks controller
 *
 * @author xie.wei
 * @date created at 2021-11-16 10:53
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/webhooks/auth")
@ResponseStatus(HttpStatus.OK)
public class WebhookAuthController implements ApplicationContextAware {

    private final WebhookService webhookService;
    private ApplicationContext applicationContext;

    @PostMapping("register")
    public ResponseDTO authOnRegister(@RequestBody @Validated AuthOnRegisterDTO dto) {
        return webhookService.authenticateRegister(dto.getUsername(), dto.getPassword()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

    @PostMapping("subscribe")
    public ResponseDTO authOnSubscribe(@RequestBody @Validated AuthOnSubscribeDTO dto) {
        return webhookService.authorizeSubscribe(dto.getUsername(), dto.getTopics()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

    @PostMapping("publish")
    public ResponseDTO authOnPublish(@RequestBody @Validated AuthOnPublishDTO dto) {
        return webhookService.authorizePublish(dto.getUsername(), dto.getTopic()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 动态注入bean
    @GetMapping("register")
    public Object registerBean() {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        if (autowireCapableBeanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CustomerBean.class);
            builder.addConstructorArgValue("hello world");
            listableBeanFactory.registerBeanDefinition("customerBean", builder.getRawBeanDefinition());
        }
        return applicationContext.getBean("customerBean");

    }


    @Data
    @AllArgsConstructor
    static class CustomerBean {
        private String name;
    }
}
