package com.example;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;

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
    static String s = "";

    @Test
    public void test() {
        // https://www.cnblogs.com/syp172654682/p/9257282.html
        String test = "";
        AntPathMatcher pathMatcher = new AntPathMatcher("/");
        String cc= "services/+/{serviceName}/vehicles/#";
        System.out.println(pathMatcher.match(cc,"services/aaa/{serviceName}/vehicles/#"));
        Assert.assertEquals(1, 1);
    }

}
