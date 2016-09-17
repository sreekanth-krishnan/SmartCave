package com.smartcave.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by USER on 25-05-2016.
 */
public class PrefUtil {
    private static PrefUtil ourInstance;
    private final SharedPreferences mPref;

    public static PrefUtil getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new PrefUtil(context);
        }
        return ourInstance;
    }

    private PrefUtil(Context context) {
        mPref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor edit(){
        return mPref.edit();
    }

    public void setConnectedDevice(String deviceId){
        edit().putString("deviceId", deviceId).commit();
    }

    public String getConnectedDevice(){
        return mPref.getString("deviceId", null);
    }

    public void setConnectedIp(String ip){
        edit().putString("ip", ip).commit();
    }

    public String getConnectedIp(){
        return mPref.getString("ip", null);
    }
}
