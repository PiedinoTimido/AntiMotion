package com.MotionSickness.antimotion.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.MotionSickness.antimotion.MainActivity
import com.MotionSickness.antimotion.R
import com.MotionSickness.antimotion.engine.SensorEngine
import com.MotionSickness.antimotion.ui.overlay.MotionOverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MotionService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var motionJob: Job? = null
    
    private lateinit var sensorEngine: SensorEngine
    private lateinit var overlayManager: MotionOverlayManager

    companion object {
        private const val TAG = "MotionService"
        private const val CHANNEL_ID = "motion_service_channel"
        private const val NOTIFICATION_ID = 1
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        sensorEngine = SensorEngine(this)
        overlayManager = MotionOverlayManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
            isRunning = true
            
            overlayManager.showOverlay(sensorEngine.getMotionFlow())
            
            // Still keep the log for debugging if needed, but the UI is now driven by the flow directly
            startMotionCollection()
        }
        return START_STICKY
    }

    private fun startMotionCollection() {
        motionJob?.cancel()
        motionJob = serviceScope.launch {
            sensorEngine.getMotionFlow().collectLatest { vector ->
                // Log for diagnostics
                // Log.d(TAG, "Motion: x=${vector.x}, y=${vector.y}, rot=${vector.rotation}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.hideOverlay()
        motionJob?.cancel()
        serviceJob.cancel()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
