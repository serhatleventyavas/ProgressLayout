package com.serhatleventyavas.progresslayout

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View

class ProgressLayout: View, Animatable {

    private var runnableProgress: Runnable = Runnable {
        runnableProgressFunc()
    }

    var progressLayoutListener: ProgressLayoutListener? = null
    private var pathProgress = Path()
    private val pathEmpty = Path()
    private val rectProgress = RectF()
    private val rectEmpty = RectF()

    private val COLOR_EMPTY_DEFAULT: Int = 0x00000000
    private val COLOR_PROGRESS_DEFAULT: Int = 0x11FFFFFF
    private val PROGRESS_SECOND_MS: Int = 1000

    private var paintProgressLoaded: Paint = Paint()
    private var paintProgressEmpty: Paint = Paint()

    private var emptyColor: Int = COLOR_EMPTY_DEFAULT

    var progressColor: Int = COLOR_PROGRESS_DEFAULT
        set(value) {
            field = value
            paintProgressLoaded.color = field
            postInvalidate()
        }

    var isPlaying: Boolean = false
        private set

    private var isAutoProgress: Boolean = false
    var radius: Float = 0f
    private var mHeight: Float = 0f
    private var mWidth: Float = 0f

    var maxProgress: Int = 100
        set(value) {
            field = value * 10
            postInvalidate()
        }

    var currentProgress: Int = 0

    private var handlerProgress: Handler = Handler()

    constructor(context: Context): super(context) {
        init(context, null)
    }
    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init(context, attr)
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr) {
        init(context, attr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attr, defStyleAttr, defStyleRes) {
        init(context, attr)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attr: AttributeSet?) {
        setWillNotDraw(false)
        val typeArray: TypedArray = context.obtainStyledAttributes(attr, R.styleable.progressLayout)
        isAutoProgress = typeArray.getBoolean(R.styleable.progressLayout_pl_autoProgress, true)
        maxProgress =  typeArray.getInt(R.styleable.progressLayout_pl_maxProgress, 100)
        radius = typeArray.getDimension(R.styleable.progressLayout_pl_radius, 0.0f)
        emptyColor = typeArray.getColor(R.styleable.progressLayout_pl_emptyColor, COLOR_EMPTY_DEFAULT)
        progressColor = typeArray.getColor(R.styleable.progressLayout_pl_progressColor, COLOR_PROGRESS_DEFAULT)
        typeArray.recycle()

        paintProgressEmpty.color = emptyColor
        paintProgressEmpty.style = Paint.Style.FILL
        paintProgressEmpty.isAntiAlias = true

        paintProgressLoaded.color = progressColor
        paintProgressLoaded.style = Paint.Style.FILL
        paintProgressLoaded.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        paintProgressLoaded.strokeWidth = mWidth / 1000
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        rectProgress.set(0f, 0f, calculatePositionIndex(currentProgress), mHeight)
        rectEmpty.set(calculatePositionIndex(currentProgress) / 2, 0.0f, mWidth, mHeight)

        pathProgress.reset()
        pathProgress.addRoundRect(rectProgress, calculateCornersOfProgress(), Path.Direction.CW)

        pathEmpty.reset()
        pathEmpty.addRoundRect(rectEmpty, calculateCornersOfEmpty(), Path.Direction.CW)


        canvas?.drawPath(pathEmpty, paintProgressEmpty)
        canvas?.drawPath(pathProgress, paintProgressLoaded)
    }

    private fun calculateCornersOfProgress(): FloatArray {
        return floatArrayOf(
            radius, radius,   // Top left radius in px
            radius, radius,   // Top right radius in px
            radius, radius,     // Bottom right radius in px
            radius, radius      // Bottom left radius in px
        )
    }

    private fun calculateCornersOfEmpty(): FloatArray {
        if (currentProgress > 0) {
            return floatArrayOf(
                0f, 0f,
                radius, radius,
                radius, radius,
                0f, 0f
            )
        }
        return floatArrayOf(
            radius, radius,
            radius, radius,
            radius, radius,
            radius, radius
        )
    }

    private fun calculatePositionIndex(currentProgress: Int): Float {
        return (currentProgress * mWidth) / maxProgress
    }

    fun setProgress(currentProgress: Int) {
        this.currentProgress = currentProgress * 10
        postInvalidate()
    }

    fun cancel() {
        isPlaying = false
        currentProgress = 0
        handlerProgress.removeCallbacks(runnableProgress)
        postInvalidate()
    }

    fun setAutoProgress(isAutoProgress: Boolean) {
        this.isAutoProgress = isAutoProgress
    }

    private fun runnableProgressFunc() {
        if (isPlaying) {
            if (currentProgress == maxProgress) {
                progressLayoutListener?.onProgressCompleted()
                //currentProgress = 0
                stop()
            } else {
                postInvalidate()
                currentProgress += 1
                progressLayoutListener?.onProgressChanged(currentProgress / 10)
                handlerProgress.postDelayed(runnableProgress, (PROGRESS_SECOND_MS  / 10).toLong())
            }
        }
    }

    override fun isRunning(): Boolean {
        return isPlaying
    }

    override fun start() {
        if (isAutoProgress) {
            isPlaying = true
            handlerProgress.removeCallbacksAndMessages(null)
            handlerProgress.postDelayed(runnableProgress, 0)
        }
    }

    override fun stop() {
        isPlaying = false
        handlerProgress.removeCallbacksAndMessages(runnableProgress)
        postInvalidate()
    }
}