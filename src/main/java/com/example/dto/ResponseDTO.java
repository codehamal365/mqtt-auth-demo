package com.example.dto;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

/**
 * response dto to mqtt client
 *
 * @author xie.wei
 * @date created at 2021-11-16 16:36
 */
public class ResponseDTO extends HashMap<String, Object> {
    static final String KEY = "result";

    public static ResponseDTO ok() {
        var res = new ResponseDTO();
        res.put(KEY, "ok");
        return res;
    }

    public static ResponseDTO errorDefault() {
        var res = new ResponseDTO();
        res.put(KEY, ImmutableMap.builder().put("error", "not_allowed").build());
        return res;
    }

    public static ResponseDTO error(String error) {
        var res = new ResponseDTO();
        res.put(KEY, ImmutableMap.builder().put("error", error).build());
        return res;
    }
}
