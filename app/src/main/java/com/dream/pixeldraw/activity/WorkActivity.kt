package com.dream.pixeldraw.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.dream.pixeldraw.AppGlobalData
import com.dream.pixeldraw.AppGlobalData.makeAlphaPlane
import com.dream.pixeldraw.AppGlobalData.makeBrightnessPlane
import com.dream.pixeldraw.AppGlobalData.makeColorPlane
import com.dream.pixeldraw.AppService
import com.dream.pixeldraw.R
import com.dream.pixeldraw.adapter.ColorListAdapter
import com.dream.pixeldraw.adapter.Listeners.getGraphToolOnClickListener
import com.dream.pixeldraw.adapter.Listeners.resetListenersForGraphTools
import com.dream.pixeldraw.adapter.Listeners.resetListenersForTools
import com.dream.pixeldraw.adapter.Listeners.selectListener
import com.dream.pixeldraw.databinding.ActivityMainBinding
import com.dream.pixeldraw.ui.PixelPicView
import com.dream.pixeldraw.ui.PixelPicView.OnPixelTouchListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class WorkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var tools = booleanArrayOf(false, false, false, false, false)
    var graphTools = booleanArrayOf(false, false, false, false, false)
    private lateinit var colorViewer: TextView
    private lateinit var buttonColorList: ListView
    private var onPixelClickListener: OnPixelTouchListener? = null
    private  var onPixelTouchListener:OnPixelTouchListener? = null
    private  var onBucketUseListener:OnPixelTouchListener? = null
    private  var onColorPickerUseListener:OnPixelTouchListener? = null
    private  var onEraserUseClickListener:OnPixelTouchListener? = null
    private var pixelSize0 = 0f
    lateinit var buttonPen: ImageButton
    lateinit var buttonDrawPen: ImageButton
    lateinit var buttonEraser: ImageButton
    lateinit var buttonBucket: ImageButton
    lateinit var buttonColorPicker :ImageButton
    lateinit var bline: ImageButton
    lateinit var bSquare: ImageButton
    lateinit var bSquareHol: ImageButton
    lateinit var bCircle: ImageButton
    lateinit var bCircleHol: ImageButton
    lateinit var returnButton: ImageButton
    lateinit var undoButton: ImageButton
    var displayMetrics = DisplayMetrics()
    var colorListAdapter = ColorListAdapter(ArrayList(), this)
    @RequiresApi(Build.VERSION_CODES.O)
    var alColor = Color.valueOf(0)
    var colorPicked = 0
    var pathStr: String = Environment.getExternalStorageDirectory().absolutePath
    var penColor: Int = -0x1000000
    var enableMove = true
    var mainWin: PopupWindow? = null
    var bottomWin: PopupWindow? = null
    var editWin: PopupWindow? = null
    var fileWin: PopupWindow? = null
    var colorWin: PopupWindow? = null
    var colorSelectorWin: PopupWindow? = null
    var returnWin: PopupWindow? = null
    var graphWin: PopupWindow? = null
    lateinit var pic: PixelPicView
    var isEnableSelect = false
    lateinit var buttonSelect: ImageButton
    var startX = FloatArray(2)
    var startY = FloatArray(2)
    var orginalPos = FloatArray(2)
    var orginalSize = FloatArray(2)
    lateinit var relMidpoint: FloatArray
    var orginalDistance = 0f
    private lateinit var bitmap: Bitmap

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init AppGlobalData
        AppGlobalData.initailizeData(this@WorkActivity, this@WorkActivity)
        AppGlobalData.initColorfulBar()
        pic = binding.mainPainter
        val screen = binding.frameLayout
        //setupListeners
        setListener(screen)
    }

    override fun onStop() {
        val intent = Intent(this, AppService::class.java)
        startService(intent)
        super.onStop()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 2 && enableMove) {
            if (event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
                startX[0] = event.getX(0)
                startY[0] = event.getY(0)
                startX[1] = event.getX(1)
                startY[1] = event.getY(1)
                orginalSize[0] = pic.scaleX
                orginalSize[1] = pic.scaleY
                orginalPos[0] = pic.x
                orginalPos[1] = pic.y
                orginalDistance = getDistance(startX[0], startY[0], startX[1], startY[1])
                val midpoint =
                    floatArrayOf((startX[0] + startX[1]) / 2, (startY[0] + startY[1]) / 2)
                relMidpoint = floatArrayOf(midpoint[0] - pic.x, midpoint[1] - pic.y)
                pic.pivotX = relMidpoint[0]
                pic.pivotY = relMidpoint[1]
            }
            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                val distence: Float =
                    getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
                val pixelSize: Float = pic.scaleX * dip2px(300f) / pic.widthPixels
                if (orginalSize[0] + (distence - orginalDistance) / 300 > 0) {
                    pic.scaleX = orginalSize[0] + (distence - orginalDistance) / 300
                    pic.scaleY = orginalSize[1] + (distence - orginalDistance) / 300
                }
                pic.x =
                    orginalPos[0] + (event.getX(0) - startX[0] + event.getX(1) - startX[1]) / 2
                pic.y =
                    orginalPos[1] + (event.getY(0) - startY[0] + event.getY(1) - startY[1]) / 2
                if (pixelSize >= 22) {
                    pic.enableWeb(true)
                } else {
                    pic.enableWeb(false)
                }
            }
            if (event.actionMasked == MotionEvent.ACTION_POINTER_UP) {
                startX = FloatArray(2)
                startY = FloatArray(2)
                orginalPos = FloatArray(2)
                orginalSize = FloatArray(2)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun showMainPopWindow(layout_id: Int): PopupWindow {
        val inflater = LayoutInflater.from(this@WorkActivity)
        val popupWindow = PopupWindow(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val view = inflater.inflate(layout_id, null, true)
        popupWindow.contentView = view
        //popupWindow.setOutsideTouchable(true);
        popupWindow.isFocusable = false
        popupWindow.animationStyle = R.style.popupWindow
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAtLocation(window.decorView, Gravity.TOP or Gravity.START, 20, 20)
        return popupWindow
    }

    private fun showSecPopWindow(layout_id: Int): PopupWindow {
        val inflater = LayoutInflater.from(this@WorkActivity)
        val popupWindow = PopupWindow(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val view = inflater.inflate(layout_id, null, true)
        popupWindow.contentView = view
        //popupWindow.setOutsideTouchable(true);
        popupWindow.isFocusable = false
        popupWindow.animationStyle = R.style.popupWindow_sec
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAtLocation(window.decorView, Gravity.TOP or Gravity.END, 20, 20)
        return popupWindow
    }

    private fun showSimplePopWindow(ctx: Context, layout_id: Int): PopupWindow {
        val inflater = LayoutInflater.from(ctx)
        val popupWindow = PopupWindow(dip2px(245f), dip2px(250f))
        val view = inflater.inflate(layout_id, null, true)
        popupWindow.contentView = view
        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true
        popupWindow.animationStyle = R.style.popupWindow_sec
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAtLocation(
            window.decorView,
            Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL,
            0,
            0
        )
        return popupWindow
    }

    private fun showReturnWindow(layout_id: Int): PopupWindow {
        val inflater = LayoutInflater.from(this@WorkActivity)
        val popupWindow = PopupWindow(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val view = inflater.inflate(layout_id, null, true)
        popupWindow.contentView = view
        //popupWindow.setOutsideTouchable(false);
        popupWindow.isFocusable = false
        popupWindow.animationStyle = R.style.popupWindow
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAtLocation(window.decorView, Gravity.START or Gravity.BOTTOM, 20, 0)
        return popupWindow
    }

    private fun showBottomWindow(layout_id: Int): PopupWindow {
        val inflater = LayoutInflater.from(this@WorkActivity)
        val popupWindow = PopupWindow(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val view = inflater.inflate(layout_id, null, true)
        popupWindow.contentView = view
        //popupWindow.setOutsideTouchable(false);
        popupWindow.isFocusable = false
        popupWindow.animationStyle = R.style.popupWindow
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAtLocation(
            window.decorView,
            Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
            20,
            0
        )
        return popupWindow
    }

    fun updateColorViewer(color: Int) {
        colorViewer.background = LayerDrawable(
            arrayOf(
                ResourcesCompat.getDrawable(resources, R.drawable.trans_1, null),
                ColorDrawable(color)
            )
        )
        colorPicked = color
        colorViewer.setText(
            String.format(
                "%02x%02x%02x%02x",
                Color.alpha(color),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        )
        val ave = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3f / 255f
        if (ave < 0.5) colorViewer.setTextColor(Color.WHITE) else colorViewer.setTextColor(Color.BLACK)
    }

    @SuppressLint("ClickableViewAccessibility")
    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showColorSelectorWin(ctx: Context?, listener: View.OnClickListener?): PopupWindow {
        val colorSelectorWin = showSimplePopWindow(ctx!!, R.layout.popupwin_colorselector)
        val ctt = colorSelectorWin.contentView
        val colorAdjust = ctt.findViewById<ImageView>(R.id.color_plane)
        val colorOpacity = ctt.findViewById<ImageView>(R.id.alpha_plate)
        val colorSelect = ctt.findViewById<ImageView>(R.id.lightness_plane)
        val colorBrightness = ctt.findViewById<ImageView>(R.id.brightness_plate)
        val buttonPick = ctt.findViewById<Button>(R.id.button_yes)
        val buttonCancel = ctt.findViewById<Button>(R.id.button_no)
        colorViewer = ctt.findViewById<TextView>(R.id.colorviewer)
        val colorBefore = colorListAdapter.getColor(0).toArgb()
        makeAlphaPlane(colorBefore)
        makeBrightnessPlane(colorBefore)
        colorBrightness.background = BitmapDrawable(AppGlobalData.brightness_plane)
        colorAdjust.background = BitmapDrawable(makeColorPlane(colorBefore))
        colorOpacity.background = BitmapDrawable(AppGlobalData.alpha_plane)
        colorSelect.background = BitmapDrawable(AppGlobalData.colorful_bar)
        updateColorViewer(colorBefore)
        colorSelect.setOnTouchListener { _, motionEvent ->
            val x = motionEvent.x.toInt()
            val y = motionEvent.y.toInt()
            try {
                val colorTouch: Int = getBitmapFromView(colorSelect).getPixel(x, y)
                AppGlobalData.alpha_plane = Bitmap.createBitmap(870, 300, Bitmap.Config.ARGB_8888)
                makeAlphaPlane(colorTouch)
                makeBrightnessPlane(colorTouch)
                colorBrightness.background = BitmapDrawable(AppGlobalData.brightness_plane)
                colorAdjust.background = BitmapDrawable(makeColorPlane(colorTouch))
                colorOpacity.background = BitmapDrawable(AppGlobalData.alpha_plane)
                updateColorViewer(colorTouch)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            true
        }
        colorAdjust.setOnTouchListener { _, motionEvent ->
            val x = motionEvent.x.toInt()
            val y = motionEvent.y.toInt()
            try {
                val colorTouch: Int = getBitmapFromView(colorAdjust).getPixel(x, y)
                AppGlobalData.alpha_plane = Bitmap.createBitmap(870, 300, Bitmap.Config.ARGB_8888)
                makeAlphaPlane(colorTouch)
                makeBrightnessPlane(colorTouch)
                colorOpacity.background = BitmapDrawable(AppGlobalData.alpha_plane)
                updateColorViewer(colorTouch)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            true
        }
        colorOpacity.setOnTouchListener { _, motionEvent ->
            val y = motionEvent.y
            try {
                val colorTouch = Color.argb(
                    (255 - y / colorOpacity.measuredHeight * 255).toInt(),
                    Color.red(colorPicked),
                    Color.green(colorPicked),
                    Color.blue(colorPicked)
                )
                updateColorViewer(colorTouch)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            true
        }
        colorBrightness.setOnTouchListener { _, motionEvent ->
            val x = motionEvent.x.toInt()
            val y = motionEvent.y.toInt()
            try {
                val colorTouch: Int = getBitmapFromView(colorBrightness).getPixel(x, y)
                makeAlphaPlane(colorTouch)
                colorOpacity.background = BitmapDrawable(AppGlobalData.alpha_plane)
                updateColorViewer(colorTouch)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            true
        }
        buttonPick.setOnClickListener(listener)
        buttonCancel.setOnClickListener { colorSelectorWin.dismiss() }
        return colorSelectorWin
    }

    fun getBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.RGB_565)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        // Draw background
        val bgDrawable = v.background
        if (bgDrawable != null) {
            bgDrawable.draw(c)
        } else {
            c.drawColor(Color.WHITE)
        }
        // Draw view to canvas
        v.draw(c)
        return b
    }

    fun saveImage() {
        try {
            val saveFile = File(pathStr)
            if (!saveFile.exists()) saveFile.createNewFile()
            val outputStream = FileOutputStream(saveFile)
            pic.bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun showNewFileDialog() {
        pathStr = ""
        val editText = AppCompatEditText(this@WorkActivity)
        editText.hint = "图像大小"
        editText.textSize = 14f
        val dialog = AlertDialog.Builder(this@WorkActivity)
            .setTitle("新建文件")
            .setMessage("输入一个整数，它代表图像的长和宽。我们将创建这个正方形图像，建议设为16。")
            .setView(editText)
            .setPositiveButton("确定") { _: DialogInterface?, _: Int ->
                if (Objects.requireNonNull(editText.text)
                        .toString().isNotEmpty()
                ) {
                    val widthAndHeight = editText.text.toString().toInt()
                    val bitmapNew =
                        Bitmap.createBitmap(widthAndHeight, widthAndHeight, Bitmap.Config.ARGB_8888)
                    pic.setInitBitmap(bitmapNew)
                }
            }
            .setNegativeButton("取消", null).create()
        dialog.show()
    }

    fun onRead() {
        Toast.makeText(applicationContext, "打开中", Toast.LENGTH_SHORT).show()
        if (File(pathStr).exists()) {
            bitmap = BitmapFactory.decodeFile(pathStr)
            if (bitmap != null) pic.setInitBitmap(bitmap) else Toast.makeText(
                applicationContext,
                "打开失败",
                Toast.LENGTH_SHORT
            ).show()
        } else Toast.makeText(applicationContext, "打开失败", Toast.LENGTH_SHORT).show()
    }

    @Suppress("NAME_SHADOWING")
    fun getToolOnClickListener(view: ImageButton?, tool_id: Int): View.OnClickListener {
        return View.OnClickListener { view ->
            tools[tool_id] = !tools[tool_id]
            if (!tools[tool_id]) {
                resetListenersForTools()
                view.setBackgroundResource(R.drawable.shape_button_selected)
                when (tool_id) {
                    0 -> pic.setOnPixelTouchListener(onPixelClickListener)
                    1 -> pic.setOnPixelTouchListener(onPixelTouchListener)
                    2 -> pic.setOnPixelTouchListener(onBucketUseListener)
                    3 -> pic.setOnPixelTouchListener(onColorPickerUseListener)
                    4 -> pic.setOnPixelTouchListener(onEraserUseClickListener)
                }
            } else {
                view.setBackgroundResource(R.drawable.shape_sel)
                pic.setOnPixelTouchListener(null)
                pic.setOnPixelClickListener(null)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListener(screen: FrameLayout) {
        onPixelClickListener = object : OnPixelTouchListener() {
            override fun onTouch(view: View, motionEvent: MotionEvent, x: Int, y: Int) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    pic.loadHistoryBitmap()
                    pic.set(x, y, penColor)
                }
                super.onTouch(view, motionEvent, x, y)
            }
        }
        onPixelTouchListener = object : OnPixelTouchListener() {
            override fun onTouch(view: View, motionEvent: MotionEvent, x: Int, y: Int) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) pic.loadHistoryBitmap() else pic.set(x, y,
                    penColor
                )
                super.onTouch(view, motionEvent, x, y)
            }
        }
        onEraserUseClickListener = object : OnPixelTouchListener() {
            override fun onTouch(view: View, motionEvent: MotionEvent, x: Int, y: Int) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) pic.loadHistoryBitmap() else pic[x, y] =
                    Color.TRANSPARENT
                super.onTouch(view, motionEvent, x, y)
            }
        }
        onBucketUseListener = object : OnPixelTouchListener() {
            private var originalColor = 0
            fun loop(x: Int, y: Int): Int {
                pic.set(x, y, penColor)
                if (x + 1 < pic.widthPixels && pic[x + 1, y] == originalColor) loop(x + 1, y)
                if (x - 1 >= 0 && pic[x - 1, y] == originalColor) loop(x - 1, y)
                if (y + 1 < pic.heightPixels && pic[x, y + 1] == originalColor) loop(x, y + 1)
                if (y - 1 >= 0 && pic[x, y - 1] == originalColor) loop(x, y - 1)
                return 0
            }
            override fun onTouch(view: View, motionEvent: MotionEvent, x: Int, y: Int) {
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    pic.loadHistoryBitmap()
                    originalColor = pic[x, y]
                    Thread {
                        try {
                            Thread.sleep(50)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        loop(x, y)
                    }.start()
                }
                super.onTouch(view, motionEvent, x, y)
            }
        }
        onColorPickerUseListener = object : OnPixelTouchListener() {
            override fun onTouch(view: View, motionEvent: MotionEvent, x: Int, y: Int) {
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    colorListAdapter.addColor(pic[x, y])
                    buttonColorList.adapter = colorListAdapter
                    penColor = pic[x, y]
                }
                super.onTouch(view, motionEvent, x, y)
            }
        }
        screen.post {
            pic.heightPixels = 16
            pic.widthPixels = 16
            pic.updateCanvas()
            colorListAdapter.addColor(Color.BLACK)
            pixelSize0 = pic.scaleX * dip2px(300f) / pic.widthPixels
            mainWin = showMainPopWindow(R.layout.popupwin)
            bottomWin = showBottomWindow(R.layout.popupwin_bottom)
            val mainView = mainWin!!.contentView
            val bottomView = bottomWin!!.contentView
            val buttonA = mainView.findViewById<ImageButton>(R.id.button2)
            val buttonB = mainView.findViewById<ImageButton>(R.id.button3)
            val buttonSettings = mainView.findViewById<ImageButton>(R.id.button4)
            buttonSelect = bottomView.findViewById(R.id.button_select)
            buttonSelect.setOnClickListener(selectListener)
            buttonSettings.setOnClickListener {
                startActivity(Intent(applicationContext, AboutActivity::class.java))
            }
            buttonA.setOnClickListener {
                mainWin!!.dismiss()
                returnWin = showReturnWindow(R.layout.popupwin_return)
                editWin = showMainPopWindow(R.layout.popupwin_edit)
                colorWin = showSecPopWindow(R.layout.popupwin_color)
                returnButton = returnWin!!.contentView.findViewById(R.id.button_return)
                undoButton = returnWin!!.contentView.findViewById(R.id.button_undo)
                returnButton.setOnClickListener {
                    if (AppGlobalData.Plates.size != 0) {
                        undoButton.visibility = View.VISIBLE
                        returnButton.visibility = View.VISIBLE
                        AppGlobalData.Saved_Plates.add(AppGlobalData.Plates[AppGlobalData.Plates.size - 1])
                        pic.updateBitmap(AppGlobalData.Plates[AppGlobalData.Plates.size - 1])
                        AppGlobalData.Plates.removeAt(AppGlobalData.Plates.size - 1)
                    }
                    if (AppGlobalData.Plates.size == 0) returnButton.visibility = View.INVISIBLE
                }
                undoButton.setOnClickListener {
                    if (AppGlobalData.Saved_Plates.size > 1) {
                        undoButton.visibility = View.VISIBLE
                        pic.updateBitmap(AppGlobalData.Saved_Plates[AppGlobalData.Saved_Plates.size - 2])
                        AppGlobalData.Saved_Plates.removeAt(AppGlobalData.Saved_Plates.size - 2)
                    }
                    if (AppGlobalData.Saved_Plates.size == 1) undoButton.visibility = View.INVISIBLE
                }
                val editView = editWin!!.contentView
                val colorView = colorWin!!.contentView
                val buttonSel = colorView.findViewById<ImageButton>(R.id.button_sel)
                buttonColorList = colorView.findViewById(R.id.color_list)
                val buttonBack = editView.findViewById<ImageButton>(R.id.button_back)
                val buttonGraph = editView.findViewById<ImageButton>(R.id.button_graph)
                buttonPen = editView.findViewById(R.id.button3)
                buttonDrawPen = editView.findViewById(R.id.button1)
                buttonBucket = editView.findViewById(R.id.button)
                buttonColorPicker = editView.findViewById(R.id.button2)
                buttonEraser = editView.findViewById(R.id.button5)
                buttonPen.setOnClickListener(getToolOnClickListener(buttonPen, 0))
                buttonDrawPen.setOnClickListener(getToolOnClickListener(buttonDrawPen, 1))
                buttonBucket.setOnClickListener(getToolOnClickListener(buttonBucket, 2))
                buttonColorPicker.setOnClickListener(getToolOnClickListener(buttonColorPicker, 3))
                buttonEraser.setOnClickListener(getToolOnClickListener(buttonEraser, 4))
                buttonColorList.adapter = colorListAdapter
                buttonColorList.setOnItemClickListener { _, _, i, _ ->
                    if (i != 0) {
                        alColor = colorListAdapter.getColor(i)
                        penColor = alColor.toArgb()
                        colorListAdapter.removeColor(i)
                        colorListAdapter.addColor(alColor.toArgb())
                        buttonColorList.adapter = colorListAdapter
                    }
                }
                buttonSel.setOnClickListener {
                    colorSelectorWin =
                        showColorSelectorWin(this@WorkActivity) {
                            penColor =
                                ((colorViewer.background as LayerDrawable).getDrawable(1) as ColorDrawable).color
                            colorListAdapter.addColor(penColor)
                            buttonColorList.adapter = colorListAdapter
                            colorSelectorWin!!.dismiss()
                        }
                }
                buttonGraph.setOnClickListener {
                    graphWin = showMainPopWindow(R.layout.popupwin_graph)
                    val graphView = graphWin!!.contentView
                    val bBack = graphView.findViewById<ImageButton>(R.id.button_back_)
                    bline = graphView.findViewById(R.id.button_line)
                    bSquare = graphView.findViewById(R.id.button_square)
                    bSquareHol = graphView.findViewById(R.id.button_square_hol)
                    bCircle = graphView.findViewById(R.id.button_circle)
                    bCircleHol = graphView.findViewById(R.id.button_circle_hol)
                    bline.setOnClickListener(getGraphToolOnClickListener(bline, 0))
                    bSquare.setOnClickListener(getGraphToolOnClickListener(bSquare, 1))
                    bSquareHol.setOnClickListener(getGraphToolOnClickListener(bSquareHol, 2))
                    bCircle.setOnClickListener(getGraphToolOnClickListener(bCircle, 3))
                    bCircleHol.setOnClickListener(getGraphToolOnClickListener(bCircleHol, 4))
                    resetListenersForGraphTools()
                    bBack.setOnClickListener {
                        resetListenersForGraphTools()
                        graphWin!!.dismiss()
                        editWin!!.showAtLocation(
                            window.decorView,
                            Gravity.TOP or Gravity.START,
                            20,
                            20
                        )
                        resetListenersForTools()
                    }
                    editWin!!.dismiss()
                }
                buttonBack.setOnClickListener {
                    mainWin!!.showAtLocation(
                        window.decorView,
                        Gravity.TOP or Gravity.START,
                        20,
                        20
                    )
                    resetListenersForTools()
                    enableMove = true
                    editWin!!.dismiss()
                    colorWin!!.dismiss()
                    returnWin!!.dismiss()
                }
            }
            buttonB.setOnClickListener {
                fileWin = showMainPopWindow(R.layout.popupwin_file)
                val con = fileWin!!.contentView
                val buttonBack = con.findViewById<ImageButton>(R.id.button_back__)
                val buttonOpenFile =
                    con.findViewById<ImageButton>(R.id.button_open_file)
                val buttonSave = con.findViewById<ImageButton>(R.id.button_save)
                val buttonSaveAs = con.findViewById<ImageButton>(R.id.button_save_as)
                val buttonNewFile = con.findViewById<ImageButton>(R.id.button_new_file)
                buttonBack.setOnClickListener {
                    mainWin!!.showAtLocation(
                        window.decorView,
                        Gravity.TOP or Gravity.START,
                        20,
                        20
                    )
                    fileWin!!.dismiss()
                }
                buttonNewFile.setOnClickListener { showNewFileDialog() }
                buttonSave.setOnClickListener {
                    if (pathStr != null) {
                        saveImage()
                        Toast.makeText(applicationContext, "保存成功", Toast.LENGTH_SHORT).show()
                    } else {
                        pathStr = Environment.getExternalStorageDirectory().path
                        startActivity(Intent(applicationContext, SaveFileActivity::class.java))
                    }
                }
                buttonSaveAs.setOnClickListener {
                    if (pathStr != null) {
                        startActivity(Intent(applicationContext, SaveFileActivity::class.java))
                    } else pathStr = Environment.getExternalStorageDirectory().path
                }
                buttonOpenFile.setOnClickListener {
                    startActivity(Intent(applicationContext, FileChooseActivity::class.java))
                }
                mainWin!!.dismiss()
            }
        }
    }

    fun getDistance(p1_x: Float, p1_y: Float, p2_x: Float, p2_y: Float): Float {
        val x = abs(p1_x - p2_x)
        val y = abs(p1_y - p2_y)
        return sqrt(x.toDouble().pow(2.0) + y.toDouble().pow(2.0)).toFloat()
    }

    fun dip2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}