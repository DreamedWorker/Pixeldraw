package com.dream.pixeldraw.adapter

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.dream.pixeldraw.AppGlobalData
import com.dream.pixeldraw.R
import com.dream.pixeldraw.activity.WorkActivity
import com.dream.pixeldraw.view.PixelPicView.OnPixelTouchListener
import kotlin.math.*

object Listeners {
    var selectEditWin: PopupWindow? = null
    var hasSelected = false
    lateinit var select_start: IntArray
    lateinit var select_end: IntArray

    fun getGraphToolOnClickListener(v: View, graph_id: Int): View.OnClickListener {
        return View.OnClickListener {
            AppGlobalData.MA_INSTANCE.graphTools[graph_id] =
                !AppGlobalData.MA_INSTANCE.graphTools[graph_id]
            if (!AppGlobalData.MA_INSTANCE.graphTools[graph_id]) {
                resetListenersForGraphTools()
                v.setBackgroundResource(R.drawable.shape_button_selected)
                when (graph_id) {
                    0 -> AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(LineListener)
                    1 -> AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(SquareListener)
                    2 -> AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(SquareHolListener)
                    3 -> AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(CircleListener)
                    4 -> AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(CircleHolListener)
                    else -> {}
                }
            } else {
                v.setBackgroundResource(R.drawable.shape_sel)
                AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(null)
                AppGlobalData.MA_INSTANCE.pic.setOnPixelClickListener(null)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private var LineListener: OnPixelTouchListener = object : OnPixelTouchListener() {
        private var x_0 = 0
        private var y_0 = 0
        private var last_bmp: Bitmap? = null
        override fun onTouch(view: View?, motionEvent: MotionEvent?, x: Int, y: Int) {
            if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
                AppGlobalData.MA_INSTANCE.pic.loadHistoryBitmap()
                last_bmp = AppGlobalData.MA_INSTANCE.pic.getBitmap()
                x_0 = x
                y_0 = y
            }
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                AppGlobalData.MA_INSTANCE.pic.updateBitmap(last_bmp!!)
                val length = sqrt(
                    ((if (x_0 - x == 0) 1 else x_0 - x.toDouble()) as Double).pow(2.0) +
                            ((if (y_0 - y == 0) 1 else y_0 - y.toDouble()) as Double).pow(2.0)).toFloat()
                var th = 0f
                while (th <= length) {
                    AppGlobalData.MA_INSTANCE.pic[x_0 + (x - x_0) * th / length, y_0 + (y - y_0) * th / length] =
                        WorkActivity().penColor
                    th += 1f
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                x_0 = 0
                y_0 = 0
                last_bmp = null
            }
            super.onTouch(view, motionEvent, x, y)
        }
    }

    private var SquareListener: OnPixelTouchListener = object : OnPixelTouchListener() {
        private var x_0 = 0
        private var y_0 = 0
        private var last_bmp: Bitmap? = null

        @TargetApi(Build.VERSION_CODES.O)
        override fun onTouch(view: View?, motionEvent: MotionEvent?, x: Int, y: Int) {
            if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
                AppGlobalData.MA_INSTANCE.pic.loadHistoryBitmap()
                last_bmp = AppGlobalData.MA_INSTANCE.pic.getBitmap()
                x_0 = x
                y_0 = y
            }
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                AppGlobalData.MA_INSTANCE.pic.updateBitmap(last_bmp!!)
                val s = if (x_0 - x > 0) 1 else -1
                val st = if (y_0 - y > 0) 1 else -1
                for (i in 0 until abs(x_0 - x) + 1) for (i1 in 0 until abs(y_0 - y) + 1) {
                    AppGlobalData.MA_INSTANCE.pic[x_0 - s * i, y_0 - st * i1] =
                        WorkActivity().penColor
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                x_0 = 0
                y_0 = 0
                last_bmp = null
            }
            super.onTouch(view, motionEvent, x, y)
        }
    }

    private var SquareHolListener: OnPixelTouchListener = object : OnPixelTouchListener() {
        private var x_0 = 0
        private var y_0 = 0
        private var last_bmp: Bitmap? = null

        @TargetApi(Build.VERSION_CODES.O)
        override fun onTouch(view: View?, motionEvent: MotionEvent?, x: Int, y: Int) {
            if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
                AppGlobalData.MA_INSTANCE.pic.loadHistoryBitmap()
                last_bmp = AppGlobalData.MA_INSTANCE.pic.getBitmap()
                x_0 = x
                y_0 = y
            }
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                last_bmp?.let { AppGlobalData.MA_INSTANCE.pic.updateBitmap(it) }
                val s = if (x_0 - x > 0) 1 else -1
                val st = if (y_0 - y > 0) 1 else -1
                for (i in 0 until abs(x_0 - x)) {
                    AppGlobalData.MA_INSTANCE.pic[x_0 - i * s, y_0] = WorkActivity().penColor
                    AppGlobalData.MA_INSTANCE.pic[x_0 - i * s, y] = WorkActivity().penColor
                }
                for (i in 0 until abs(y_0 - y)) AppGlobalData.MA_INSTANCE.pic[x_0, y_0 - i * st] =
                    WorkActivity().penColor
                for (i in 0 until abs(y_0 - y) + 1) AppGlobalData.MA_INSTANCE.pic[x, y_0 - i * st] =
                    WorkActivity().penColor
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                x_0 = 0
                y_0 = 0
                last_bmp = null
            }
            super.onTouch(view, motionEvent, x, y)
        }
    }

    private var CircleHolListener: OnPixelTouchListener = object : OnPixelTouchListener() {
        private var x_0 = 0
        private var y_0 = 0
        private var last_bmp: Bitmap? = null

        @TargetApi(Build.VERSION_CODES.O)
        override fun onTouch(view: View?, motionEvent: MotionEvent?, x: Int, y: Int) {
            if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
                AppGlobalData.MA_INSTANCE.pic.loadHistoryBitmap()
                last_bmp = AppGlobalData.MA_INSTANCE.pic.getBitmap()
                x_0 = x
                y_0 = y
            }
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                last_bmp?.let { AppGlobalData.MA_INSTANCE.pic.updateBitmap(it) }
                var s = 0f
                while (s < 60) {
                    val sin = ((sin(s * 6 * Math.PI / 180) * AppGlobalData.MA_INSTANCE.getDistance(
                        x_0.toFloat(),
                        y_0.toFloat(),
                        x.toFloat(),
                        y.toFloat()
                    ) * 100000).roundToInt() / 100000).toFloat()
                    val cos = ((cos(s * 6 * Math.PI / 180) * AppGlobalData.MA_INSTANCE.getDistance(
                        x_0.toFloat(),
                        y_0.toFloat(),
                        x.toFloat(),
                        y.toFloat()
                    ) * 100000).roundToInt() / 100000).toFloat()
                    AppGlobalData.MA_INSTANCE.pic[x_0 + sin, y_0 + cos] = WorkActivity().penColor
                    s += 0.1f
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                x_0 = 0
                y_0 = 0
                last_bmp = null
            }
            super.onTouch(view, motionEvent, x, y)
        }
    }

    private var CircleListener: OnPixelTouchListener = object : OnPixelTouchListener() {
        private var x_0 = 0
        private var y_0 = 0
        private var last_bmp: Bitmap? = null

        @TargetApi(Build.VERSION_CODES.O)
        override fun onTouch(view: View?, motionEvent: MotionEvent?, x: Int, y: Int) {
            if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
                AppGlobalData.MA_INSTANCE.pic.loadHistoryBitmap()
                last_bmp = AppGlobalData.MA_INSTANCE.pic.getBitmap()
                x_0 = x
                y_0 = y
            }
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                last_bmp?.let { AppGlobalData.MA_INSTANCE.pic.updateBitmap(it) }
                var s = 0f
                while (s < 60) {
                    var i = 1f
                    while (i <= AppGlobalData.MA_INSTANCE.getDistance(
                            x_0.toFloat(),
                            y_0.toFloat(),
                            x.toFloat(),
                            y.toFloat()
                        )
                    ) {
                        val sin =
                            ((sin(s * 6 * Math.PI / 180) * i * 100000).roundToInt() / 100000).toFloat()
                        val cos =
                            ((cos(s * 6 * Math.PI / 180) * i * 100000).roundToInt() / 100000).toFloat()
                        AppGlobalData.MA_INSTANCE.pic[x_0 + sin, y_0 + cos] = WorkActivity().penColor
                        i += 0.1f
                    }
                    s += 0.1f
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                x_0 = 0
                y_0 = 0
                last_bmp = null
            }
            super.onTouch(view, motionEvent, x, y)
        }
    }

    var selectListener = View.OnClickListener { view ->
            if (!AppGlobalData.MA_INSTANCE.isEnableSelect) {
                WorkActivity().enableMove = false
                view.setBackgroundResource(R.drawable.shape_button_selected)
                AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(object :
                    OnPixelTouchListener() {
                    lateinit var pos: IntArray
                    lateinit var stop_pos: IntArray
                    lateinit var diff: IntArray
                    lateinit var diff_end: IntArray
                    lateinit var pos_mea: FloatArray
                    override fun onTouch(view: View?, motionEvent: MotionEvent?, x: Int, y: Int) {
                        if (!hasSelected) {
                            AppGlobalData.MA_INSTANCE.pic.cleanSelectedPixels()
                            if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
                                if (selectEditWin != null) selectEditWin!!.dismiss()
                                pos = intArrayOf(x, y)
                                select_start = pos.clone()
                            } else {
                                AppGlobalData.MA_INSTANCE.pic.selectRectPixel(
                                    pos[0],
                                    pos[1],
                                    x + 1,
                                    y + 1
                                )
                                AppGlobalData.MA_INSTANCE.pic.renderSelectedPixels()
                            }
                            if (motionEvent.action == MotionEvent.ACTION_UP) {
                                stop_pos = intArrayOf(x, y)
                                select_end = stop_pos.clone()
                                hasSelected = true
                                pos_mea = floatArrayOf(motionEvent.rawX, motionEvent.rawY)
                                selectEditWin = showSecPopWindow(R.layout.popupwin_select_edit_tools)
                            }
                        } else {
                            if (x >= pos[0] && x <= stop_pos[0] && y >= pos[1] && y <= stop_pos[1]) {
                                if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
                                    diff = intArrayOf(pos[0] - x, pos[1] - y)
                                    diff_end = intArrayOf(stop_pos[0] - x, stop_pos[1] - y)
                                }
                                if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                                    pos[0] = diff[0] + x
                                    pos[1] = diff[1] + y
                                    stop_pos[0] = diff_end[0] + x
                                    stop_pos[1] = diff_end[1] + y
                                    AppGlobalData.MA_INSTANCE.pic.cleanSelectedPixels()
                                    AppGlobalData.MA_INSTANCE.pic.selectRectPixel(
                                        pos[0],
                                        pos[1], stop_pos[0] + 1, stop_pos[1] + 1
                                    )
                                    AppGlobalData.MA_INSTANCE.pic.renderSelectedPixels()
                                }
                            }
                        }
                        super.onTouch(view, motionEvent, x, y)
                    }
                })
            } else {
                hasSelected = false
                WorkActivity().enableMove = true
                AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(null)
                AppGlobalData.MA_INSTANCE.pic.cleanSelectedPixels()
                selectEditWin!!.dismiss()
                view.setBackgroundResource(R.drawable.shape_button)
            }
            AppGlobalData.MA_INSTANCE.isEnableSelect = !AppGlobalData.MA_INSTANCE.isEnableSelect
        }

    @TargetApi(Build.VERSION_CODES.O)
    fun resetListenersForTools() {
        AppGlobalData.MA_INSTANCE.tools = booleanArrayOf(false, false, false, false, false)
        AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(null)
        AppGlobalData.MA_INSTANCE.pic.setOnPixelClickListener(null)
        AppGlobalData.MA_INSTANCE.buttonPen.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.buttonDrawPen.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.buttonEraser.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.buttonBucket.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.buttonColorPicker.setBackgroundResource(R.drawable.shape_sel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun resetListenersForGraphTools() {
        AppGlobalData.MA_INSTANCE.graphTools = booleanArrayOf(false, false, false, false, false)
        AppGlobalData.MA_INSTANCE.pic.setOnPixelTouchListener(null)
        AppGlobalData.MA_INSTANCE.pic.setOnPixelClickListener(null)
        AppGlobalData.MA_INSTANCE.bline.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.bSquare.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.bSquareHol.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.bCircle.setBackgroundResource(R.drawable.shape_sel)
        AppGlobalData.MA_INSTANCE.bCircleHol.setBackgroundResource(R.drawable.shape_sel)
    }

    private fun showSecPopWindow(layout_id: Int): PopupWindow {
        val inflater = LayoutInflater.from(AppGlobalData.MA_INSTANCE)
        val popupWindow = PopupWindow(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val view = inflater.inflate(layout_id, null, true)
        popupWindow.contentView = view
        popupWindow.isFocusable = false
        popupWindow.animationStyle = R.style.popupWindow
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAtLocation(
            AppGlobalData.MA_INSTANCE.window.decorView,
            Gravity.TOP or Gravity.START,
            AppGlobalData.MA_INSTANCE.dip2px(40f) + 20,
            20
        )
        val sliceButton = view.findViewById<ImageButton>(R.id.button_slice)
        val cutButton = view.findViewById<ImageButton>(R.id.button_cut)
        val copyButton = view.findViewById<ImageButton>(R.id.button_copy)
        sliceButton.setOnClickListener {
            AppGlobalData.MA_INSTANCE.pic.sliceAsBitmap(
                select_start[0],
                select_start[1],
                select_end[0] + 1,
                select_end[1] + 1
            )?.let { it1 ->
                AppGlobalData.MA_INSTANCE.pic.setInitBitmap(
                    it1
                )
            }
            AppGlobalData.MA_INSTANCE.pic.cleanSelectedPixels()
            select_start = intArrayOf(0, 0)
            select_end = intArrayOf(
                AppGlobalData.MA_INSTANCE.pic.mWidthPixels - 1,
                AppGlobalData.MA_INSTANCE.pic.mHeightPixels - 1
            )
            AppGlobalData.MA_INSTANCE.pic.selectRectPixel(
                0,
                0,
                AppGlobalData.MA_INSTANCE.pic.mWidthPixels,
                AppGlobalData.MA_INSTANCE.pic.mHeightPixels
            )
            AppGlobalData.MA_INSTANCE.pic.renderSelectedPixels()
        }
        cutButton.setOnClickListener {
            AppGlobalData.copied_pic = AppGlobalData.MA_INSTANCE.pic.sliceAsBitmap(
                select_start[0],
                select_start[1],
                select_end[0] + 1,
                select_end[1] + 1
            )
            for (i in select_start[0] until select_end[0] + 1) for (ii in select_start[1] until select_end[1] + 1) AppGlobalData.MA_INSTANCE.pic[i, ii] =
                Color.TRANSPARENT
            Toast.makeText(AppGlobalData.MAIN_CONTEXT, "长按即可粘贴", Toast.LENGTH_SHORT).show()
            AppGlobalData.MA_INSTANCE.buttonSelect.performClick()
        }
        copyButton.setOnClickListener {
            AppGlobalData.copied_pic = AppGlobalData.MA_INSTANCE.pic.sliceAsBitmap(
                select_start[0],
                select_start[1],
                select_end[0] + 1,
                select_end[1] + 1
            )
            Toast.makeText(AppGlobalData.MAIN_CONTEXT, "长按即可粘贴", Toast.LENGTH_SHORT).show()
            AppGlobalData.MA_INSTANCE.buttonSelect.performClick()
        }
        return popupWindow
    }
}