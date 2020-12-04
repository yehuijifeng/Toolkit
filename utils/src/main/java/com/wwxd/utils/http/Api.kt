package com.wwxd.utils.http

import com.wwxd.utils.rxandroid.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import java.util.*

/**
 * user：LuHao
 * time：2019/11/26 13:58
 * describe：请求服务器的api
 */
enum class Api {
    Translation {//翻译
        override fun getUrl(): String {
           return "https://fanyi-api.baidu.com/api/trans/vip/translate"
//            return "http://fanyi.youdao.com/translate"
        }
    };

    //post请求参数
    var params: MutableMap<String, Any>

    //请求头
    var heanders: MutableMap<String, String>

    init {
        params = HashMap()
        heanders = HashMap()
        //            heanders.put("Connection", "keep-alive");
//            heanders.put("platform", "0");//0,android;1,ios
//            heanders.put("phoneModel", Build.MODEL);//手机型号
//            heanders.put("systemVersion", Build.VERSION.RELEASE);//系统版本
    }

    //请求结果回调接口
    //回调接口
    var iHttpResponse: IHttpResponse? = null

    //请求地址，默认请求的api，如果需要切换成log的地址，则重写value的值
    abstract fun getUrl(): String

    //清除所有配置操作
    fun clean() {
        params.clear()
        heanders.clear()
        iHttpResponse = null
    }

    //线程调度器
    //io();//存储Bitmap到本地时，可以直接在Schedulers的io线程中执行任务
    //computation();//这个是计算工作默认的调度器，它与I/O操作无关。它也是许多RxJava方法的默认
    //newThread();//产生一个新线程
    //trampoline();//运行在当前线程。当有新任务时，并不会立即执行，而是将它加入队列按顺序执行
    var scheduler: Scheduler = AndroidSchedulers.mainThread()

    //是否异步请求。true,是异步的；false，不是异步;默认异步
    var isSync: Boolean = true
}