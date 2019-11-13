package com.xin.bmobupdate.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by 鑫 Administrator on 2017/5/19.
 */

public class SpUtils {
    public static void lastUpdata(Context context, long lastTime) {
        getSp(context).edit().putLong("time", lastTime).commit();
    }

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences("updata", Context.MODE_PRIVATE);
    }

    public static long lastUpdata(Context context) {
        return getSp(context).getLong("time", 0);
    }

    /**
     * @param context 小于该版本的静默更新 不再提示
     * @return
     */
    public static int lastIgnoreVersion(Context context) {
        return getSp(context).getInt("IgnoreVersion", 0);
    }

    public static void lastIgnoreVersion(Context context, int ignoreVersion) {
        getSp(context).edit().putInt("IgnoreVersion", ignoreVersion).commit();
    }

    public static void newVersion(Context context, int newVersion) {
        getSp(context).edit().putInt("newVersion", newVersion).commit();
    }

    public static int newVersion(Context context) {
        return getSp(context).getInt("ignoreNum", 0);
    }

    public static void newVersionNum(Context context, int newVersionNum) {
        getSp(context).edit().putInt("newVersionNum", newVersionNum).commit();
    }

    public static int newVersionNum(Context context) {
        return getSp(context).getInt("newVersionNum", 1);
    }
}
