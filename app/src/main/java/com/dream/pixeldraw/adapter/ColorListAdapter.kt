package com.dream.pixeldraw.adapter

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.dream.pixeldraw.R

class ColorListAdapter(private var color: ArrayList<Int>, private var mContext: Context) : BaseAdapter() {

    fun addColor(color: Int) {
        val w: Int = mContext.resources.displayMetrics.heightPixels / dip2px(30f) - 4
        if (this.color.size + 1 > w) this.color.removeAt(this.color.size - 1)
        this.color.add(0, color)
    }

    fun removeColor(index: Int) {
        color.removeAt(index)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getColor(index: Int): Color {
        return Color.valueOf(color[index])
    }
    override fun getCount(): Int {
        return color.size
    }

    override fun getItem(position: Int): Any {
        return color[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(mContext)
        val scaleDrawable = ScaleDrawable(
            LayerDrawable(
                arrayOf(
                    ResourcesCompat.getDrawable(mContext.resources, R.drawable.trans_2, null),
                    ColorDrawable(color[position]))
            ), Gravity.CENTER, dip2px(25f).toFloat(), dip2px(25f).toFloat()
        )
        scaleDrawable.level = 9960
        val layerDrawable = LayerDrawable(
            arrayOf(
                ResourcesCompat.getDrawable(mContext.resources, R.drawable.shape_button, null),
                scaleDrawable
            )
        )
        imageView.background = layerDrawable
        imageView.layoutParams = LinearLayout.LayoutParams(dip2px(30f), dip2px(30f))
        imageView.isFocusable = false
        imageView.isFocusableInTouchMode = false
        return imageView
    }

    private fun dip2px(dpValue: Float): Int {
        val context = mContext
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}