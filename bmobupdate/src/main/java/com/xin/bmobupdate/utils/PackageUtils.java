package com.xin.bmobupdate.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.xin.bmobupdate.bean.PackagaeConfigBean;

/**
 * Created by Administrator on 2016/7/18.
 * 读取应用包信息
 */
public class PackageUtils {
    public static PackagaeConfigBean getBmobKey(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = packageInfo.applicationInfo.metaData;
            if (metaData != null) {
                String bmobKey = metaData.getString("BMOB_KEY");
                String bmobRestFulKey = metaData.getString("BMOB_RestFul_KEY");
                String bmobChannel = metaData.getString("BMOB_CHANNEL");
                if (bmobChannel == null) {
                    bmobChannel = packageInfo.packageName;
                }
                if (!TextUtils.isEmpty(bmobKey) && !TextUtils.isEmpty(bmobRestFulKey) && !TextUtils.isEmpty(bmobChannel)) {
                    PackagaeConfigBean configBean = new PackagaeConfigBean(bmobKey, bmobRestFulKey, bmobChannel);
                    configBean.setVersionCode(packageInfo.versionCode);
                    configBean.setLauncherResId(packageInfo.applicationInfo.icon);
                    PackagaeConfigBean.isDebugable = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0;
                    return configBean;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
