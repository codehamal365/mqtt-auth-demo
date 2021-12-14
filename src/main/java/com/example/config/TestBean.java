package com.example.config;

import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * test for spring bean init process
 *
 * @author xie.wei
 * @date created at 2021-12-14 10:01
 */
@Component
@Data
public class TestBean implements InitializingBean {

    private String name;


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("testBean afterPropertiesSet");
    }

    @PostConstruct
    private void postConstruct(){
        System.out.println("testBean postConstruct");
    }
}
