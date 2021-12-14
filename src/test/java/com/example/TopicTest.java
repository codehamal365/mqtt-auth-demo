package com.example;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;

import static com.example.constant.AuthConstants.PLUS;
import static com.example.constant.AuthConstants.POUND_KEY;
import static org.junit.Assert.assertTrue;

/**
 * topic tests
 *
 * @author xie.wei
 * @date created at 2021-11-17 15:02
 */
public class TopicTest {

    /**
     * /users/{userId}/vehicles/{pvin}/#
     * /users/{userId}/customers/#
     * /users/{userId}/grantPermission/#
     * /devices/{pdeviceId}/getDeviceVehicle/#
     * /devices/{pdeviceId}/postNetworkStatus/#
     * /devices/{pdeviceId}/postDeviceLog/#
     * /vehicles/{pvin}/devices/{pdeviceId}/info
     * /services/{serviceName}/devices/getDeviceLogs/#
     * /users/+/{serviceName}/
     * /services/{serviceName}/vehicles/#
     * /vehicle-user/{userId}/{pvin}/addUserVehicle/#
     * /vehicle-user/{userId}/{pvin}/removeUserVehicle/#
     * /vehicle-user/{userId}/{pvin}/updateUserVehicle/#
     */
    @Test
    public void test() {
        // https://www.cnblogs.com/syp172654682/p/9257282.html
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String reg = "+/services/+/test/{serviceName}/+/vehicles/#";
        String reg1 = "*/services/*/test/{serviceName}/*/vehicles/**";
//        final String replace = reg.replace("+", "*")
//                //.replace("+/","*/")
//                .replace("#","**");
//        System.out.println(replace);
//        System.out.println(reg.concat("/#"));
        String originTopic = "+/services/afa/test/{serviceName}/test/vehicles/afaf/aafa/afaf";
        boolean matchAll1 = pathMatcher.match(reg1, originTopic);
        boolean matchAll = pathMatcher.match(reg
                .replace(PLUS, "*")
                .replace(POUND_KEY, "**"), originTopic);
        boolean matchPoundKey = pathMatcher.match(reg
                .replace(PLUS, "*"), originTopic);
        boolean matchPlus = pathMatcher.match(reg
                .replace(POUND_KEY, "**"), originTopic);
        boolean result = matchAll || matchPoundKey || matchPlus;


        assertTrue(matchAll1);


    }

    /**
     * 订阅主题
     * 1.如果含有#号，且不是等于# 就只能以/#结尾
     * 2.
     */
    @Test
    public void testSubscribePound() {
        /**
         * /#
         * test/#
         * #
         */
        String pound1 = "#";
        String pound2 = "/#";
        String pound3 = "test/#";
//        System.out.println(pound1.endsWith("/#"));
//        System.out.println(pound2.endsWith("/#"));
//        System.out.println(pound3.endsWith("/#"));


        /**
         * test/++/topic/ 错误
         * test+/topic/ 错误
         * test/+/+/topic/ 正确
         * /+testtopic/ 错误
         * test+topic/ 错误
         */
//
//        AntPathMatcher pathMatcher = new AntPathMatcher();
//        System.out.println(pathMatcher.isPattern("aaf"));
//        System.out.println(pathMatcher.isPattern("/aaf/"));
//        System.out.println(pathMatcher.isPattern("/aaf/{}"));
//        System.out.println(pathMatcher.isPattern("/aaf/{a}"));
//
        String reg = "+/services/+/test/{serviceName}/+/vehicles/#";
        System.out.println(reg.replace("+", "*").replace("#", "**"));
    }
}
