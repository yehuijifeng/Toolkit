package com.wwxd.translation

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.baidu.translate.asr.OnRecognizeListener
import com.baidu.translate.asr.TransAsrClient
import com.baidu.translate.asr.TransAsrConfig
import com.baidu.translate.asr.data.Language
import com.wwxd.base.BaseActivity
import com.wwxd.base.IDefaultDialogClickListener
import com.wwxd.utils.*
import kotlinx.android.synthetic.main.activity_trans_voice.*


/**
 * user：LuHao
 * time：2020/12/4 18:07
 * describe：语音翻译,每月可享用1万次免费调用量
 */
class TransVoiceActivity : BaseActivity() {
    private var lauageFromType = Language.English
    private var lauageToType = Language.Chinese
    private var client: TransAsrClient? = null
    private val voiceCode = 111
    private var wakeLock: WakeLock? = null //避免屏幕因为录音时间过长而锁屏
    private var voiceRecorder: EaseVoiceRecorder? = null//调节音量

    // 动画资源文件,用于录制语音时
    // 动画资源文件,用于录制语音时
    private var micImages: Array<Drawable?>? = null


    override fun getContentView(): Int {
        return R.layout.activity_trans_voice
    }

    @SuppressLint("ClickableViewAccessibility", "InvalidWakeLockTag")
    override fun init() {
        micImages = arrayOf(
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_01),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_02),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_03),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_04),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_05),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_06),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_07),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_08),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_09),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_10),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_11),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_12),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_13),
            ContextCompat.getDrawable(this, R.drawable.ease_record_animate_14)
        )
        //防止息屏
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "toolkit")
        //监听音量,根据麦克风音量的大小来调整动画
        voiceRecorder = EaseVoiceRecorder(OnVoiceImageHandler())
        llVoice.visibility = View.INVISIBLE
        spinnerCountriesFrom.setAdapter(OnSpinnerAdapterFrom())
        spinnerCountriesFrom.setOnItemSelectedListener(OnItemSelectedListenerFrom())
        spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageFromType), true)
        spinnerCountriesTo.setAdapter(OnSpinnerAdapterTo())
        spinnerCountriesTo.setOnItemSelectedListener(OnItemSelectedListenerTo())
        spinnerCountriesTo.setSelection(Language.values().indexOf(lauageToType), true)
        // 其他配置项参照[其他常用接口]
        val config = TransAsrConfig(BuildConfig.app_id, BuildConfig.app_secret)
        //设置是否回调中间结果；true，回调；false，不回调；
        config.setPartialCallbackEnabled(false)
        //获取到翻译结果以后，是否自动TTS播报译文；true，自动；false，不自动；
        config.setAutoPlayTts(true)
        //设置英文TTS发音类型；TTS_ENGLISH_TYPE_US} - 美式发音； TTS_ENGLISH_TYPE_UK} - 英式发音
        config.setTtsEnglishType(TransAsrConfig.TTS_ENGLISH_TYPE_US)
        //设置语音识别开始时的声音
//        config.setRecognizeStartAudioRes(R.id.audioRawResId)
        client = TransAsrClient(this, config)
        client!!.setRecognizeListener { resultType, result ->
            if (resultType == OnRecognizeListener.TYPE_FINAL_RESULT) { // 最终结果
                if (result.error == 0) { // 表示正常，有识别结果
                    // 语音识别结果
                    addVoiceUseNum()
                    val content = StringBuilder()
                    content.append(getString(R.string.str_trans_asr)).append(result.asrResult)
                        .append("\n")
                    content.append(getString(R.string.str_trans_result)).append(result.transResult)
                        .append("\n")
                    etContent.setText(content)
                } else { // 翻译出错
                    etContent.setText(
                        String.format(getString(R.string.str_voice_trans_error), result.errorMsg)
                    )
                }
            }
        }
        //判断某个语种是否支持语音播报
        Language.isTtsAvailable(Language.English.language)
        btnTouch.setOnTouchListener { v, event ->
            if (PermissionsUtil.lacksPermission(Manifest.permission.RECORD_AUDIO))
                PermissionsUtil.requestPermissions(
                    this,
                    Manifest.permission.RECORD_AUDIO,
                    voiceCode
                )
            else
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        start()
                    }
                    MotionEvent.ACTION_UP -> {
                        stop(event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        move(event)
                    }
                }
            true
        }
    }

    private fun addVoiceUseNum() {
        SharedPreferencesUtil.saveInt(
            TransContanst.TransVoiceNum,
            SharedPreferencesUtil.getInt(TransContanst.TransVoiceNum, 0) + 1
        )
    }

    private inner class OnVoiceImageHandler : Handler() {
        override fun handleMessage(msg: Message) {
            // 切换msg切换图片
            imgVoice.setImageDrawable(micImages!![msg.what])
        }
    }

    //选择语言
    private inner class OnSpinnerAdapterFrom :
        ArrayAdapter<Language>(this, R.layout.item_lauage_spinner, Language.values()) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convertView1 = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lauage_spinner, parent, false)
            val textSpinner = convertView1.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView1 = convertView
            if (convertView1 == null) {
                convertView1 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lauage_spinner_two, parent, false)
            }
            val textSpinner = convertView1!!.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }
    }

    //选择语言监听
    private inner class OnItemSelectedListenerFrom : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val type = Language.values()[position]
            if (lauageToType == type) {
                spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageToType), true)
                spinnerCountriesTo.setSelection(Language.values().indexOf(lauageFromType), true)
                lauageToType = lauageFromType
                lauageFromType = type
            } else if (!Language.isAsrAvailable(type.abbreviation)) {//判断某个语种是否支持语音识别
                spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageFromType), true)
                ToastUtil.showLongToast(
                    String.format(
                        getString(R.string.str_translation_available),
                        type.language
                    )
                )
            } else
                lauageFromType = type
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    //选择语言
    private inner class OnSpinnerAdapterTo :
        ArrayAdapter<Language>(this, R.layout.item_lauage_spinner, Language.values()) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convertView1 = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lauage_spinner, parent, false)
            val textSpinner = convertView1.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView1 = convertView
            if (convertView1 == null) {
                convertView1 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lauage_spinner_two, parent, false)
            }
            val textSpinner = convertView1!!.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }
    }

    //选择语言监听
    private inner class OnItemSelectedListenerTo : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val type = Language.values()[position]
            if (lauageFromType == type) {
                spinnerCountriesTo.setSelection(Language.values().indexOf(lauageFromType), true)
                spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageToType), true)
                lauageFromType = lauageToType
                lauageToType = type
            } else if (!Language.isAsrAvailable(type.abbreviation)) {//判断某个语种是否支持语音识别
                spinnerCountriesTo.setSelection(Language.values().indexOf(lauageToType), true)
                ToastUtil.showLongToast(
                    String.format(
                        getString(R.string.str_translation_available),
                        type.language
                    )
                )
            } else
                lauageToType = type
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == voiceCode) {
            if (PermissionsUtil.lacksPermission(Manifest.permission.RECORD_AUDIO))
                getDefaultDialog().getBuilder()
                    .isShowTiltle(false)
                    .isBackDismiss(false)
                    .setContent(getString(R.string.str_void_permission_error))
                    .setOkText(getString(R.string.str_go_settings))
                    .setCancelText(getString(R.string.str_cancel))
                    .setOkClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                            PermissionsUtil.goToSetting(this@TransVoiceActivity)
                        }
                    })
                    .show()
        }
    }

    /**
     * 开始语音识别
     *
     * @param from 需要识别的语种（简写，例如zh、en）
     * @param to   需要翻译到的目标语种（简写，例如zh、en）
     */
    private fun start() {
        if (client != null && voiceRecorder != null) {
            if (wakeLock != null) {
                wakeLock!!.acquire(3 * 60 * 1000L)
            }
            llVoice.visibility = View.VISIBLE
            textTips.text = getString(R.string.str_up_trans_voice)
            textTips.setBackgroundColor(Color.TRANSPARENT)
            btnTouch.isPressed = true
            voiceRecorder!!.startRecording()
            client!!.startRecognize(lauageFromType.abbreviation, lauageToType.abbreviation)
        }
    }

    //上滑取消录音
    private fun move(event: MotionEvent) {
        llVoice.visibility = View.VISIBLE
        if (event.getY() < 0) {
            textTips.text = getString(R.string.str_move_trans_voice)
            textTips.setBackgroundResource(R.drawable.bg_move_trans_voice_cancel)
        } else {
            textTips.text = getString(R.string.str_up_trans_voice)
            textTips.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * 停止语音识别（会有回调）
     */
    private fun stop(event: MotionEvent) {
        try {
            if (client != null && voiceRecorder != null) {
                if (wakeLock != null && wakeLock!!.isHeld)
                    wakeLock!!.release()
                btnTouch.isPressed = false
                llVoice.visibility = View.INVISIBLE
                if (event.getY() < 0) {
                    // 取消音频记录
                    // 停止录音
                    if (voiceRecorder!!.isRecording) {
                        voiceRecorder!!.discardRecording()
                    }
                } else {
                    // 停止记录音频并发送文件
                    val length = voiceRecorder!!.stopRecoding()
                    if (length >= 1) {
                        if (length <= 60) {
                            client!!.stopRecognize()
                        } else {
                            client!!.cancelRecognize()
                            ToastUtil.showLongToast(getString(R.string.str_voice_length_max))
                        }
                    } else {
                        client!!.cancelRecognize()
                        ToastUtil.showLongToast(getString(R.string.str_voice_length_min))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}