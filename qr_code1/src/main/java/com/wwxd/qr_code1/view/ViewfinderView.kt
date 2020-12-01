package com.wwxd.qr_code1.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.wwxd.qr_code1.R
import com.wwxd.qr_code1.camera.CameraManager
import java.util.*
//取景框
class ViewfinderView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var cameraManager: CameraManager? = null
    private val paint: Paint
    private var resultBitmap: Bitmap? = null
    private val maskColor // 取景框外的背景颜色
            : Int
    private val resultColor // result Bitmap的颜色
            : Int
    private val laserColor // 红色扫描线的颜色
            : Int
    private val resultPointColor // 特征点的颜色
            : Int
    private val statusColor // 提示文字颜色
            : Int
    private val scannerAlpha: Int
    private var possibleResultPoints: MutableList<ResultPoint>
    private var lastPossibleResultPoints: List<ResultPoint>?

    // 扫描线移动的y
    private var scanLineTop = 0

    // 扫描线移动速度
    private var SCAN_VELOCITY = 10

    //扫描线高度
    private val scanLightHeight = 20

    // 扫描线
    var scanLight: Bitmap
    fun setCameraManager(cameraManager: CameraManager?) {
        this.cameraManager = cameraManager
    }

    @SuppressLint("DrawAllocation")
    public override fun onDraw(canvas: Canvas) {
        if (cameraManager == null) {
            return  // not ready yet, early draw before done configuring
        }

        // frame为取景框
        val frame = cameraManager!!.getFramingRect()
        val previewFrame = cameraManager!!.getFramingRectInPreview()
        if (frame == null || previewFrame == null) {
            return
        }
        val width1 = this.width
        val height1 = this.height

        // Draw the exterior (i.e. outside the framing rect) darkened
        // 绘制取景框外的暗灰色的表面，分四个矩形绘制
        paint.color = if (resultBitmap != null) resultColor else maskColor
        /*上面的框*/canvas.drawRect(0f, 0f, width1.toFloat(), frame.top.toFloat(), paint)
        /*绘制左边的框*/canvas.drawRect(
            0f,
            frame.top.toFloat(),
            frame.left.toFloat(),
            (frame.bottom + 1).toFloat(),
            paint
        )
        /*绘制右边的框*/canvas.drawRect(
            (frame.right + 1).toFloat(),
            frame.top.toFloat(),
            width1.toFloat(),
            (frame.bottom + 1).toFloat(),
            paint
        )
        /*绘制下面的框*/canvas.drawRect(
            0f,
            (frame.bottom + 1).toFloat(),
            width1.toFloat(),
            height1.toFloat(),
            paint
        )
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            // 如果有二维码结果的Bitmap，在扫取景框内绘制不透明的result Bitmap
            paint.alpha = CURRENT_POINT_OPACITY
            canvas.drawBitmap(resultBitmap!!, null, frame, paint)
        } else {

            /*绘制取景框边框*/
            drawFrameBounds(canvas, frame)

            /*绘制提示文字*/
            //  drawStatusText(canvas, frame, width);
            /*绘制扫描线*/drawScanLight(canvas, frame)
            val scaleX = frame.width() / previewFrame.width().toFloat()
            val scaleY = frame.height() / previewFrame.height().toFloat()

            // 绘制扫描线周围的特征点
            val currentPossible: List<ResultPoint> = possibleResultPoints
            val currentLast = lastPossibleResultPoints
            val frameLeft = frame.left
            val frameTop = frame.top
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null
            } else {
                possibleResultPoints = ArrayList(5)
                lastPossibleResultPoints = currentPossible
                paint.alpha = CURRENT_POINT_OPACITY
                paint.color = resultPointColor
                synchronized(currentPossible) {
                    for (point in currentPossible) {
                        canvas.drawCircle(
                            (frameLeft
                                    + (point.x * scaleX).toInt()).toFloat(), (frameTop
                                    + (point.y * scaleY).toInt()).toFloat(), POINT_SIZE.toFloat(),
                            paint
                        )
                    }
                }
            }
            if (currentLast != null) {
                paint.alpha = CURRENT_POINT_OPACITY / 2
                paint.color = resultPointColor
                synchronized(currentLast) {
                    val radius = POINT_SIZE / 2.0f
                    for (point in currentLast) {
                        canvas.drawCircle(
                            (frameLeft
                                    + (point.x * scaleX).toInt()).toFloat(), (frameTop
                                    + (point.y * scaleY).toInt()).toFloat(), radius, paint
                        )
                    }
                }
            }

            // Request another update at the animation interval, but only
            // repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(
                ANIMATION_DELAY, frame.left - POINT_SIZE,
                frame.top - POINT_SIZE, frame.right + POINT_SIZE,
                frame.bottom + POINT_SIZE
            )
        }
    }

    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private fun drawFrameBounds(canvas: Canvas, frame: Rect) {


        /*扫描框的边框线*/
//        paint.setColor(Color.WHITE);
//        paint.setStrokeWidth(2);
//        paint.setStyle(Paint.Style.STROKE);
//
//        canvas.drawRect(frame, paint);

        /*扫描框的四个角*/
        paint.color = ContextCompat.getColor(context, R.color.main_color_def)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 1f

        /*四个角的长度和宽度*/
        val width = frame.width()
        val corLength = (width * 0.1).toInt()
        var corWidth = (corLength * 0.2).toInt()
        if (corWidth > 15) {
            corWidth = 15
        }


        /*角在线外*/
        // 左上角
        canvas.drawRect(
            (frame.left - corWidth).toFloat(), frame.top.toFloat(), frame.left.toFloat(), (frame.top
                    + corLength).toFloat(), paint
        )
        canvas.drawRect(
            (frame.left - corWidth).toFloat(), (frame.top - corWidth).toFloat(), (frame.left
                    + corLength).toFloat(), frame.top.toFloat(), paint
        )
        // 右上角
        canvas.drawRect(
            frame.right.toFloat(), frame.top.toFloat(), (frame.right + corWidth).toFloat(), (
                    frame.top + corLength).toFloat(), paint
        )
        canvas.drawRect(
            (frame.right - corLength).toFloat(), (frame.top - corWidth).toFloat(), (
                    frame.right + corWidth).toFloat(), frame.top.toFloat(), paint
        )
        // 左下角
        canvas.drawRect(
            (frame.left - corWidth).toFloat(), (frame.bottom - corLength).toFloat(),
            frame.left.toFloat(), frame.bottom.toFloat(), paint
        )
        canvas.drawRect(
            (frame.left - corWidth).toFloat(), frame.bottom.toFloat(), (frame.left
                    + corLength).toFloat(), (frame.bottom + corWidth).toFloat(), paint
        )
        // 右下角
        canvas.drawRect(
            frame.right.toFloat(), (frame.bottom - corLength).toFloat(), (frame.right
                    + corWidth).toFloat(), frame.bottom.toFloat(), paint
        )
        canvas.drawRect(
            (frame.right - corLength).toFloat(), frame.bottom.toFloat(), (frame.right
                    + corWidth).toFloat(), (frame.bottom + corWidth).toFloat(), paint
        )
    }

    /**
     * 绘制提示文字
     *
     * @param canvas
     * @param frame
     * @param width
     */
    private fun drawStatusText(canvas: Canvas, frame: Rect, width: Int) {
        val statusText1 = resources.getString(
            R.string.viewfinderview_status_text1
        )
        val statusText2 = resources.getString(
            R.string.viewfinderview_status_text2
        )
        val statusTextSize: Int

        /*低分辨率处理*/statusTextSize = if (width >= 480 && width <= 600) {
            22
        } else if (width > 600 && width <= 720) {
            26
        } else {
            45
        }
        val statusPaddingTop = 180
        paint.color = statusColor
        paint.textSize = statusTextSize.toFloat()
        val textWidth1 = paint.measureText(statusText1).toInt()
        canvas.drawText(
            statusText1, ((width - textWidth1) / 2).toFloat(), (frame.top
                    - statusPaddingTop).toFloat(), paint
        )
        val textWidth2 = paint.measureText(statusText2).toInt()
        canvas.drawText(
            statusText2, ((width - textWidth2) / 2).toFloat(), (frame.top
                    - statusPaddingTop + 60).toFloat(), paint
        )
    }

    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private fun drawScanLight(canvas: Canvas, frame: Rect) {
        if (scanLineTop == 0 || scanLineTop + SCAN_VELOCITY >= frame.bottom) {
            scanLineTop = frame.top
        } else {

            /*缓动动画*/
            SCAN_VELOCITY = (frame.bottom - scanLineTop) / 12
            SCAN_VELOCITY =
                if (SCAN_VELOCITY > 10)
                    Math.ceil(SCAN_VELOCITY.toDouble()).toInt()
                else 10
            scanLineTop += SCAN_VELOCITY
        }
        val scanRect = Rect(
            frame.left, scanLineTop, frame.right,
            scanLineTop + scanLightHeight
        )
        canvas.drawBitmap(scanLight, null, scanRect, paint)
    }

    fun drawViewfinder() {
        val resultBitmap = resultBitmap
        this.resultBitmap = null
        resultBitmap?.recycle()
        invalidate()
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    fun drawResultBitmap(barcode: Bitmap?) {
        resultBitmap = barcode
        invalidate()
    }

    @Synchronized
    fun addPossibleResultPoint(point: ResultPoint) {
        val points = possibleResultPoints
        points.add(point)
        val size = points.size
        if (size > MAX_RESULT_POINTS) {
            points.subList(0, size - MAX_RESULT_POINTS / 2).clear()
        }
    }

    companion object {
        /*界面刷新间隔时间*/
        private const val ANIMATION_DELAY = 80L
        private const val CURRENT_POINT_OPACITY = 0xA0
        private const val MAX_RESULT_POINTS = 20
        private const val POINT_SIZE = 6
    }

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskColor = ContextCompat.getColor(context,R.color.viewfinder_mask)
        resultColor = ContextCompat.getColor(context,R.color.result_view)
        laserColor = ContextCompat.getColor(context,R.color.viewfinder_laser)
        resultPointColor = ContextCompat.getColor(context,R.color.possible_result_points)
        statusColor = ContextCompat.getColor(context,R.color.status_text)
        scannerAlpha = 0
        possibleResultPoints = ArrayList(5)
        lastPossibleResultPoints = null
        scanLight = BitmapFactory.decodeResource(resources, R.drawable.scan_light)
    }
}