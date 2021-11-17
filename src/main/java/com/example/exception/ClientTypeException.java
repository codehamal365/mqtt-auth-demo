package com.example.exception;

import lombok.ToString;

/**
 * mqtt client exception class
 *
 * @author xie.wei
 * @date created at 2021-11-16 14:11
 */
@ToString
public class ClientTypeException extends RuntimeException {

    public ClientTypeException(String message) {
        super(message);
    }
}
