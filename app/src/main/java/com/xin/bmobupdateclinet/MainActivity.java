package com.xin.bmobupdateclinet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.xin.bmobupdate.BmobAppUpdateUtils;
import com.xin.bmobupdate.listener.BmobUpdateListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doCheck(View view) {
        BmobAppUpdateUtils.update(this, new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int resultCode, Object o) {
                System.out.println("onUpdateReturned() called with: resultCode = [" + resultCode + "], o = [" + o + "]");
            }
        });
    }
}
