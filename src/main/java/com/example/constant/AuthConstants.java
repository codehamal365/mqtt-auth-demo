package com.example.constant;

import org.springframework.http.CacheControl;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * auth constant class
 *
 * @author xie.wei
 * @date created at 2021-11-16 11:31
 */
public interface AuthConstants {

    String AUTH_HEADER_KEY = "vernemq-hook";
    String SLASH = "/";
    String PLUS = "+";
    String POUND_KEY = "#";


    String PUB_ACTION = "pub";
    String SUB_ACTION = "sub";
    List<String> ACTION_LIST = List.of(PUB_ACTION, SUB_ACTION);


    String USERID_PLACEHOLDER = "userId";
    String PVIN_PLACEHOLDER = "pvin";
    String PDEVICEID_PLACEHOLDER = "pdeviceId";
    String SERVICENAME_PLACEHOLDER = "serviceName";

    CacheControl CACHE_CONTROL = CacheControl.maxAge(30, TimeUnit.SECONDS);

}
