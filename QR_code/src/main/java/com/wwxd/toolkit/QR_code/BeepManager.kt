package com.wwxd.toolkit.QR_code

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Vibrator
import java.io.Closeable

/**
 * 管理声音和震动
 */
class BeepManager(activity: Activity) : OnCompletionListener, MediaPlayer.OnErrorListener,
    Closeable {
    private var mediaPlayer //声音
            : MediaPlayer? = null
    private var vibrator //震动
            : Vibrator? = null

    /**
     * 开启响铃和震动
     */
    @Synchronized
    fun palyBeep() {
        if (mediaPlayer != null) {
            mediaPlayer!!.start()
        }
        if (vibrator != null) {
            vibrator?.vibrate(200L)
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        mp.seekTo(0)
    }

    @Synchronized
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        if (what != MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            mp.release()
        }
        if (mediaPlayer != null) mediaPlayer!!.release()
        mediaPlayer = null
        return true
    }

    @Synchronized
    override fun close() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    init {
        try {
            // 设置activity音量控制键控制的音频流
            activity.volumeControlStream = AudioManager.STREAM_MUSIC
            //初始化音频播放
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            // 监听是否播放完成
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.setOnErrorListener(this)
            // 配置播放资源
            val file = activity.resources.openRawResourceFd(R.raw.beep)
            mediaPlayer!!.setDataSource(file.fileDescriptor, file.startOffset, file.length)
            // 设置音量
            mediaPlayer!!.setVolume(0.1f, 0.1f)
            mediaPlayer!!.prepare()
            file.close()
        } catch (e: Exception) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        //初始化震动
        vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}