package com.wwxd.toolkit.activity

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.wwxd.base.BaseActivity
import com.wwxd.toolkit.R
import kotlinx.android.synthetic.main.activity_privacy.*

/**
 * user：LuHao
 * time：2020/12/9 11:26
 * describe：隐私政策
 */
class PrivacyActivity :BaseActivity(){
    override fun getContentView(): Int {
        return R.layout.activity_privacy
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        //设置编码
        wvPrivacy.getSettings().setDefaultTextEncodingName("utf-8")
        //支持js
        wvPrivacy.getSettings().setJavaScriptEnabled(true)
        //设置背景颜色 透明
        wvPrivacy.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.transparent
            )
        )
        //添加webview客户端
        wvPrivacy.setWebViewClient(MyWebViewClient())
        //添加浏览器客户端
        wvPrivacy.setWebChromeClient(MyWebChromeClient())
        //注销说明地址
        wvPrivacy.loadUrl("file:///android_asset/toolkit_privacy.html")
    }


    //客户端
    private class MyWebViewClient : WebViewClient() {
        //是否重写url
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return try {
                false
            } catch (e: Exception) {
                true
            }
        }

        //拦截每次请求的url
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
//            try {
//                String url = request.getUrl().toString();
//                return super.shouldInterceptRequest(view, request);
//            } catch (Exception e) {
//                return super.shouldInterceptRequest(view, request);
//            }
        }
        //关于https请求部导的问题，这里设置兼容模式
        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            if (error.primaryError != SslError.SSL_UNTRUSTED) {
                handler.proceed() //https验证不通过，继续加载
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            } else {
                handler.cancel() //https验证不通过，继续加载
            }
        }
    }

    //浏览器客户端
    private class MyWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
//            if (getView() != null) {
//                getView().showWebTitle(title);
//            }
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
//            if (newProgress == 100) {//加载完成
//                try {
            //加载完网页才能加载js
//                    view.loadUrl("javascript:hideDown()");
//                } catch (Exception e) {
            //js无效
//                }
//            }
        }
    }
}