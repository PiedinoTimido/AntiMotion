package com.MotionSickness.antimotion.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient

class ActivityRecognitionManager(private val context: Context) {

    private val client: ActivityRecognitionClient = ActivityRecognition.getClient(context)
    
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun startTracking() {
        try {
            client.requestActivityUpdates(30000L, pendingIntent)
                .addOnSuccessListener { Log.d("AR_Manager", "Activity updates requested") }
                .addOnFailureListener { e -> Log.e("AR_Manager", "Failed to request updates", e) }
        } catch (e: SecurityException) {
            Log.e("AR_Manager", "Permission missing for Activity Recognition", e)
        }
    }

    fun stopTracking() {
        client.removeActivityUpdates(pendingIntent)
            .addOnSuccessListener { Log.d("AR_Manager", "Activity updates removed") }
    }
}
