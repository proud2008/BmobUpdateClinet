package com.xin.bmobupdate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.xin.bmobupdate.bean.BmobAppBean;
import com.xin.bmobupdate.bean.PackagaeConfigBean;
import com.xin.bmobupdate.listener.FileDownListener;
import com.xin.bmobupdate.utils.HttpBmob;
import com.xin.bmobupdate.utils.LogUtils;
import com.xin.bmobupdate.utils.PackageUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/12/27.
 */

public class BmobUpdateService extends Service {
    public static boolean isDowningApk = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        BmobAppBean appBean = intent.getParcelableExtra("data");
        if (appBean == null) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        checkDown(appBean);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void notifyShow(int progress) {
        PackagaeConfigBean configBean = PackageUtils.getBmobKey(this);
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "channel_id";
        String channelName = "channel_name";
        String tickerText = "新版本下载中..";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setTicker(tickerText)
                .setContentTitle(tickerText)
                .setProgress(100, progress, false)
                .setSmallIcon(configBean.getLauncherResId());
        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    /**
     * 下载结束 不管是否成功
     */
    private void onDownEnd() {
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }


    private void checkDown(BmobAppBean bmobAppBean) {
        boolean isDown = false;
        String url = null;
        if (bmobAppBean.getPath() != null) {
            url = bmobAppBean.getPath().getUrl();
        } else if (bmobAppBean.getAndroid_url() != null) {
            url = bmobAppBean.getAndroid_url();
        }
        if (url == null) {
            return;
        }
        File file = new File(getExternalCacheDir(), url.substring(url.lastIndexOf("/") + 1));
        if (file.exists()) {
            if (file.length() != bmobAppBean.getTarget_size()) {
                /*避免下载不全出现安装包出错的问题*/
                file.delete();
            } else {
                isDown = true;
                installApk(file);
            }
        }
        if (!isDown) {
            down(url);
        }
    }

    private void down(final String url) {
        PackagaeConfigBean bean = PackageUtils.getBmobKey(this);
        final File file = new File(getExternalCacheDir(), "v" + bean.getVersionCode() + ".apk");
        final DownListener downListener = new DownListener(new Handler());
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpBmob.downApk(url, file.getAbsolutePath(), downListener);
            }
        }).start();

    }

    /**
     * 安装APK
     */
    private void installApk(File apkFile) {
        Context context = this;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".comm.fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Uri data = Uri.fromFile(apkFile);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
        stopSelf();
    }

    private void log(String s) {
        LogUtils.log("BmobUpdateService", s);
    }

    /**
     * 是在子线程中进行的
     */
    private class DownListener implements FileDownListener {
        Handler handler;

        public DownListener(Handler handler) {
            this.handler = handler;
        }

        public void onStart() {
            log("onStart() called with: ]");
            isDowningApk = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyShow(0);
                }
            });

        }

        public void onProgress(final int progress) {
            log("onProgress() called with: progress = [" + progress + "]");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyShow(progress);
                }
            });
        }

        public void onError() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownEnd();
                }
            });

            log("onError() called with: progress = ");
            isDowningApk = false;
        }

        public void onFinish(final File file) {
            log("onFinish() called with: file = [" + file + "]");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownEnd();
                    installApk(file);
                }
            });
            isDowningApk = false;
        }

        public void onRemove() {
            log("onRemove() called with: progress = [" + "]");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownEnd();
                }
            });
            isDowningApk = false;
        }
    }
}
