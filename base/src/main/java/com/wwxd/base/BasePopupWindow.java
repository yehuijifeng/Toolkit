package com.wwxd.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.wwxd.base.R;


/**
 * user：LuHao
 * time：2019/11/10 17:20
 * describe：pow的基类
 */
public abstract class BasePopupWindow extends PopupWindow {
    private Context context;

    //是否全屏。true,全屏；false，不全屏
    public abstract boolean isFullWindow();

    //pow的宽高
    //WindowManager.LayoutParams.MATCH_PARENT//铺满
    //WindowManager.LayoutParams.WRAP_CONTENT//根据内容
    public abstract int getWidthAndHeight();

    //pow的进入/退出动画
    public int getAnimationStyle() {
        return R.style.pow_default_anim;
    }

    //pow的view
    public abstract int getRootView();

    //背景色
    public int getBackDrawable() {
        return R.color.transparent_black_80;
    }

    //是否可获得焦点
    public abstract boolean isFocusable();

    //是否允许view之外可以点击
    public abstract boolean isOutsideTouchable();

    //初始化view
    public abstract void initView(View view);

    public Context getContext() {
        return context;
    }

    @SuppressLint("WrongConstant")
    public BasePopupWindow(Context context) {
        try {
            this.context = context;
            //添加view
            View view = View.inflate(context, getRootView(), null);
            //设置宽高
            setWidth(getWidthAndHeight());
            setHeight(getWidthAndHeight());
            //进入/退出动画
            setAnimationStyle(getAnimationStyle());
            //是否启用剪裁。可以设置pow全屏
            setClippingEnabled(!isFullWindow());
            //测量view，以便于得到实际的长宽
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //设置背景色的透明度
            setBackgroundDrawable(context.getResources().getDrawable(getBackDrawable()));
            //是否可获得焦点
            setFocusable(isFocusable());
            //是否允许视图之外可点击
            setOutsideTouchable(isOutsideTouchable());
            setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            setContentView(view);
            initView(view);
            if (isFullWindow()) {
                getContentView().setPadding(getContentView().getPaddingLeft(), getContentView().getPaddingTop(), getContentView().getPaddingRight(), getContentView().getPaddingBottom() + AppConstant.INSTANCE.getApp().getNavigationBarHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 居中显示
     *
     * @param parent 外部传递进来的，pow依赖的view
     */
    public void showAtLocation(View parent) {
        hintSoftInput(parent);
        super.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 隐藏键盘
     *
     * @param view 外部传递进来的，pow依赖的view
     */
    public void hintSoftInput(View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     *
     * @param view 外部传递进来的，pow依赖的view
     */
    public void showSoftInput(View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

}
