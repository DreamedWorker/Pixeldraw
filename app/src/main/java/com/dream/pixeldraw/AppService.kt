package com.dream.pixeldraw

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AppService : Service() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}