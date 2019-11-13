package com.xin.bmobupdate.listener;

/**
 * Created by Administrator on 2017/12/27.
 */

public interface BmobUpdateListener {
    /**
     * @param resultCode <0 出错的 0无新版本 1是有新版本 2 下载中
     * @param o jsonobject对象 error不为0 desc为出错的信息
     */
    void onUpdateReturned(int resultCode, Object o);
}
