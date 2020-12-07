package com.wwxd.translation

import android.media.MediaRecorder
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import com.wwxd.utils.AppFile
import com.wwxd.utils.DateUtil.getServerTime
import com.wwxd.utils.FileUtil
import java.io.File
//录音音量监听
class EaseVoiceRecorder(private val handler: Handler) {
    var recorder: MediaRecorder? = null
    var isRecording = false
        private set
    private var startTime: Long = 0
    private var file: File? = null

    //开始录音
    fun startRecording(): Boolean {
        file = File(AppFile.DOCUMENTS_FILE.ObtainAppFilePath() + "testTransVoice.amr")
        try {
            if (file!!.exists()) file!!.delete()
            if (recorder != null) {
                //释放
                recorder!!.release()
                recorder = null
            }
            recorder = MediaRecorder()
            recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder!!.setAudioChannels(1) // MONO
            recorder!!.setAudioSamplingRate(8000) // 8000Hz
            recorder!!.setAudioEncodingBitRate(64) // seems if change this to
            recorder!!.setOutputFile(file!!.absolutePath)
            recorder!!.prepare()
            isRecording = true
            recorder!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Thread {
            try {
                while (isRecording) {
                    val msg = Message()
                    msg.what = recorder!!.maxAmplitude * 13 / 0x7FFF
                    handler.sendMessage(msg)
                    SystemClock.sleep(100)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        startTime = getServerTime()
        return FileUtil.isFile(file)
    }

    //取消录音
    fun discardRecording() {
        if (recorder != null) {
            try {
                recorder!!.stop()
                recorder!!.release()
                recorder = null
                FileUtil.deleteFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isRecording = false
    }

    //停止录音
    fun stopRecoding(): Int {
        if (recorder != null) {
            isRecording = false
            recorder!!.stop()
            recorder!!.release()
            recorder = null
            if (!FileUtil.isFile(file)) {
                return 0
            }
            if (file!!.length() == 0L) {
                file!!.delete()
                return 0
            }
            return (getServerTime() - startTime).toInt() / 1000
        }
        return 0
    }
}