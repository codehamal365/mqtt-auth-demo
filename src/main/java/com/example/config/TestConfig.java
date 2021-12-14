package com.example.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * test bean init method
 *
 * @author xie.wei
 * @date created at 2021-12-14 10:26
 */
@Configuration
public class TestConfig {


    @Bean(initMethod = "init")
    public InitBean initBean() {
        return new InitBean();
    }


    static class InitBean implements InitializingBean, BeanNameAware, ApplicationContextAware {

        public InitBean() {
            System.out.println("InitBean construct");
        }

        private void init() {
            System.out.println("init bean init-method");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("init bean afterPropertiesSet");
        }

        @Override
        public void setBeanName(String s) {
            System.out.println("init bean setBeanName: " + s);
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            System.out.println("init bean setApplicationContext");
        }
    }
}
