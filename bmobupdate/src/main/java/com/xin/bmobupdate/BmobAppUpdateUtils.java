package com.xin.bmobupdate;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.xin.bmobupdate.bean.BmobAppBean;
import com.xin.bmobupdate.bean.PackagaeConfigBean;
import com.xin.bmobupdate.listener.BmobUpdateListener;
import com.xin.bmobupdate.utils.NetUtils;
import com.xin.bmobupdate.utils.PackageUtils;
import com.xin.bmobupdate.utils.SpUtils;

/**
 *
 */
public class BmobAppUpdateUtils {

    /**
     * 检查配置中是否设置了bmonkey
     *
     * @param b 是否一天只能检查一次
     */
    private static boolean initBmob(Context context, BmobUpdateListener listener, boolean b) {
        PackagaeConfigBean bmobKey = PackageUtils.getBmobKey(context);
        if (bmobKey == null) {
            if (listener != null) {
                listener.onUpdateReturned(-2, null);
            }
            return false;
        }
        if (b) {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis - SpUtils.lastUpdata(context) < 24 * 60 * 60 * 1000) {
                if (listener != null) {
                    listener.onUpdateReturned(-2, null);
                }
                return false;
            }
            SpUtils.lastUpdata(context, timeMillis);
        }

        return true;
    }

    /**
     * 会次执行都会检查 提示有新版本 下载后直接安装
     *
     * @param context
     * @param listener
     */

    public static void update(final Context context, final BmobUpdateListener listener) {
        final Context applicationContext = context.getApplicationContext();
        if (initBmob(context, listener, false)) {
            BmobUpdateAgent.forceUpdate(applicationContext, listener, 0);
        }
    }

    /**
     * 每天只会检查一次 不提示有新版本 下载后直接安装
     *
     * @param context
     */
    public static void update(final Context context) {
        if (!NetUtils.isWifiConnected(context)) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        if (initBmob(applicationContext, null, true)) {
            BmobUpdateAgent.silentUpdate(applicationContext);
        }
    }

    /**
     * 每天只会检查一次 下载有提示 下载后安装 取消后此版本不再提示
     * 不要放在application中，弹窗会在首页加载前显示出来
     *
     * @param context
     */
    public static void update2(final Context context) {
        if (!NetUtils.isWifiConnected(context)) {
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        if (initBmob(applicationContext, null, true)) {

            BmobUpdateListener listener = new BmobUpdateListener() {
                @Override
                public void onUpdateReturned(int resultCode, Object o) {
                    if (resultCode == 1) {
                        BmobAppBean appBean = ((JSONObject) o).toJavaObject(BmobAppBean.class);
                        PackagaeConfigBean bmobKey = PackageUtils.getBmobKey(context);
                        if (appBean.getVersion_i() != bmobKey.getVersionCode()) {
                            SpUtils.newVersion(applicationContext, appBean.getVersion_i());
                            SpUtils.newVersionNum(applicationContext, 1);
                        } else {
                            int newVersionNum = SpUtils.newVersionNum(applicationContext);
                            newVersionNum++;
                            SpUtils.newVersionNum(applicationContext, newVersionNum);
                        }
                    }
                }
            };
            int ignoreVersion = SpUtils.newVersionNum(applicationContext) == 2 ? SpUtils.lastIgnoreVersion(applicationContext) : 0;
            BmobUpdateAgent.forceUpdate(applicationContext, listener, ignoreVersion);
        }
    }


}
