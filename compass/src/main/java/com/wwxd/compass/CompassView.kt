package com.wwxd.compass

import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.wwxd.utils.DisplayUtil
import java.lang.StringBuilder


/**
 * user：LuHao
 * time：2020/12/3 15:40
 * describe：指南针ui
 */
class CompassView : View {
    private var mCanvas: Canvas? = null

    //View矩形的宽度
    private var width1 = 0

    //指南针圆心点坐标
    private var mCenterX = 0f
    private var mCenterY = 0f

    //外圆半径
    private var mOutSideRadius = 0f

    //外接圆半径
    private var mCircumRadius = 0f

    //指南针文字大小空间高度
    private var mTextHeight = 0f

    //暗红色 外圈笔
    private var mDarkRedPaint: Paint

    //深灰 外圈笔
    private var mDeepGrayPaint: Paint

    //外三角笔
    private var mOutSideCircumPaint: Paint

    //浅灰 外圈笔
    private var mLightGrayPaint: Paint

    //指南针上面 文字笔
    private var mTextPaint: Paint

    //外接圆，三角形笔
    private var mCircumPaint: Paint

    //指南针上面文字的外接矩形,用来测文字大小让文字居中
    private var mTextRect: Rect

    //外圈小三角形的Path
    private var mOutsideTriangle: Path

    //外接圆小三角形的Path
    private var mCircumTriangle: Path

    //NESW 文字笔 和文字外接矩形
    private var mNorthPaint: Paint
    private var mSouthPaint: Paint
    private var mOthersPaint: Paint
    private var mPositionRect: Rect

    //小刻度文字大小矩形和画笔
    private var mSamllDegreePaint: Paint

    //两位数的
    private var mSencondRect: Rect

    //三位数的
    private var mThirdRect: Rect

    //圆心数字矩形
    private var mCenterTextRect: Rect

    //中心文字笔
    private var mCenterPaint: Paint

    //内心圆是一个颜色辐射渐变的圆
    private var mInnerShader: Shader? = null
    private var mInnerPaint: Paint

    //定义个点击属性动画
    private var mValueAnimator: ValueAnimator? = null

    // camera绕X轴旋转的角度
    private var mCameraRotateX = 0f

    // camera绕Y轴旋转的角度
    private var mCameraRotateY = 0f

    //camera最大旋转角度
    private val mMaxCameraRotate = 10f

    // camera绕X轴旋转的角度
    private var mCameraTranslateX = 0f

    // camera绕Y轴旋转的角度
    private var mCameraTranslateY = 0f

    //camera最大旋转角度
    private var mMaxCameraTranslate = 0f

    //camera矩阵
    private var mCameraMatrix: Matrix

    //设置camera
    private var mCamera: Camera

    private var val1 = 0f
    private var valCompare = 0f

    //偏转角度红线笔
    private var mAnglePaint: Paint

    //方位文字
    private var text = "北"

    fun getVal(): Float {
        return val1
    }

    fun setVal(val1: Float) {
        this.val1 = val1
        invalidate()
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mDarkRedPaint = Paint()
        mDarkRedPaint.setStyle(Paint.Style.STROKE)
        mDarkRedPaint.setAntiAlias(true)
        mDarkRedPaint.setColor(ContextCompat.getColor(context, R.color.darkRed))
        mDeepGrayPaint = Paint()
        mDeepGrayPaint.setStyle(Paint.Style.STROKE)
        mDeepGrayPaint.setAntiAlias(true)
        mDeepGrayPaint.setColor(ContextCompat.getColor(context, R.color.deepGray))
        mLightGrayPaint = Paint()
        mLightGrayPaint.setStyle(Paint.Style.FILL)
        mLightGrayPaint.setAntiAlias(true)
        mLightGrayPaint.setColor(ContextCompat.getColor(context, R.color.lightGray))
        mTextPaint = Paint()
        mTextPaint.setStyle(Paint.Style.FILL)
        mTextPaint.setAntiAlias(true)
        mTextPaint.setTextSize(DisplayUtil.sp2px(30f))
        mTextPaint.setColor(ContextCompat.getColor(context, R.color.white))
        mCircumPaint = Paint()
        mCircumPaint.setStyle(Paint.Style.FILL)
        mCircumPaint.setAntiAlias(true)
        mCircumPaint.setColor(ContextCompat.getColor(context, R.color.red))
        mOutSideCircumPaint = Paint()
        mOutSideCircumPaint.setStyle(Paint.Style.FILL)
        mOutSideCircumPaint.setAntiAlias(true)
        mOutSideCircumPaint.setColor(ContextCompat.getColor(context, R.color.white))
        mTextRect = Rect()
        mOutsideTriangle = Path()
        mCircumTriangle = Path()
        val northSize=DisplayUtil.sp2px(20f)
        mNorthPaint = Paint()
        mNorthPaint.setStyle(Paint.Style.FILL)
        mNorthPaint.setAntiAlias(true)
        mNorthPaint.setTextSize(northSize)
        mNorthPaint.setColor(ContextCompat.getColor(context, R.color.red))
        mSouthPaint = Paint()
        mSouthPaint.setStyle(Paint.Style.FILL)
        mSouthPaint.setAntiAlias(true)
        mSouthPaint.setTextSize(northSize)
        mSouthPaint.setColor(ContextCompat.getColor(context, R.color.color_1678ff))
        mOthersPaint = Paint()
        mOthersPaint.setStyle(Paint.Style.FILL)
        mOthersPaint.setAntiAlias(true)
        mOthersPaint.setTextSize(northSize)
        mOthersPaint.setColor(ContextCompat.getColor(context, R.color.white))
        mPositionRect = Rect()
        mCenterTextRect = Rect()
        mCenterPaint = Paint()
        mCenterPaint.setStyle(Paint.Style.FILL)
        mCenterPaint.setAntiAlias(true)
        mCenterPaint.setTextSize(DisplayUtil.sp2px(30f))
        mCenterPaint.setColor(ContextCompat.getColor(context, R.color.lightGray))
        mSamllDegreePaint = Paint()
        mSamllDegreePaint.setStyle(Paint.Style.FILL)
        mSamllDegreePaint.setAntiAlias(true)
        mSamllDegreePaint.setTextSize(DisplayUtil.sp2px(13f))
        mSamllDegreePaint.setColor(ContextCompat.getColor(context, R.color.lightGray))
        mSencondRect = Rect()
        mThirdRect = Rect()
        mInnerPaint = Paint()
        mInnerPaint.setStyle(Paint.Style.FILL)
        mInnerPaint.setAntiAlias(true)
        mAnglePaint = Paint()
        mAnglePaint.setStyle(Paint.Style.STROKE)
        mAnglePaint.setAntiAlias(true)
        mAnglePaint.setColor(ContextCompat.getColor(context, R.color.red))
        mCameraMatrix = Matrix()
        mCamera = Camera()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvas = canvas
        //设置Camera矩阵 实现3D效果
        set3DMetrix()
        //画文字
        drawText()
        //画指南针外圈
        drawCompassOutSide()
        //画指南针外接圆
        drawCompassCircum()
        //画内部渐变颜色圆
        drawInnerCricle()
        //画指南针内部刻度
        drawCompassDegreeScale()
        //画圆心数字
        drawCenterText()
    }

    /**
     * 设置camera相关
     */
    private fun set3DMetrix() {
        mCameraMatrix.reset()
        mCamera.save()
        mCamera.rotateX(mCameraRotateX)
        mCamera.rotateY(mCameraRotateY)
        mCamera.getMatrix(mCameraMatrix)
        mCamera.restore()
        //camera默认旋转是View左上角为旋转中心
        //所以动作之前要，设置矩阵位置 -mTextHeight-mOutSideRadius
        mCameraMatrix.preTranslate(-getWidth() / 2f, -height / 2f)
        //动作之后恢复位置
        mCameraMatrix.postTranslate(getWidth() / 2f, height / 2f)
        if (mCanvas != null)
            mCanvas!!.concat(mCameraMatrix)
    }

    private fun drawInnerCricle() {
        mInnerShader = RadialGradient(
            width1 / 2f,
            mOutSideRadius + mTextHeight,
            mCircumRadius - 40f,
            ContextCompat.getColor(context, R.color.color_323232),
            ContextCompat.getColor(context, R.color.black),
            Shader.TileMode.CLAMP
        )
        mInnerPaint.setShader(mInnerShader)
        if (mCanvas != null)
            mCanvas!!.drawCircle(
                width1 / 2f,
                mOutSideRadius + mTextHeight,
                mCircumRadius - 40,
                mInnerPaint
            )
    }

    private fun drawCenterText() {
        val centerText = StringBuilder().append(val1.toInt()).append("°")
        mCenterPaint.getTextBounds(centerText.toString(), 0, centerText.length, mCenterTextRect)
        val centerTextWidth: Int = mCenterTextRect.width()
        val centerTextHeight: Int = mCenterTextRect.height()
        if (mCanvas != null)
            mCanvas!!.drawText(
                centerText.toString(),
                width1 / 2f - centerTextWidth / 2f,
                mTextHeight + mOutSideRadius + centerTextHeight / 5f,
                mCenterPaint
            )
    }

    private fun drawCompassDegreeScale() {
        if (mCanvas != null)
            mCanvas!!.save()
        //获取N文字的宽度
        mNorthPaint.getTextBounds("N", 0, 1, mPositionRect)
        val mPositionTextWidth: Int = mPositionRect.width()
        val mPositionTextHeight: Int = mPositionRect.height()
        //获取W文字宽度,因为W比较宽 所以要单独获取
        mNorthPaint.getTextBounds("W", 0, 1, mPositionRect)
        val mWPositionTextWidth: Int = mPositionRect.width()
        val mWPositionTextHeight: Int = mPositionRect.height()
        //获取小刻度，两位数的宽度
        mSamllDegreePaint.getTextBounds("30", 0, 1, mSencondRect)
        val mSencondTextWidth: Int = mSencondRect.width()
        val mSencondTextHeight: Int = mSencondRect.height()
        //获取小刻度，3位数的宽度
        mSamllDegreePaint.getTextBounds("30", 0, 1, mThirdRect)
        val mThirdTextWidth: Int = mThirdRect.width()
        val mThirdTextHeight: Int = mThirdRect.height()
        if (mCanvas != null)
            mCanvas!!.rotate(-val1, width1 / 2f, mOutSideRadius + mTextHeight)

        //画刻度线
        for (i in 0..239) {
            if (i == 0 || i == 60 || i == 120 || i == 180) {
                if (mCanvas != null)
                    mCanvas!!.drawLine(
                        getWidth() / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 10f,
                        getWidth() / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 30f,
                        mDeepGrayPaint
                    )
            } else {
                if (mCanvas != null)
                    mCanvas!!.drawLine(
                        getWidth() / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 10f,
                        getWidth() / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 30f,
                        mLightGrayPaint
                    )
            }
            if (i == 0) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "N",
                        width1 / 2f - mPositionTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mPositionTextHeight,
                        mNorthPaint
                    )
            } else if (i == 60) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "E",
                        width1 / 2f - mPositionTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mPositionTextHeight,
                        mOthersPaint
                    )
            } else if (i == 120) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "S",
                        width1 / 2f - mPositionTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mPositionTextHeight,
                        mSouthPaint
                    )
            } else if (i == 180) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "W",
                        width1 / 2f - mWPositionTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mWPositionTextHeight,
                        mOthersPaint
                    )
            } else if (i == 20) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "30",
                        width1 / 2f - mSencondTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mSencondTextHeight,
                        mSamllDegreePaint
                    )
            } else if (i == 40) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "60",
                        width1 / 2f - mSencondTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mSencondTextHeight,
                        mSamllDegreePaint
                    )
            } else if (i == 80) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "120",
                        width1 / 2f - mThirdTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mThirdTextHeight,
                        mSamllDegreePaint
                    )
            } else if (i == 100) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "150",
                        width1 / 2f - mThirdTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mThirdTextHeight,
                        mSamllDegreePaint
                    )
            } else if (i == 140) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "210",
                        width1 / 2f - mThirdTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mThirdTextHeight,
                        mSamllDegreePaint
                    )
            } else if (i == 160) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "240",
                        width1 / 2f - mThirdTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mThirdTextHeight,
                        mSamllDegreePaint
                    )
            } else if (i == 200) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "300",
                        width1 / 2f - mThirdTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mThirdTextHeight,
                        mSamllDegreePaint
                    )
            } else if (i == 220) {
                if (mCanvas != null)
                    mCanvas!!.drawText(
                        "330",
                        width1 / 2f - mThirdTextWidth / 2f,
                        mTextHeight + mOutSideRadius - mCircumRadius + 40 + mThirdTextHeight,
                        mSamllDegreePaint
                    )
            }
            if (mCanvas != null)
                mCanvas!!.rotate(1.5f, mCenterX, mOutSideRadius + mTextHeight)
        }
        if (mCanvas != null)
            mCanvas!!.restore()
    }

    /**
     * 指南针外接圆，和外部圆换道理差不多
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun drawCompassCircum() {
        if (mCanvas != null)
            mCanvas!!.save()
        //外接圆小三角形的高度
        val mTriangleHeight = (mOutSideRadius - mCircumRadius) / 2
        if (mCanvas != null)
            mCanvas!!.rotate(-val1, width1 / 2f, mOutSideRadius + mTextHeight)
        mCircumTriangle.moveTo(width1 / 2f, mTriangleHeight + mTextHeight)
        //内接三角形的边长,简单数学运算
        val mTriangleSide = (mTriangleHeight / Math.sqrt(3.0) * 2f).toFloat()
        mCircumTriangle.lineTo(width1 / 2f - mTriangleSide / 2f, mTextHeight + mTriangleHeight * 2f)
        mCircumTriangle.lineTo(width1 / 2f + mTriangleSide / 2f, mTextHeight + mTriangleHeight * 2f)
        mCircumTriangle.close()
        if (mCanvas != null)
            mCanvas!!.drawPath(mCircumTriangle, mCircumPaint)
        if (mCanvas != null)
            mCanvas!!.drawArc(
                width1 / 2f - mCircumRadius,
                mTextHeight + mOutSideRadius - mCircumRadius,
                width1 / 2f + mCircumRadius,
                mTextHeight + mOutSideRadius + mCircumRadius,
                -85f,
                350f,
                false,
                mDeepGrayPaint
            )
        mAnglePaint.setStrokeWidth(5f)
        if (val1 <= 180) {
            valCompare = val1
            if (mCanvas != null)
                mCanvas!!.drawArc(
                    width1 / 2f - mCircumRadius,
                    mTextHeight + mOutSideRadius - mCircumRadius,
                    width1 / 2f + mCircumRadius,
                    mTextHeight + mOutSideRadius + mCircumRadius,
                    -85f,
                    valCompare,
                    false,
                    mAnglePaint
                )
        } else {
            valCompare = 360 - val1
            if (mCanvas != null)
                mCanvas!!.drawArc(
                    width1 / 2f - mCircumRadius,
                    mTextHeight + mOutSideRadius - mCircumRadius,
                    width1 / 2f + mCircumRadius,
                    mTextHeight + mOutSideRadius + mCircumRadius,
                    -95f,
                    -valCompare,
                    false,
                    mAnglePaint
                )
        }
        if (mCanvas != null)
            mCanvas!!.restore()
    }

    /**
     * 指南针外部可简单分为两部分
     * 1、用Path实现小三角形
     * 2、两个圆弧
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun drawCompassOutSide() {
        if (mCanvas != null)
            mCanvas!!.save()
        //小三角形的高度
        val mTriangleHeight = 40
        //定义Path画小三角形
        mOutsideTriangle.moveTo(width1 / 2f, mTextHeight - mTriangleHeight)
        //小三角形的边长
        val mTriangleSide = 46.18f
        //画出小三角形
        mOutsideTriangle.lineTo(width1 / 2 - mTriangleSide / 2, mTextHeight)
        mOutsideTriangle.lineTo(width1 / 2 + mTriangleSide / 2, mTextHeight)
        mOutsideTriangle.close()
        if (mCanvas != null)
            mCanvas!!.drawPath(mOutsideTriangle, mOutSideCircumPaint)

        //画圆弧
        mDarkRedPaint.setStrokeWidth(5f)
        mLightGrayPaint.setStrokeWidth(3f)
        mDeepGrayPaint.setStrokeWidth(3f)
        mLightGrayPaint.setStyle(Paint.Style.STROKE)
        if (mCanvas != null)
            mCanvas!!.drawArc(
                width1 / 2 - mOutSideRadius,
                mTextHeight,
                width1 / 2 + mOutSideRadius,
                mTextHeight + mOutSideRadius * 2,
                -80f,
                120f,
                false,
                mLightGrayPaint
            )
        if (mCanvas != null)
            mCanvas!!.drawArc(
                width1 / 2 - mOutSideRadius,
                mTextHeight,
                width1 / 2 + mOutSideRadius,
                mTextHeight + mOutSideRadius * 2,
                40f,
                20f,
                false,
                mDeepGrayPaint
            )
        if (mCanvas != null)
            mCanvas!!.drawArc(
                width1 / 2 - mOutSideRadius,
                mTextHeight,
                width1 / 2 + mOutSideRadius,
                mTextHeight + mOutSideRadius * 2,
                -100f,
                -20f,
                false,
                mLightGrayPaint
            )
        if (mCanvas != null)
            mCanvas!!.drawArc(
                width1 / 2 - mOutSideRadius,
                mTextHeight,
                width1 / 2 + mOutSideRadius,
                mTextHeight + mOutSideRadius * 2,
                -120f,
                -120f,
                false,
                mDarkRedPaint
            )
        if (mCanvas != null)
            mCanvas!!.restore()
    }

    private fun drawText() {
        if (val1 <= 15 || val1 >= 345) {
            text = "北"
        } else if (val1 > 15 && val1 <= 75) {
            text = "东北"
        } else if (val1 > 75 && val1 <= 105) {
            text = "东"
        } else if (val1 > 105 && val1 <= 165) {
            text = "东南"
        } else if (val1 > 165 && val1 <= 195) {
            text = "南"
        } else if (val1 > 195 && val1 <= 255) {
            text = "西南"
        } else if (val1 > 255 && val1 <= 285) {
            text = "西"
        } else if (val1 > 285 && val1 < 345) {
            text = "西北"
        }
        mTextPaint.getTextBounds(text, 0, text.length, mTextRect)
        //文字宽度
        val mTextWidth: Int = mTextRect.width()
        //让文字水平居中显示
        if (mCanvas != null)
            mCanvas!!.drawText(text, width1 / 2f - mTextWidth / 2f, mTextHeight / 2, mTextPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        width1 = Math.min(widthSize, heightSize)
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width1 = heightSize
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width1 = widthSize
        }
        //为指南针上面的文字预留空间，定为1/3边张
        mTextHeight = width1 / 3f
        //设置圆心点坐标
        mCenterX = width1 / 2f
        mCenterY = width1 / 2f + mTextHeight
        //外部圆的外径
        mOutSideRadius = width1 * 3f / 8f
        //外接圆的半径
        mCircumRadius = mOutSideRadius * 4f / 5f
        //camera最大平移距离
        mMaxCameraTranslate = 0.02f * mOutSideRadius
        setMeasuredDimension(width1, width1 + width1 / 3)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mValueAnimator != null && mValueAnimator!!.isRunning) {
                    mValueAnimator!!.cancel()
                }
                //3D 效果让Camera旋转,获取旋转偏移大小
                getCameraRotate(event)
                //获取平移大小
                getCameraTranslate(event)
            }
            MotionEvent.ACTION_MOVE -> {
                //3D 效果让Camera旋转,获取旋转偏移大小
                getCameraRotate(event)
                //获取平移大小
                getCameraTranslate(event)
            }
            MotionEvent.ACTION_UP ->             //松开手 复原动画
                startRestore()
        }
        return true
    }

    private fun startRestore() {
        val cameraRotateXName = "cameraRotateX"
        val cameraRotateYName = "cameraRotateY"
        val canvasTranslateXName = "canvasTranslateX"
        val canvasTranslateYName = "canvasTranslateY"
        val cameraRotateXHolder =
            PropertyValuesHolder.ofFloat(cameraRotateXName, mCameraRotateX, 0f)
        val cameraRotateYHolder =
            PropertyValuesHolder.ofFloat(cameraRotateYName, mCameraRotateY, 0f)
        val canvasTranslateXHolder =
            PropertyValuesHolder.ofFloat(canvasTranslateXName, mCameraTranslateX, 0f)
        val canvasTranslateYHolder =
            PropertyValuesHolder.ofFloat(canvasTranslateYName, mCameraTranslateY, 0f)
        mValueAnimator = ValueAnimator.ofPropertyValuesHolder(
            cameraRotateXHolder,
            cameraRotateYHolder, canvasTranslateXHolder, canvasTranslateYHolder
        )
        mValueAnimator!!.setInterpolator(TimeInterpolator { input ->
            val f = 0.571429f
            (Math.pow(
                2.0,
                (-2 * input).toDouble()
            ) * Math.sin((input - f / 4) * (2 * Math.PI) / f) + 1).toFloat()
        })
        mValueAnimator!!.setDuration(1000)
        mValueAnimator!!.addUpdateListener(AnimatorUpdateListener { animation ->
            mCameraRotateX = animation.getAnimatedValue(cameraRotateXName) as Float
            mCameraRotateY = animation.getAnimatedValue(cameraRotateYName) as Float
            mCameraTranslateX = animation.getAnimatedValue(canvasTranslateXName) as Float
            mCameraTranslateX = animation.getAnimatedValue(canvasTranslateYName) as Float
        })
        mValueAnimator!!.start()
    }

    /**
     * 获取Camera，平移大小
     *
     * @param event
     */
    private fun getCameraTranslate(event: MotionEvent) {
        val translateX = event.x - getWidth() / 2
        val translateY = event.y - height / 2
        //求出此时位移的大小与半径之比
        val percentArr = getPercent(translateX, translateY)
        //最终位移的大小按比例匀称改变
        mCameraTranslateX = percentArr[0] * mMaxCameraTranslate
        mCameraTranslateY = percentArr[1] * mMaxCameraTranslate
    }

    /**
     * 让Camera旋转,获取旋转偏移大小
     *
     * @param event
     */
    private fun getCameraRotate(event: MotionEvent) {
        val mRotateX = -(event.y - height / 2)
        val mRotateY = event.x - getWidth() / 2
        //求出旋转大小与半径之比
        val percentArr = getPercent(mRotateX, mRotateY)
        mCameraRotateX = percentArr[0] * mMaxCameraRotate
        mCameraRotateY = percentArr[1] * mMaxCameraRotate
    }

    /**
     * 获取比例
     *
     * @param mCameraRotateX
     * @param mCameraRotateY
     * @return
     */
    private fun getPercent(mCameraRotateX: Float, mCameraRotateY: Float): FloatArray {
        val percentArr = FloatArray(2)
        var percentX = mCameraRotateX / width1
        var percentY = mCameraRotateY / width1
        //处理一下比例值
        if (percentX > 1) {
            percentX = 1f
        } else if (percentX < -1) {
            percentX = -1f
        }
        if (percentY > 1) {
            percentY = 1f
        } else if (percentY < -1) {
            percentY = -1f
        }
        percentArr[0] = percentX
        percentArr[1] = percentY
        return percentArr
    }
}
