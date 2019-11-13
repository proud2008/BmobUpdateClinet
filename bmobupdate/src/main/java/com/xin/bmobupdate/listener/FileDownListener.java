package com.xin.bmobupdate.listener;

import java.io.File;

public interface FileDownListener {

    void onStart();

    public void onProgress(int progress);

    public void onError();

    public void onFinish(File file);

    public void onRemove();
}