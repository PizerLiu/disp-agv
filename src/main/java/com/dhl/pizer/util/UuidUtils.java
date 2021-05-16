package com.dhl.pizer.util;

import java.util.UUID;

public class UuidUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(8);
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }

}
