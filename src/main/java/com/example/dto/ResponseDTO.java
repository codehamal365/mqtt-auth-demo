package com.example.dto;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
       // res.put("reason_code", 128);
        // 3.0 可以 5.0 不可以 registration
        res.put(KEY, Map.of("error", "not_allowed"));
        // 都可以
        //res.put(KEY, "error");
        return res;
    }

    public static ResponseDTO error(String error) {
        var res = errorDefault();
        res.put("error_msg", error);
        return res;
    }


    public static void main(String[] args) {
        String reg = "/test/test/#";
        final List<String> strings = Splitter.on("#").splitToList(reg);
        System.out.println(strings.size());
        strings.stream().forEach(System.out::println);
    }
}
