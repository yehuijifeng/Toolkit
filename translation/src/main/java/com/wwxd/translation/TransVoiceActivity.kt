package com.wwxd.translation

import com.baidu.translate.asr.OnRecognizeListener
import com.baidu.translate.asr.TransAsrClient
import com.baidu.translate.asr.TransAsrConfig
import com.wwxd.base.BaseActivity
import com.wwxd.utils.LogUtil


/**
 * user：LuHao
 * time：2020/12/4 18:07
 * describe：语音翻译
 */
class TransVoiceActivity : BaseActivity() {
    var client: TransAsrClient? = null
    override fun getContentView(): Int {
        return R.layout.activity_trans_voice
    }

    override fun init() {
// 其他配置项参照[其他常用接口]
        val config = TransAsrConfig(BuildConfig.app_id, BuildConfig.app_secret)
        client = TransAsrClient(this, config)
        client!!.setRecognizeListener { resultType, result ->
            if (resultType == OnRecognizeListener.TYPE_PARTIAL_RESULT) { // 中间结果
                LogUtil.d("中间识别结果：" + result.asrResult)
            } else if (resultType == OnRecognizeListener.TYPE_FINAL_RESULT) { // 最终结果
                if (result.error == 0) { // 表示正常，有识别结果
                    // 语音识别结果
                    LogUtil.d("最终识别结果：" + result.asrResult)
                    LogUtil.d("翻译结果：" + result.transResult)
                } else { // 翻译出错
                    LogUtil.d(

                        "语音翻译出错 错误码：" + result.error + " 错误信息：" + result.errorMsg
                    )
                }
            }
        }
    }

    /**
     * 开始语音识别
     *
     * @param from 需要识别的语种（简写，例如zh、en）
     * @param to   需要翻译到的目标语种（简写，例如zh、en）
     */
    private fun start() {
        client!!.startRecognize("zh", "en")
    }

    /**
     * 停止语音识别（会有回调）
     */
    private fun stop() {
        client!!.stopRecognize();
    }

    /**
     * 取消语音识别（不会有回调）
     */
    private fun cancel(){
        client!!.cancelRecognize();
    }
}