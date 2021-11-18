package com.example.exception;

import lombok.ToString;

/**
 * config map yaml exception
 *
 * @author xie.wei
 * @date created at 2021-11-19 00:36
 */
@ToString
public class ConfigMapException extends RuntimeException {
    public ConfigMapException(String message) {
        super(message);
    }
}
