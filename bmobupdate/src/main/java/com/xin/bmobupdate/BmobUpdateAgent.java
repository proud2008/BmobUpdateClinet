package com.xin.bmobupdate;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xin.bmobupdate.bean.BmobAppBean;
import com.xin.bmobupdate.bean.PackagaeConfigBean;
import com.xin.bmobupdate.listener.BmobUpdateListener;
import com.xin.bmobupdate.utils.HttpBmob;
import com.xin.bmobupdate.utils.LogUtils;
import com.xin.bmobupdate.utils.PackageUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/27.
 */

class BmobUpdateAgent {
    /**
     * 不提示 直接下载 提示安装
     *
     * @param applicationContext
     */
    public static void silentUpdate(final Context applicationContext) {
        BmobUpdateListener bmobUpdateListener = new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int resultCode, Object o) {
                if (resultCode == 1) {
                    /*开启下载*/
                    BmobAppBean appBean = ((JSONObject) o).toJavaObject(BmobAppBean.class);
                    if (appBean.isValidDownUrl()) {
                        down(applicationContext, appBean);
                    }
                }
            }
        };
        checkUpdate(applicationContext, bmobUpdateListener);
    }

    protected static void down(Context context, BmobAppBean appBean) {
        log("down() called with: context = [" + context + "], appBean = [" + appBean + "]");
        Intent service = new Intent(context, BmobUpdateService.class);
        service.putExtra("data", appBean);
        context.startService(service);
    }

    private static void log(String s) {
        if (PackagaeConfigBean.isDebugable) {
            LogUtils.log("BmobUpdateAgent", s);
        }
    }

    private static void checkUpdate(Context context, final BmobUpdateListener listener) {
        if (listener == null) {
            return;
        }
        final PackagaeConfigBean config = PackageUtils.getBmobKey(context);
        if (config == null) {
            listener.onUpdateReturned(-2, null);
            return;
        }
        if (BmobUpdateService.isDowningApk) {
            listener.onUpdateReturned(2, null);
            return;
        }
        ObservableOnSubscribe<JSONObject> subscribe = new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> e) {
                JSONObject jsonObject = HttpBmob.findRow(config);
                log(jsonObject.toJSONString());
                e.onNext(jsonObject);
            }
        };
        Observable.create(subscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<JSONObject>() {


                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onUpdateReturned(-1, null);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        parseResult(jsonObject, listener);
                    }
                });


    }

    /**
     * 解析网络请求后的数据
     *
     * @param jsonObject
     * @param listener
     */
    private static void parseResult(JSONObject jsonObject, BmobUpdateListener listener) {
        if (jsonObject == null) {
            listener.onUpdateReturned(-5, jsonObject);
            return;
        }
        if (jsonObject.get("error") != null) {
            listener.onUpdateReturned(-6, jsonObject);
            return;
        }
        JSONArray results = jsonObject.getJSONArray("results");
        if (results == null || results.size() == 0) {
            listener.onUpdateReturned(0, jsonObject);
            return;
        }
        listener.onUpdateReturned(1, results.getJSONObject(0));
    }

    /**
     * 提示 是否下载 安装
     *
     * @param applicationContext
     * @param listener
     * @param ignoreVersion      忽略的版本号，若要不检查忽略 传0
     */
    public static void forceUpdate(final Context applicationContext, final BmobUpdateListener listener, final int ignoreVersion) {
        BmobUpdateListener bmobUpdateListener = new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int resultCode, Object o) {
                if (listener != null) {
                    listener.onUpdateReturned(resultCode, o);
                }
                if (resultCode == 1) {
                    BmobAppBean appBean = ((JSONObject) o).toJavaObject(BmobAppBean.class);
                    if (appBean.getVersion_i() > ignoreVersion) {
                        if (appBean.isValidDownUrl()) {
                            /*显示对话框*/
                            Intent intent = new Intent(applicationContext, BmobUpdateActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(BmobUpdateActivity.ExtraBmobAppBean, appBean);
                            applicationContext.startActivity(intent);
                        } else {
                            if (listener != null) {
                                listener.onUpdateReturned(0, o);
                            }
                        }
                    }
                }
            }
        };
        checkUpdate(applicationContext, bmobUpdateListener);
    }
}
