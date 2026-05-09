package com.MotionSickness.antimotion.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.MotionSickness.antimotion.data.PreferenceManager
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityRecognitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent) ?: return
            val mostProbableActivity = result.mostProbableActivity
            
            Log.d("ActivityReceiver", "Detected activity: ${mostProbableActivity.type} with confidence ${mostProbableActivity.confidence}")
            
            val prefs = PreferenceManager(context)
            if (!prefs.isAutoStartEnabled) return

            if (mostProbableActivity.type == DetectedActivity.IN_VEHICLE && mostProbableActivity.confidence >= 75) {
                if (!MotionService.isRunning) {
                    val serviceIntent = Intent(context, MotionService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            } else if (mostProbableActivity.type != DetectedActivity.IN_VEHICLE && mostProbableActivity.confidence >= 90) {
                // If we are definitely not in a vehicle, we could stop the service, 
                // but maybe we should wait for a few "not in vehicle" events to avoid flickering
                // For now, let's just log it. Manual stop is always possible via tile.
            }
        }
    }
}
