package com.example.exception;

import lombok.ToString;

/**
 * @author xie.wei
 * @date created at 2021-11-27 18:36
 */
@ToString
public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}
