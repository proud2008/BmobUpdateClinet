package com.xin.bmobupdate.bean;

/**
 * 应用中配置的信息
 */
public class PackagaeConfigBean {
    String bmobKey;
    String bmobRestFulKey;
    String bmobChannel;
    int versionCode;
    int launcherResId;
    public static boolean isDebugable;

    public PackagaeConfigBean(String bmobKey, String bmobRestFulKey, String bmobChannel) {
        this.bmobKey = bmobKey;
        this.bmobRestFulKey = bmobRestFulKey;
        this.bmobChannel = bmobChannel;
    }


    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getLauncherResId() {
        return launcherResId;
    }

    public void setLauncherResId(int launcherResId) {
        this.launcherResId = launcherResId;
    }

    public String getBmobKey() {
        return bmobKey;
    }

    public String getBmobChannel() {
        return bmobChannel;
    }

    public String getBmobRestFulKey() {
        return bmobRestFulKey;
    }

}
