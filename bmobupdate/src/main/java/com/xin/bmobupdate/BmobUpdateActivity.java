package com.xin.bmobupdate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.xin.bmobupdate.bean.BmobAppBean;
import com.xin.bmobupdate.utils.SpUtils;


/**
 * Created by Administrator on 2017/12/27.
 */

public class BmobUpdateActivity extends Activity {
    public static final String ExtraBmobAppBean = "UpdateActivity_ExtraBmobAppBean";
    final int REQUEST_CODE_FOR_MANAGE_UNKNOWN_APP = 9886;
    TextView bmobUpdateContent;
    Button bmobUpdateIdCancel;
    BmobAppBean appBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmob_update_activity);
        initView();
        initViewPermission();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*强制升级时不可返回*/
            if (appBean.isIsforce()) {
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        bmobUpdateContent = findViewById(R.id.bmob_update_content);
        bmobUpdateIdCancel = findViewById(R.id.bmob_update_id_cancel);
        bmobUpdateIdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClick();
            }
        });
        findViewById(R.id.bmob_update_id_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOkClick();
            }
        });
        appBean = getIntent().getParcelableExtra(ExtraBmobAppBean);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("发现新版本\n");
        stringBuilder.append("版本v" + appBean.getVersion() + "(" + formatFileSize(appBean.getTarget_size()) + ")");
        bmobUpdateContent.setText(stringBuilder.toString());
        /*强制升级时无取消*/
        if (appBean.isIsforce()) {
            bmobUpdateIdCancel.setVisibility(View.GONE);
        }

    }

    private void onCancelClick() {
        SpUtils.lastIgnoreVersion(BmobUpdateActivity.this, appBean.getVersion_i());
        finish();
    }

    private void onOkClick() {
        BmobUpdateAgent.down(BmobUpdateActivity.this, appBean);
        finish();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 检查是否有安装权限，未获得权限则弹框提示
     */
    private void initViewPermission() {
        /*先获取是否有安装未知来源应用的权限*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                /*手动更新时，提示打开权限，自动更新时不提示*/
                new AlertDialog.Builder(this)
                        .setMessage("应用更新需要打开未知来源权限，请去设置中开启权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BmobUpdateActivity.this.startInstallPermissionSettingActivity();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, REQUEST_CODE_FOR_MANAGE_UNKNOWN_APP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_MANAGE_UNKNOWN_APP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                if (haveInstallPermission) {
                    onOkClick();
                }
            }
        }
    }


}
