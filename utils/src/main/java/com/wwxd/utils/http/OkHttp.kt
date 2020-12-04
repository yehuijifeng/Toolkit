package com.wwxd.utils.http

import android.text.TextUtils
import com.wwxd.utils.rxandroid.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * user：LuHao
 * time：2019/11/18 13:30
 * describe：okhttp请求
 */
object OkHttp {
    private var client //okhttp的客户端对象
            : OkHttpClient? = null
    private var sslContext //正向证书
            : SSLContext? = null
    private var x509TrustManager //反向证书
            : X509TrustManager? = null
    private val timeout = 15L

    //获取HostnameVerifier
    private var hostnameVerifier //主机名验证
            : HostnameVerifier? = null
        get() {
            if (field == null) field = HostnameVerifier { hostname, session ->
                //s, sslSession -> s.contains(ip) || s.contains("s3.us.qyqxapp.com")
                true
            }
            return field
        }

    //okhttp的client对象
    private val okHttpClient: OkHttpClient?
        get() {
            if (client == null) {
                try {
                    client = OkHttpClient.Builder()
                        .connectTimeout(timeout, TimeUnit.SECONDS)
                        .readTimeout(timeout, TimeUnit.SECONDS)
                        .sslSocketFactory(sSLSocketFactory, trustManager)
                        .hostnameVerifier(hostnameVerifier!!)
                        .build()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return client
        }

    //获取这个SSLSocketFactory
    private val sSLSocketFactory: SSLSocketFactory
        get() = try {
            if (sslContext == null) {
                sslContext = SSLContext.getInstance("SSL")
                sslContext!!.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
            }
            sslContext!!.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    //获取TrustManager
    private val trustManager: X509TrustManager
        get() {
            if (x509TrustManager == null)
                x509TrustManager = object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            return x509TrustManager!!
        }

    //设置超时时间
    fun setTimeOut(s: Int) {
        if (okHttpClient != null)
            client =
                okHttpClient!!.newBuilder()
                    .connectTimeout(s.toLong(), TimeUnit.SECONDS)
                    .readTimeout(s.toLong(), TimeUnit.SECONDS)
                    .build()
    }

    //下载文件
    @Synchronized
    fun download(url: String, saveFilePath: String, iHttpDownload: IHttpDownload?) {
        if (TextUtils.isEmpty(url)) return
        try {
            //初始化一次请求
            val requestBuilder = Request.Builder()
            //添加请求的url
            requestBuilder.url(url)
            //处理待发送的状态
            if (okHttpClient == null) return
            val call = okHttpClient!!.newCall(requestBuilder.build())
            val downloadObserverOnSubscribe =
                DownloadObserverOnSubscribe(call, saveFilePath, iHttpDownload)
            //处理返回结果
            val downloadObserver = DownloadObserver(iHttpDownload)
            //发出请求
            Observable
                .create(downloadObserverOnSubscribe)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(downloadObserver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //并发的get请求
    @Synchronized
    fun requestGet(api: Api) {
        //处理待发送的状态
        if (okHttpClient == null) return
        val requestBuilder = Request.Builder()
        //添加请求的url
        if (api.params.size > 0) {
            var url = api.getUrl() + "?"
            api.params.entries.forEach { (key, value) ->
                url += "&" + key + "=" + value
            }
            requestBuilder.url(url)
        } else {
            requestBuilder.url(api.getUrl())
        }
        val call = okHttpClient!!.newCall(requestBuilder.build())
        val requestObserverOnSubscribe = RequestObserverOnSubscribe(true, call)
        //处理返回结果
        val responseObserver = GetResponseObserver(api.iHttpResponse)
        //发出请求
        Observable
            .create(requestObserverOnSubscribe)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(responseObserver)
    }

    //发送请求，发送之前，判断是否需要检查sessionId的有效性，
    // 如果需要判断则判断session是否即将过期，
    //如果即将过期，则先自动获取sesisonId再将队列中待发送的请求全部发送出去
    @Synchronized
    fun requestPost(api: Api) {
        try {
            //处理待发送的状态
            if (okHttpClient == null) return
            //初始化一次请求
            val requestBuilder = Request.Builder()
            val url: String = api.getUrl()
            requestBuilder.url(url)
            postParams(requestBuilder, api.params)
            api.params.clear()
            //添加请求头
            headers(requestBuilder, api.heanders)
            //处理待发送的状态
            val call = okHttpClient!!.newCall(requestBuilder.build())
            val requestObserverOnSubscribe = RequestObserverOnSubscribe(api.isSync, call)
            //处理返回结果
            val responseObserver = ResponseObserver(api)
            //发出请求
            Observable
                .create(requestObserverOnSubscribe)
                .subscribeOn(Schedulers.newThread())
                .observeOn(api.scheduler)
                .subscribe(responseObserver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //设置请求头
    private fun headers(requestBuilder: Request.Builder, params: Map<String, String>) {
        if (params.size > 0) {
            for ((key, value) in params) {
                requestBuilder.addHeader(key, value)
            }
        }
    }

    //设置post请求参数
    private fun postParams(requestBuilder: Request.Builder, params: Map<String, Any>) {
        if (params.size > 0) {
            val builder = FormBody.Builder(StandardCharsets.UTF_8)
            for (key in params.keys) {
                try {
                    val value = params[key] ?: continue
                    //传递键值对参数
                    builder.addEncoded(key, URLEncoder.encode(value.toString(), "utf-8") )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            requestBuilder.post(builder.build())
        }
    }

}