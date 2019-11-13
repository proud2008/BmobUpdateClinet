package com.xin.bmobupdate.utils;


import com.alibaba.fastjson.JSONObject;
import com.xin.bmobupdate.BuildConfig;
import com.xin.bmobupdate.bean.PackagaeConfigBean;
import com.xin.bmobupdate.listener.FileDownListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Administrator on 2017/12/27.
 */

public class HttpBmob {
    private static final String TableName = "AppVersion";

    /**
     * 请求两次，避免异常
     * @param bmobKey
     * @return
     */
    public static JSONObject findRow(PackagaeConfigBean bmobKey) {
        JSONObject jsonObject = findRow2(bmobKey);
        if (jsonObject.get("error") != null) {
            jsonObject = findRow2(bmobKey);
        }
        return jsonObject;
    }

    private static JSONObject findRow2(PackagaeConfigBean bmobKey) {
        JSONObject jsonObject = new JSONObject();
        JSONObject version = new JSONObject();
        version.put("$gt", BuildConfig.VERSION_CODE);
        jsonObject.put("channel", bmobKey.getBmobChannel());
        jsonObject.put("version_i", version);
        String url = "https://api.bmob.cn/1/classes/" + TableName + "?where=" + jsonObject.toJSONString() + "&order=-version_i&limit=1";
        try {
            OkHttpClient okHttpClient = HttpUtils.getOkHttpClient();
            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Bmob-REST-API-Key", bmobKey.getBmobRestFulKey())
                    .addHeader("X-Bmob-Application-Id", bmobKey.getBmobKey())
                    .get()
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String bodyStr = response.body().string();
            JSONObject resultJson = JSONObject.parseObject(bodyStr);
            return resultJson;
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObjectErr = new JSONObject();
            jsonObject.put("desc", e.getLocalizedMessage());
            jsonObject.put("error", -1);
            return jsonObjectErr;
        }
    }

    /**
     * 下载文件
     *
     * @param apkUrl
     * @param targtPath 下载文件的保存地址详细
     * @return 本地的文件地址
     */
    public static String downApk(String apkUrl, String targtPath, FileDownListener downListener) {
        if (apkUrl == null || targtPath == null) {
            return null;
        }
        if (downListener != null) {
            downListener.onStart();
        }
        Request request = new Request.Builder().url(apkUrl).build();
        final Call call = HttpUtils.getOkHttpClient().newCall(request);
        try {
            Response response = call.execute();
            /*本地保存的文件*/
            File file = new File(targtPath);
            if (file.exists()) {
                file.delete();
            }

            InputStream is = null;
            byte[] buf = new byte[102400];/*100k*/
            int len = 0;/*本次读取的长度*/
            int lenRead = 0; /*总共读取的长度*/
            FileOutputStream fos = null;
            try {
                long total = response.body().contentLength();
                log("downApk ... file total " + total);
                is = response.body().byteStream();
                fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    lenRead += len;
                    if (downListener != null) {
                        downListener.onProgress((int) (lenRead * 100f / total));
                    }
                }
                fos.flush();
                is.close();
                fos.close();
                is = null;
                fos = null;
                if (downListener != null) {
                    downListener.onFinish(file);
                }
                return file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                downListener.onError();
            } finally {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void log(String s) {
        if (PackagaeConfigBean.isDebugable) {
            LogUtils.log("HttpBmob", s);
        }
    }
}
