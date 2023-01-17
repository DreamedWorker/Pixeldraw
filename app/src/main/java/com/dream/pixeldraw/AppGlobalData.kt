package com.dream.pixeldraw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.dream.pixeldraw.activity.WorkActivity
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

@SuppressLint("StaticFieldLeak")
object AppGlobalData {
    lateinit var MAIN_CONTEXT: Context
    lateinit var MA_INSTANCE: WorkActivity
    @JvmField
    var Plates = ArrayList<Bitmap>()
    @JvmField
    var Saved_Plates = ArrayList<Bitmap>()
    @JvmField
    var colorful_bar: Bitmap? = null
    private var color_plane = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
    @JvmField
    var alpha_plane = Bitmap.createBitmap(870, 300, Bitmap.Config.ARGB_8888)!!
    @JvmField
    var brightness_plane = Bitmap.createBitmap(870, 300, Bitmap.Config.ARGB_8888)!!
    @JvmField
    var copied_pic: Bitmap? = null
    private var bitmap_file: File? = null
    private var collection_file: File? = null
    private var black: Drawable? = null
    private var white: Drawable? = null
    @Suppress("DEPRECATION")
    @JvmStatic
    fun initailizeData(mContext: Context, activity: WorkActivity) {
        MAIN_CONTEXT = mContext
        MA_INSTANCE = activity
        bitmap_file = File(MAIN_CONTEXT.filesDir.absolutePath + "/recent.png")
        collection_file = File(MAIN_CONTEXT.filesDir.absolutePath + "/color.txt")
        MA_INSTANCE.windowManager.defaultDisplay.getMetrics(MA_INSTANCE.displayMetrics)
        try {
            if (!bitmap_file!!.exists()) {
                bitmap_file!!.createNewFile()
                val os = FileOutputStream(bitmap_file)
                Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
                    .compress(Bitmap.CompressFormat.PNG, 80, os)
                os.flush()
                os.close()
            }
            if (!collection_file!!.exists()) {
                collection_file!!.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun addColColors(color: Int) {
        try {
            val fw = FileWriter(collection_file)
            if (collection_file!!.length() == 0L) fw.write("" + color) else fw.write(",$color")
            fw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun initColorfulBar(): Bitmap? {
        //color
        colorful_bar = Bitmap.createBitmap(300, 870, Bitmap.Config.ARGB_8888)
        for (i in 0..869) for (j in 0..299) {
            val sum = i.toFloat() / 870f
            colorful_bar?.setPixel(j, i, Color.HSVToColor(255, floatArrayOf(sum * 360, 1f, 1f)))
        }
        black = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.TRANSPARENT, Color.BLACK)
        )
        white = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.TRANSPARENT, Color.WHITE)
        )
        return colorful_bar
    }

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun makeColorPlane(color: Int): Bitmap {
        val d: Drawable = ColorDrawable(color)
        val l = LayerDrawable(arrayOf(d, black, white))
        val canvas = Canvas(color_plane)
        l.setBounds(0, 0, 800, 800)
        l.draw(canvas)
        return color_plane
    }

    @JvmStatic
    fun makeAlphaPlane(color: Int): Bitmap {
        val turnedColor = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color))
        val ll = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(color, turnedColor)
        )
        val l =
            LayerDrawable(arrayOf(ResourcesCompat.getDrawable(MA_INSTANCE.resources, R.drawable.trans, null), ll))
        val canvas = Canvas(alpha_plane)
        l.setBounds(0, 0, 900, 300)
        l.draw(canvas)
        return alpha_plane
    }

    @JvmStatic
    fun makeBrightnessPlane(color: Int): Bitmap {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = 1f
        val l = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(
                Color.BLACK, Color.HSVToColor(
                    Color.alpha(color), hsv
                ), Color.WHITE
            )
        )
        val canvas = Canvas(brightness_plane)
        l.setBounds(0, 0, 900, 300)
        l.draw(canvas)
        return brightness_plane
    }
}