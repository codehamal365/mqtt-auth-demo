package com.example;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;

/**
 * @author xie.wei
 * @date created at 2021-11-17 15:02
 */
public class ParseTest {


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
    /**
     * 不含/
     * 头含/ 尾不含/
     * 头含/ 尾含/
     */
    static String s = "";

    @Test
    public void test() {
        String test = "";
        AntPathMatcher pathMatcher = new AntPathMatcher("/");
        final boolean test1 = pathMatcher.match("/users/{userId}/**/vehicles/{pvin}/**", "/users/aa/ccc/vehicles/bb");
        System.out.println(test1);

        Assert.assertEquals(1, 1);
    }

    public static boolean auth() {
        String topic = "/users/{userId}/vehicles/{pvin}/#";


        return true;
    }
}
