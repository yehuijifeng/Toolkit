package com.wwxd.base;

import android.view.View;


/**
 * user：LuHao
 * time：2019/10/27 17:21
 * describe：防止快速点击的点击事件
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
    private long lastClickTime = 0;

    public abstract void onNoDoubleClick(View v);

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > 500) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }
}
