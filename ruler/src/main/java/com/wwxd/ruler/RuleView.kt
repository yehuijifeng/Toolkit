package com.wwxd.ruler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.wwdx.toolkit.utils.DisplayUtil
import com.wwdx.toolkit.utils.WindowsUtil
import com.wwxd.toolkit.base.AppConstant
import kotlin.concurrent.thread

/**
 * user：LuHao
 * time：2020/11/30 13:23
 * describe：尺子
 */
class RuleView : SurfaceView, SurfaceHolder.Callback {
    private var UNIT_MM = 0f
    private var RULE_HEIGHT = 0f
    private var RULE_SCALE = 0f
    private var SCREEN_W = 0
    private var SCREEN_H = 0
    private var FONT_SIZE = 0f
    private var PADDING = 0f
    private var RADIUS_BIG = 0f
    private var RADIUS_MEDIUM = 0f
    private var RADIUS_SMALL = 0f
    private var CYCLE_WIDTH = 0f
    private var DISPLAY_SIZE_BIG = 0f
    private var DISPLAY_SIZE_SMALL = 0f
    private var holder1: SurfaceHolder
    private var unlockLineCanvas = false
    private var lineX = 0f
    private var lineOffset = 0f
    private var startX = 0f
    private var lastX = 0f
    private var kedu = 0
    private var paint: Paint
    private var linePaint: Paint
    private var fontPaint: Paint

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private fun onTouchBegain(x: Float, y: Float) {
        lineOffset = Math.abs(y - lineX)
        if (lineOffset <= PADDING * 2) {
            startX = y
            unlockLineCanvas = true
        }
    }

    private fun onTouchMove(x: Float, y: Float) {
        if (unlockLineCanvas) {
            lineX += y - startX
            if (lineX < PADDING) {
                lineX = PADDING
            } else if (lineX > lastX) {
                lineX = lastX
            }
            kedu = Math.round((lineX - PADDING) / UNIT_MM)
            startX = y
            draw()
        }
    }

    private fun onTouchDone(x: Float, y: Float) {
        unlockLineCanvas = false
        startX = -1f
        draw()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> onTouchDone(event.x, event.y)
            MotionEvent.ACTION_DOWN -> onTouchBegain(event.x, event.y)
            MotionEvent.ACTION_MOVE -> onTouchMove(event.x, event.y)
        }
        return true
    }

    init {
        RADIUS_BIG = DisplayUtil.dip2px(46f)
        RADIUS_MEDIUM = DisplayUtil.dip2px( 40f)
        RADIUS_SMALL = DisplayUtil.dip2px( 20f)
        CYCLE_WIDTH = DisplayUtil.dip2px( 4f)
        DISPLAY_SIZE_BIG = DisplayUtil.dip2px( 40f)
        DISPLAY_SIZE_SMALL = DisplayUtil.dip2px( 20f)
        UNIT_MM = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f,DisplayUtil.getDisplayMetrics())
        RULE_HEIGHT = DisplayUtil.dip2px( 30f)
        FONT_SIZE = DisplayUtil.dip2px( 20f)
        PADDING = FONT_SIZE / 2
        SCREEN_W = DisplayUtil.getWindowWidth()
        SCREEN_H = DisplayUtil.getWindowHeight()
        holder1 = getHolder()
        holder1.addCallback(this)
        paint = Paint()
        paint.color = -0xe14708
        linePaint = Paint()
        linePaint.color = -0xe14708
        linePaint.strokeWidth = 4f
        fontPaint = Paint()
        fontPaint.textSize = FONT_SIZE
        fontPaint.isAntiAlias = true
        fontPaint.color = -0xe14708
        lineX = PADDING
        kedu = 0
    }

    //画刻度
    private fun draw() {
        val canvas = holder1.lockCanvas()
        try {
            canvas.drawColor(-0x1)
            var left = PADDING
            var i = 0
            while (SCREEN_H - PADDING - left > 0) {
                RULE_SCALE = 0.5f
                if (i % 5 == 0) {
                    if (i and 0x1 == 0) {
                        RULE_SCALE = 1f
                        val txt = (i / 10).toString()
                        val bounds = Rect()
                        fontPaint.getTextBounds(txt, 0, txt.length, bounds)
                        canvas.drawText(
                            txt,
                            RULE_HEIGHT + FONT_SIZE / 2 + bounds.height(),
                            left + bounds.height() / 2,
                            fontPaint
                        )
                    } else {
                        RULE_SCALE = 0.75f
                    }
                }
                val rect = RectF()
                rect.left = 0f
                rect.top = left - 1
                rect.right = rect.left + RULE_HEIGHT * RULE_SCALE
                rect.bottom = left + 1
                canvas.drawRect(rect, paint)
                left += UNIT_MM
                i++
            }
            lastX = left - UNIT_MM
            drawDisplay(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (canvas != null) {
                holder1.unlockCanvasAndPost(canvas)
            }
        }
    }

    //画刻度指引线
    private fun drawDisplay(canvas: Canvas) {
        val cm = (kedu / 10).toString()
        val mm = (kedu % 10).toString()
        val displayPaint1 = Paint() //厘米
        displayPaint1.isAntiAlias = true
        displayPaint1.color = -0xe14708
        displayPaint1.textSize = DISPLAY_SIZE_BIG
        val cmWidth = displayPaint1.measureText(cm)
        val bounds1 = Rect()
        displayPaint1.getTextBounds(cm, 0, cm.length, bounds1)
        val displayPaint2 = Paint()//毫米
        displayPaint2.isAntiAlias = true
        displayPaint2.color = -0x99999a
        displayPaint2.textSize = DISPLAY_SIZE_SMALL
        val mmWidth = displayPaint2.measureText(mm)
        val bounds2 = Rect()
        displayPaint2.getTextBounds(mm, 0, mm.length, bounds2)
        canvas.drawLine(0f, lineX, SCREEN_H.toFloat(), lineX, linePaint) //指引线
        val cyclePaint = Paint()//圆圈
        cyclePaint.color = -0x1
        cyclePaint.isAntiAlias = true
        cyclePaint.style = Paint.Style.FILL
        val strokPaint = Paint()//圆
        strokPaint.isAntiAlias = true
        strokPaint.color = -0x666667
        strokPaint.style = Paint.Style.STROKE
        strokPaint.strokeWidth = CYCLE_WIDTH
        canvas.drawCircle(//厘米的大圆圈
            (SCREEN_W / 2).toFloat(),
            (SCREEN_H / 2).toFloat(),
            RADIUS_BIG,
            cyclePaint
        )
        canvas.drawCircle(//厘米的小圆圈
            (SCREEN_W / 2).toFloat(),
            (SCREEN_H / 2).toFloat(),
            RADIUS_MEDIUM,
            cyclePaint
        )
        canvas.drawCircle(//毫米的圆圈
            (SCREEN_W / 2).toFloat(),
            (SCREEN_H / 2).toFloat(),
            RADIUS_BIG,
            strokPaint
        )
        strokPaint.color = -0x99999a
        canvas.drawCircle(
            (SCREEN_W / 2).toFloat(),
            (SCREEN_H / 2).toFloat(),
            RADIUS_MEDIUM,
            strokPaint
        )
        strokPaint.color = -0x666667
        canvas.drawCircle(
            SCREEN_W / 2 + RADIUS_BIG, (SCREEN_H / 2).toFloat(),
            RADIUS_SMALL, cyclePaint
        )
        canvas.drawCircle(
            SCREEN_W / 2 + RADIUS_BIG, (SCREEN_H / 2).toFloat(),
            RADIUS_SMALL, strokPaint
        )
        canvas.drawText(
            cm, SCREEN_W / 2 - cmWidth / 2, (
                    SCREEN_H / 2 + bounds1.height() / 2).toFloat(), displayPaint1
        )
        canvas.drawText(
            mm, SCREEN_W / 2 + RADIUS_BIG - mmWidth / 2, (SCREEN_H
                    / 2 + bounds2.height() / 2).toFloat(), displayPaint2
        )
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread {
            SCREEN_H = height
            draw()
        }
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}