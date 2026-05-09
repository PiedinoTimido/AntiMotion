package com.MotionSickness.antimotion.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.MotionSickness.antimotion.data.PreferenceManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferenceManager(context)
            if (prefs.isAutoStartEnabled) {
                ActivityRecognitionManager(context).startTracking()
            }
        }
    }
}
