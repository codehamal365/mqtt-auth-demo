package com.example.constant;

import java.util.List;

/**
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

    String USER_PLACEHOLDER = "{userId}";
    String PVIN_PLACEHOLDER = "{pvin}";
}
