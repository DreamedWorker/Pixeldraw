package com.dream.pixeldraw.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dream.pixeldraw.AppGlobalData.MA_INSTANCE
import com.dream.pixeldraw.AppGlobalData.Plates
import com.dream.pixeldraw.AppGlobalData.Saved_Plates
import com.dream.pixeldraw.AppGlobalData.copied_pic
import java.util.*


open class PixelPicView : View {
    constructor(ctx: Context?) : super(ctx) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    private val canvas = Canvas()
    var mWidthPixels = 16
    var mHeightPixels = 16
    private var Plate = arrayOf(intArrayOf(0))
    private var selected = Array(16) { BooleanArray(16) }
    private var isWeb = false
    internal var isSelected: Boolean = false
    internal var background: Drawable? = null
    var mod = 0

    abstract class OnPixelClickListener {
        open fun onClick(view: View?, x: Int, y: Int) {}
    }

    abstract class OnPixelTouchListener {
        open fun onTouch(view: View?, motionEvent: MotionEvent?, x: Int, y: Int){}
    }

    protected fun updateSize() {
        Plate = Array(mWidthPixels) { IntArray(mHeightPixels) }
        selected = Array(mWidthPixels) { BooleanArray(mHeightPixels) }
        for (i in Plate.indices) for (i1 in Plate[i].indices) Plate[i][i1] = Color.TRANSPARENT
        val l = layoutParams as FrameLayout.LayoutParams
        l.width = MA_INSTANCE.dip2px(300f)
        l.height =
            (MA_INSTANCE.dip2px(300f) * (mHeightPixels.toFloat() / mWidthPixels.toFloat())).toInt()
        l.gravity = Gravity.CENTER
        x = (MA_INSTANCE.displayMetrics.widthPixels / 2 - measuredWidth / 2).toFloat()
        y = (MA_INSTANCE.displayMetrics.heightPixels / 2 - measuredHeight / 2).toFloat()
        scaleX = 1f
        scaleY = 1f
        layoutParams = l
        updateCanvas()
    }

    open fun updateCanvas() {
        invalidate()
    }

    override fun invalidate() {
        super.invalidate()
    }

    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        val mpaint2 = Paint()
        mpaint2.isAntiAlias = false
        mpaint2.color = Color.argb(0.5f, 1f, 1f, 1f)
        for (i in Plate.indices) for (i1 in Plate[i].indices) {
            val mpaint = Paint()
            mpaint.isAntiAlias = false
            mpaint.color = Plate[i][i1]
            canvas.drawRect(
                (this.measuredWidth / Plate.size * i).toFloat(),
                (this.measuredHeight / Plate[0].size * i1).toFloat(),
                (this.measuredWidth / Plate.size * (i + 1)).toFloat(),
                (this.measuredHeight / Plate[0].size * (i1 + 1)).toFloat(),
                mpaint
            )
            if (isSelected) {
                val paint = Paint()
                paint.style = Paint.Style.STROKE
                paint.color = Color.argb(0.5f, 1f, 1f, 1f)
                paint.strokeWidth = 8f
                paint.pathEffect = DashPathEffect(floatArrayOf(18f, 8f), 0f)
                if (selected[i][i1]) canvas.drawRect(
                    (this.measuredWidth / Plate.size * i).toFloat(),
                    (this.measuredHeight / Plate[0].size * i1).toFloat(),
                    (this.measuredWidth / Plate.size * (i + 1)).toFloat(),
                    (this.measuredHeight / Plate[0].size * (i1 + 1)).toFloat(),
                    paint
                )
            }
        }
        if (isWeb) {
            for (i in 1 until Plate.size) canvas.drawRect(
                (this.measuredWidth / Plate.size * i).toFloat(),
                0f,
                this.measuredWidth / Plate.size * i + measuredWidth / getWidthPixels() * 0.05f,
                this.measuredHeight.toFloat(),
                mpaint2
            )
            for (i in 1 until Plate[0].size) canvas.drawRect(
                0f,
                this.measuredHeight / Plate[0].size * i - measuredHeight / getHeightPixels() * 0.05f,
                measuredWidth.toFloat(),
                (this.measuredHeight / Plate[0].size * i).toFloat(),
                mpaint2
            )
        }
        val paint = Paint()
        paint.color = Color.argb(0.5f, 1f, 1f, 1f)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawRect(0f, 0f, this.measuredWidth.toFloat(), this.measuredHeight.toFloat(), paint)
        super.onDraw(canvas)
    }

    open fun enableWeb(b: Boolean) {
        if (isWeb != b) {
            isWeb = b
            Log.d("c", "" + b)
            updateCanvas()
        }
    }

    open fun setWidthPixels(count: Int) {
        mWidthPixels = count
        updateSize()
    }

    open fun setHeightPixels(count: Int) {
        mHeightPixels = count
        updateSize()
    }

    open fun getWidthPixels(): Int {
        return mWidthPixels
    }

    open fun getHeightPixels(): Int {
        return mHeightPixels
    }

    open operator fun set(x: Int, y: Int, value: Int) {
        if (x >= 0 && x < mWidthPixels && y >= 0 && y < mHeightPixels) if (Plate[x][y] != value) Plate[x][y] =
            value
        invalidate()
    }

    open operator fun set(x: Float, y: Float, value: Int) {
        if (x >= 0 && x < mWidthPixels && y >= 0 && y < mHeightPixels) if (Plate[x.toInt()][y.toInt()] != value) Plate[x.toInt()][y.toInt()] =
            value
        invalidate()
    }

    open fun drawBitmapAt(x: Int, y: Int, bitmap: Bitmap) {
        for (i in x until x + bitmap.width) for (ii in y until y + bitmap.height) {
            if (i >= 0 && i < getWidthPixels()) if (ii >= 0 && ii < getHeightPixels()) if (bitmap.getPixel(
                    i - x,
                    ii - y
                ) == Color.TRANSPARENT
            ) {
                set(i, ii, bitmap.getPixel(i - x, ii - y))
            }
        }
    }

    open fun selectPixel(x: Int, y: Int) {
        if (x >= 0 && x < mWidthPixels && y >= 0 && y < mHeightPixels) selected[x][y] = true
    }

    open fun renderSelectedPixels() {
        isSelected = true
        updateCanvas()
    }

    open fun cleanSelectedPixels() {
        isSelected = false
        selected = Array(mWidthPixels) { BooleanArray(mHeightPixels) }
        updateCanvas()
    }

    open fun selectRectPixel(x: Int, y: Int, x_end: Int, y_end: Int) {
        for (i in x until x_end) for (ii in y until y_end) selectPixel(i, ii)
    }

    open operator fun get(x: Int, y: Int): Int {
        return if (x < 0 || y < 0 || x > mWidthPixels || y > mHeightPixels) 0 else Plate[x][y]
    }

    open fun loadHistoryBitmap() {
        Plates.add(getBitmap())
        if (MA_INSTANCE.undoButton.visibility == VISIBLE) {
            MA_INSTANCE.undoButton.visibility = INVISIBLE
        }
        Saved_Plates = ArrayList<Bitmap>()
        MA_INSTANCE.returnButton.visibility = VISIBLE
        if (Saved_Plates.size != 0) {
            Saved_Plates = ArrayList<Bitmap>()
        }
    }

    open fun sliceAsBitmap(x: Int, y: Int, end_x: Int, end_y: Int): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(Math.abs(x - end_x), Math.abs(y - end_y), Bitmap.Config.ARGB_8888)
        for (ii in 0 until Math.abs(x - end_x)) for (ii1 in 0 until Math.abs(y - end_y)) {
            bitmap.setPixel(ii, ii1, get(x + ii, y + ii1))
        }
        return bitmap
    }

    open fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(mWidthPixels, mHeightPixels, Bitmap.Config.ARGB_8888)
        for (ii in 0 until bitmap.width) for (ii1 in 0 until bitmap.height) {
            bitmap.setPixel(ii, ii1, get(ii, ii1))
        }
        return bitmap
    }

    open fun setInitBitmap(bitmap: Bitmap) {
        isWeb = false
        setWidthPixels(bitmap.width)
        setHeightPixels(bitmap.height)
        Log.d("create", bitmap.width.toString() + "," + Plate[1].size)
        updateSize()
        for (i in 0 until bitmap.width) for (i1 in 0 until bitmap.height) {
            set(i, i1, bitmap.getPixel(i, i1))
        }
    }

    open fun updateBitmap(bitmap: Bitmap) {
        for (i in 0 until bitmap.width) for (i1 in 0 until bitmap.height) {
            set(i, i1, bitmap.getPixel(i, i1))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    open fun setOnPixelClickListener(pixelClickListener: OnPixelClickListener?) {
        if (pixelClickListener != null) {
            setOnTouchListener { _, motionEvent ->
                val instance = this@PixelPicView
                motionEvent.recycle()
                if (motionEvent.action == MotionEvent.ACTION_BUTTON_PRESS) {
                    for (i in Plate.indices) for (i1 in 0 until Plate[i].size) {
                        if (motionEvent.x > i * (measuredWidth / getWidthPixels()) && motionEvent.x < (i + 1) * (measuredWidth / getWidthPixels()) && motionEvent.y > i1 * (measuredHeight / getHeightPixels()) && motionEvent.y < (i1 + 1) * (measuredHeight / getWidthPixels())
                        ) {
                            pixelClickListener.onClick(instance, i, i1)
                        }
                    }
                }
                true
            }
        } else setOnTouchListener(null)
    }

    @SuppressLint("ClickableViewAccessibility")
    open fun setOnPixelTouchListener(pixelTouchListener: OnPixelTouchListener?) {
        if (pixelTouchListener != null) {
            setOnTouchListener(object : OnTouchListener {
                var down: Long = 0
                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                    val instance: PixelPicView = this@PixelPicView
                    for (i in Plate.indices) for (i1 in Plate[i].indices) {
                        if (motionEvent.x > i * (measuredWidth / getWidthPixels()) && motionEvent.x < (i + 1) * (measuredWidth / getWidthPixels()) && motionEvent.y > i1 * (measuredHeight / getHeightPixels()) && motionEvent.y < (i1 + 1) * (measuredHeight / getHeightPixels())) {
                            pixelTouchListener.onTouch(instance, motionEvent, i, i1)
                            if (motionEvent.action == MotionEvent.ACTION_DOWN) down = Date().time
                            if (motionEvent.action == MotionEvent.ACTION_UP) if (Date().time - down >= 400 && copied_pic != null) {
                                Toast.makeText(context, "已粘贴", Toast.LENGTH_SHORT).show()
                                drawBitmapAt(i, i1, copied_pic!!)
                            }
                        }
                    }
                    return true
                }
            })
        } else setOnTouchListener { _, _ ->
            setOnTouchListener(object : OnTouchListener {
                var down: Long = 0
                override fun onTouch(
                    view: View,
                    motionEvent: MotionEvent
                ): Boolean {
                    for (i in Plate.indices) for (i1 in Plate[i].indices) {
                        if (motionEvent.x > i * (measuredWidth / getWidthPixels()) && motionEvent.x < (i + 1) * (measuredWidth / getWidthPixels()) && motionEvent.y > i1 * (measuredHeight / getHeightPixels()) && motionEvent.y < (i1 + 1) * (measuredHeight / getHeightPixels())
                        ) {
                            if (motionEvent.action == MotionEvent.ACTION_DOWN) down =
                                Date().time
                            if (motionEvent.action == MotionEvent.ACTION_UP) if (Date().time - down >= 400 && copied_pic != null) {
                                Toast.makeText(context, "已粘贴", Toast.LENGTH_SHORT).show()
                                drawBitmapAt(i, i1, copied_pic!!)
                            }
                        }
                    }
                    return true
                }
            })
            true
        }
    }
}