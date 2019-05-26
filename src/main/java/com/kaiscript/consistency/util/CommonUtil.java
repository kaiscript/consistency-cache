package com.kaiscript.consistency.util;

/**
 * Created by kaiscript on 2019/5/25.
 */
public class CommonUtil {

    public static void sleep(int millSec) {
        try {
            Thread.sleep(millSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
