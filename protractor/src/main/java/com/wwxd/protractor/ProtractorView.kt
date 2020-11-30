package com.wwxd.protractor

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.wwxd.utils.DisplayUtil
import kotlin.math.*

class ProtractorView : View {
    private var kedu = 0
    private var width1 = 0
    private var height1 = 0
    private var radius = 0f
    private var padding = 0f
    private var fontSize = 0f
    private var offset = 0f
    private var coordinate2: Coordinate? = null
    private var arcPaintWidth = 0f
    private var paint2Width = 0f
    private var RADIUS_BIG = 0f
    private var RADIUS_MEDIUM = 0f
    private var CYCLE_WIDTH = 0f
    private var DISPLAY_SIZE_BIG = 0f
    private var DISPLAY_SIZE_SMALL = 0f
    private val paint = Paint()
    private val xpaint = Paint()
    private val paint2 = Paint()
    private val degreePaint = Paint()
    private val arcPaint = Paint()
    private val oval1 = RectF()
    private val oval = RectF()
    private val srcOut = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    private val oval2 = RectF()

    /**
     * 坐标
     */
    internal inner class Coordinate {
        constructor()
        constructor(x: Float, y: Float) {
            this.x = x
            this.y = y
        }

        var x = 0f
        var y = 0f
        override fun toString(): String {
            return "[x:$x, y:$y]"
        }
    }

    /**
     * 计算出字体的path让字体跟着path走
     */
    private fun getTextPath(text: String, paint: Paint, degree: Double, r: Float): Path {
        val pathDegree = abs(90 - degree)
        val textWidth = paint.measureText(text)
        val y = abs((textWidth * sin(pathDegree / 180 * PI)).toFloat())
        val x = abs((textWidth * cos(pathDegree / 180 * PI)).toFloat())
        val coordinate = getCoordinate(r, degree)
        val start = Coordinate()
        val end = Coordinate()
        if (degree < 90) {
            end.x = -coordinate.x + x / 2
            end.y = -coordinate.y - y / 2
            start.x = -coordinate.x - x / 2
            start.y = -coordinate.y + y / 2
        } else {
            end.x = -coordinate.x + x / 2
            end.y = -coordinate.y + y / 2
            start.x = -coordinate.x - x / 2
            start.y = -coordinate.y - y / 2
        }
        val path = Path()
        path.moveTo(start.x, start.y)
        path.lineTo(end.x, end.y)
        return path
    }

    //画指示器
    private fun drawDisplay(canvas: Canvas) {
        val cm = kedu.toString()
        val mm = (kedu % 10).toString()
        val displayPaint1 = Paint()
        displayPaint1.isAntiAlias = true
        displayPaint1.color = -0xe14708
        displayPaint1.textSize = DISPLAY_SIZE_BIG
        val cmWidth = displayPaint1.measureText(cm)
        val bounds1 = Rect()
        displayPaint1.getTextBounds(cm, 0, cm.length, bounds1)
        val displayPaint2 = Paint()
        displayPaint2.isAntiAlias = true
        displayPaint2.color = -0x99999a
        displayPaint2.textSize = DISPLAY_SIZE_SMALL
        val bounds2 = Rect()
        displayPaint2.getTextBounds(mm, 0, mm.length, bounds2)
        val cyclePaint = Paint()
        cyclePaint.color = -0x1
        cyclePaint.isAntiAlias = true
        cyclePaint.style = Paint.Style.FILL
        val strokPaint = Paint()
        strokPaint.isAntiAlias = true
        strokPaint.color = -0x666667
        strokPaint.style = Paint.Style.STROKE
        strokPaint.strokeWidth = CYCLE_WIDTH
        val height11 = 7f / 10f
        val width11 = 1f / 2f
        canvas.drawCircle(
            (width1 * width11),
            (height1 * height11),
            RADIUS_BIG,
            cyclePaint
        )
        canvas.drawCircle(
            (width1 * width11),
            (height1 * height11),
            RADIUS_MEDIUM,
            cyclePaint
        )
        canvas.drawCircle(
            (width1 * width11),
            (height1 * height11),
            RADIUS_BIG,
            strokPaint
        )
        strokPaint.color = -0x99999a
        canvas.drawCircle(
            (width1 * width11),
            (height1 * height11),
            RADIUS_MEDIUM,
            strokPaint
        )
        strokPaint.color = -0x666667
        canvas.drawText(
            cm, width1 * width11 - cmWidth / 2, (
                    height1 * height11 + bounds1.height() / 2), displayPaint1
        )
    }

    private fun getCoordinate(r: Float, degree: Double): Coordinate {
        val x = (r * cos(degree / 180 * PI)).toFloat()
        val y = (r * sin(degree / 180 * PI)).toFloat()
        return Coordinate(x, y)
    }

    private fun onTouchBegain(coordinate: Coordinate) {
        caculatePoint(coordinate)
    }

    private fun onTouchMove(coordinate: Coordinate) {
        caculatePoint(coordinate)
    }

    private fun onTouchDone(coordinate: Coordinate) {
        // caculatePoint(coordinate);
    }

    //移动指针
    private fun caculatePoint(coordinate: Coordinate) {
        val mx = width1 / 2f
        val my = height1 - offset
        if (coordinate.y > my) {
            coordinate.y = my
        }
        val dx = coordinate.x - mx
        val dy = coordinate.y - my
        val r = sqrt((dx * dx + dy * dy).toDouble())
        val x = (dx / r * radius).toFloat()
        val y = (dy / r * radius).toFloat()
        coordinate2 = Coordinate(x, y)
        kedu = round(atan((dy / dx).toDouble()) / PI * 180).toInt()
        if (dx >= 0) {
            kedu += 180
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL -> onTouchDone(Coordinate(event.x, event.y))
            MotionEvent.ACTION_UP -> onTouchDone(Coordinate(event.x, event.y))

            MotionEvent.ACTION_MOVE -> onTouchMove(Coordinate(event.x, event.y))
            MotionEvent.ACTION_DOWN -> onTouchBegain(Coordinate(event.x, event.y))
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        width1 = width
        height1 = height
        radius = width1 / 2f
        paint.color = -0x1
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        offset = (height1 - width1 / 2f) / 2f
        oval.left = 0f
        oval.top = offset
        oval.right = width1.toFloat()
        oval.bottom = width1 + offset
        canvas.drawArc(oval, 180f, 180f, true, paint)
        // 反向填充
        xpaint.isAntiAlias = true
        xpaint.xfermode = srcOut
        xpaint.style = Paint.Style.FILL
        xpaint.color = 0x5fffffff
        oval2.left = 0f
        oval2.top = 0f
        oval2.right = width1.toFloat()
        oval2.bottom = height1.toFloat()
        canvas.drawRect(oval2, xpaint)
        canvas.save()
        canvas.translate((width1 / 2).toFloat(), height1 - offset)
        paint2.isAntiAlias = true
        paint2.color = 0x6fffffff
        paint2.strokeWidth = paint2Width
        degreePaint.isAntiAlias = true
        degreePaint.textSize = fontSize
        degreePaint.color = 0x6fffffff
        for (i in 1..179) {
            val coordinate = getCoordinate(radius, i.toDouble())
            val x = coordinate.x
            val y = coordinate.y
            var r = radius - padding / 2
            if (i % 5 == 0) {
                if (i and 0x1 == 0) {
                    // 10
                    r = radius - padding
                    val text: String = i.toString()
                    val path = getTextPath(
                        text, degreePaint, i.toDouble(), radius
                                - padding - fontSize * 5 / 4
                    )
                    canvas.drawTextOnPath(text, path, 0f, 0f, degreePaint)
                } else {
                    // 5
                    r = radius - padding * 3 / 4
                }
            }
            val coordinate1 = getCoordinate(r, i.toDouble())
            val x1 = coordinate1.x
            val y1 = coordinate1.y
            canvas.drawLine(-x1, -y1, -x, -y, paint2)
        }
        arcPaint.isAntiAlias = true
        arcPaint.color = 0x6fffffff
        arcPaint.strokeWidth = arcPaintWidth
        arcPaint.style = Paint.Style.STROKE
        oval1.left = -width1 / 2f
        oval1.top = offset * 2f - height1
        oval1.right = width1 / 2f
        oval1.bottom = height1 - offset * 2f
        canvas.drawArc(oval1, 180f, 180f, true, arcPaint)
        canvas.drawLine(0f, 0f, 0f, -padding, paint2)
        if (coordinate2 != null) {
            canvas.drawLine(0f, 0f, coordinate2!!.x, coordinate2!!.y, paint2)
        }
        canvas.restore()
        drawDisplay(canvas)
    }


    init {
        paint2Width = DisplayUtil.dip2px(1f)
        arcPaintWidth = DisplayUtil.dip2px(1f)

        padding = DisplayUtil.dip2px(15f)
        fontSize = DisplayUtil.dip2px(11f)

        RADIUS_BIG = DisplayUtil.dip2px(23f)
        RADIUS_MEDIUM = DisplayUtil.dip2px(20f)

        CYCLE_WIDTH = DisplayUtil.dip2px(2f)

        DISPLAY_SIZE_BIG = DisplayUtil.dip2px(20f)
        DISPLAY_SIZE_SMALL = DisplayUtil.dip2px(10f)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}