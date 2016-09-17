package com.smartcave.app.util;

/**
 * Created by USER on 06-09-2016.
 */

public class CalcUtil {
    public static double calculateUnitConsumed(long deviceRating, long operatedTime){
        return (deviceRating * operatedTime)/(1000.0d * 3600.0d);
    }

    public static double getExpenseForUnit(double unit){
        return unit * 3.7d;
    }
}
