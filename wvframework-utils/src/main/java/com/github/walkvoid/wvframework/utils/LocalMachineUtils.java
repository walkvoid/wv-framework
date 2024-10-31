package com.github.walkvoid.wvframework.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author jiangjunqing
 * @date 2024/6/6
 * @description: 本机工具类，可以获取mac地址，CPU核数，内存大小等信息
 * @version:
 */
public class LocalMachineUtils {



    public static String getMac() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        networkInterfaces.hasMoreElements();
        NetworkInterface networkInterface = networkInterfaces.nextElement();
        byte[] hardwareAddress = networkInterface.getHardwareAddress();


        return "";
    }

}
