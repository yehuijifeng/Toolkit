package com.wwxd.subtitles

import android.content.pm.ActivityInfo
import android.os.Handler
import androidx.core.content.ContextCompat
import com.wwxd.base.BaseActivity
import kotlinx.android.synthetic.main.activity_subtitles.*

/**
 * user：LuHao
 * time：2020/12/8 16:19
 * describe：LED字幕
 */
class SubtitlesActivity : BaseActivity() {
    private var backColor: Int = 0
    private var textColor: Int = 0
    private var textSize: Float = 50*5f
    private var textContent: String = ""
    override fun isFullWindow(): Boolean {
        return true
    }

    override fun getContentView(): Int {
        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        return R.layout.activity_subtitles
    }

    override fun init() {
        textSize = getInt(SubtitlesConstant.textSize, 16) * 5f
        textContent = getString(SubtitlesConstant.textContent, "")
        backColor = getInt(SubtitlesConstant.backColor, R.color.black)
        textColor = getInt(SubtitlesConstant.textColor, R.color.white)
        marqueeTextView.setTextColor(ContextCompat.getColor(this, textColor))
        llRoot.setBackgroundResource(backColor)
        Handler().postDelayed(object :Runnable{
            override fun run() {
                marqueeTextView.requestLayout()
                marqueeTextView.setTextSize(textSize)
                marqueeTextView.setText(textContent)
                marqueeTextView.requestLayout()
            }
        },500L)
    }

}