package com.dream.pixeldraw.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.dream.pixeldraw.R

object WorkActHelper {

     fun showMainPopWindow(layout_id: Int, context: Context, act: Activity): PopupWindow {
        val inflater = LayoutInflater.from(context)
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
        popupWindow.showAtLocation(act.window.decorView, Gravity.TOP or Gravity.START, 20, 20)
        return popupWindow
    }
}