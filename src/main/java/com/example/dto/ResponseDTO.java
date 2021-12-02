package com.example.dto;

import java.util.HashMap;

/**
 * response dto to mqtt client
 * for mqtt5 response if authorization failed should return reason code
 * <a>https://www.emqx.com/en/blog/mqtt5-new-features-reason-code-and-ack#suback-packet</a>
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
        res.put("reason_code", 128);
        res.put(KEY, "error");
        return res;
    }

    public static ResponseDTO error(String error) {
        var res = errorDefault();
        res.put("error_msg", error);
        return res;
    }
}
