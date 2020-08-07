package com.dj.plugin.plugin1;

import android.util.Log;

public class SignUtils {
    private static final String TAG = SignUtils.class.getName();
    static {
        System.loadLibrary("native-lib");
    }
    public static native String getSecret();

    public static void sign(){
        Log.e(TAG,">>>>>>>>>>>>>>>>>>>>>>>>getSecret："+getSecret());
    }
}
