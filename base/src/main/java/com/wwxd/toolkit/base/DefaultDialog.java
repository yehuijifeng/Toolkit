package com.wwxd.toolkit.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * user：LuHao
 * time：2019/10/28 10:00
 * describe：默认的提示框
 */
public class DefaultDialog extends AlertDialog {

   private EditText textTitle;
   private EditText textContent;
   private TextView btnCancel;
   private TextView btnOk;
   private LinearLayout lyBtnTwo;
   private TextView btnOkTwo;

    private View view;
    private boolean isBackDismiss, isClickDismiss = true;//是否按返回键关闭；true,关闭；false，不关闭；
    private Builder builder;//建造者

    public DefaultDialog(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setOnKeyListener(new OnKeyListener());
        view = View.inflate(context, R.layout.dialog_default, null);
    }

    public synchronized Builder getBuilder() {
        if (builder == null) {
            builder = new Builder();
        }
        defaultSetting(builder);
        return builder;
    }

    //每次调用builder的时候需要初始化
    private void defaultSetting(Builder builder) {
        builder
                .isBackDismiss(true)
                .isNoCancle(false)
                .isShowTiltle(true)
                .setTitle("")
                .setContent("")
                .setOkText("")
                .setCancelText("")
                .setCancelClick(null)
                .setOkClick(null)
                .setCancelTextColor(R.color.color_242424)
                .setOkTextColor(R.color.white)
                .setCancelBackground(R.drawable.bg_dialog_default_clean_btn)
                .setOkBackground(R.drawable.bg_dialog_default_ok_btn);
    }

    //清除内存
    public void clear() {
        dismiss();
    }

    public class Builder {

        //是否点击返回键关闭。true,可以点击返回键关闭
        public Builder isBackDismiss(boolean bl) {
            isBackDismiss = bl;
            return this;
        }

        //是否点击之后弹窗消失
        public Builder isClickDismiss(boolean bl) {
            isClickDismiss = bl;
            return this;
        }

        /**
         * 显示一个确定按钮还是取消和确定两个按钮
         *
         * @param bl true,一个确定按钮；false，取消和确定按钮
         */
        public Builder isNoCancle(boolean bl) {
            if (bl) {
                lyBtnTwo.setVisibility(View.INVISIBLE);
                btnOkTwo.setVisibility(View.VISIBLE);
            } else {
                lyBtnTwo.setVisibility(View.VISIBLE);
                btnOkTwo.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        /**
         * 是否显示标题
         *
         * @param bl true,显示；false，不显示
         */
        public Builder isShowTiltle(boolean bl) {
            textTitle.setVisibility(bl ? View.VISIBLE : View.GONE);
            return this;
        }

        /**
         * 设置标题
         *
         * @param title 标题内容
         */
        public Builder setTitle(String title) {
            if (TextUtils.isEmpty(title))
                title = "";
            textTitle.setText(title);
            return this;
        }

        /**
         * 设置内容
         *
         * @param content 内容
         */
        public Builder setContent(String content) {
            if (TextUtils.isEmpty(content))
                content = "";
            textContent.setText(content);
            return this;
        }

        /**
         * 显示html内容
         *
         * @param htmlContent 带有html标签的内容
         */
        public Builder setHtmlContent(String htmlContent) {
            if (TextUtils.isEmpty(htmlContent))
                htmlContent = "";
            textContent.setText(Html.fromHtml(htmlContent));
            return this;
        }

        /**
         * 设置确定按钮文字
         *
         * @param ok 文字
         */
        public Builder setOkText(String ok) {
            if (TextUtils.isEmpty(ok))
                ok = "";
            btnOkTwo.setText(ok);
            btnOk.setText(ok);
            return this;
        }

        /**
         * 设置取消按钮文字
         *
         * @param cancle 文字
         */
        public Builder setCancelText(String cancle) {
            if (TextUtils.isEmpty(cancle))
                cancle = "";
            btnCancel.setText(cancle);
            return this;
        }

        /**
         * 设置确定按钮的文字颜色
         *
         * @param color 色值
         */
        public Builder setOkTextColor(int color) {
            if (color == 0) return this;
            btnOkTwo.setTextColor(ContextCompat.getColor(getContext(), color));
            btnOk.setTextColor(ContextCompat.getColor(getContext(), color));
            return this;
        }

        /**
         * 设置取消按钮的文字颜色
         *
         * @param color 色值
         */
        public Builder setCancelTextColor(int color) {
            if (color == 0) return this;
            btnCancel.setTextColor(ContextCompat.getColor(getContext(), color));
            return this;
        }

        /**
         * 设置确定按钮的背景颜色
         *
         * @param res 资源
         */
        public Builder setOkBackground(int res) {
            if (res == 0) return this;
            btnOkTwo.setBackgroundResource(res);
            btnOk.setBackgroundResource(res);
            return this;
        }

        /**
         * 设置取消按钮的背景颜色
         *
         * @param res 色值
         */
        public Builder setCancelBackground(int res) {
            if (res == 0) return this;
            btnCancel.setBackgroundResource(res);
            return this;
        }

        /**
         * 确定的点击事件
         *
         * @param onClickListener 点击事件
         */
        public Builder setOkClick(IDefaultDialogClickListener onClickListener) {
            btnOkTwo.setOnClickListener(new OnDialogClick(onClickListener));
            btnOk.setOnClickListener(new OnDialogClick(onClickListener));
            return this;
        }

        /**
         * 取消的点击事件
         *
         * @param onClickListener 点击事件
         */
        public Builder setCancelClick(IDefaultDialogClickListener onClickListener) {
            btnCancel.setOnClickListener(new OnDialogClick(onClickListener));
            return this;
        }

        //只能是dialog
        public void show() {
            showView();
        }

        //确定/取消 按钮点击事件
        private class OnDialogClick extends NoDoubleClickListener {
            private IDefaultDialogClickListener iDefaultDialogClickListener;

            private OnDialogClick(IDefaultDialogClickListener iDefaultDialogClickListener) {
                this.iDefaultDialogClickListener = iDefaultDialogClickListener;
            }

            @Override
            public void onNoDoubleClick(View v) {
                if (iDefaultDialogClickListener != null) {
                    iDefaultDialogClickListener.onClick(v);
                }
                if (isClickDismiss)
                    dismiss();
            }
        }
    }

    //监听返回按键
    private class OnKeyListener implements DialogInterface.OnKeyListener {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && isBackDismiss) {  //表示按返回键时的操作
                    dismiss();
                    return true;
                }
                return true;//已处理
            }
            return false;
        }
    }

    //显示dialog
    private void showView() {
        try {
            setCanceledOnTouchOutside(false);
            show();
            setContentView(view);
            textContent.post(new Runnable() {
                @Override
                public void run() {
                    if (textContent.getLineCount() == 1) {
                        textContent.setGravity(Gravity.CENTER);
                    }
                }
            });
            if (getWindow() != null)
                getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
