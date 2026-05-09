package com.MotionSickness.antimotion.engine

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlin.math.PI

class SensorEngine(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // Smoothing factor for low-pass filter (0 < alpha < 1)
    // Lower alpha = smoother/slower response
    private val alpha = 0.12f
    
    // Scale factors to map sensor data to UI displacement (DP or relative pixels)
    private val translationScale = 20f
    private val rotationScale = 8f

    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredRotation = 0f

    fun getMotionFlow(): Flow<MotionVector> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        // We use the raw accelerometer values. 
                        // When the vehicle accelerates, the dots should shift in the opposite direction.
                        // Accelerometer measures the force, so it naturally handles both tilt and linear acceleration.
                        val rawX = -event.values[0]
                        val rawY = event.values[1]
                        
                        filteredX = filteredX + alpha * (rawX - filteredX)
                        filteredY = filteredY + alpha * (rawY - filteredY)
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        // Z-axis angular velocity for rotation (turning)
                        val rawRot = -event.values[2]
                        filteredRotation = filteredRotation + alpha * (rawRot - filteredRotation)
                    }
                }

                val vector = MotionVector(
                    x = filteredX * translationScale,
                    y = filteredY * translationScale,
                    rotation = filteredRotation * rotationScale * (180f / PI.toFloat())
                )
                trySend(vector)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Using SENSOR_DELAY_GAME for high-frequency but battery-efficient sampling
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.onStart {
        // Emit an initial zero vector
        emit(MotionVector())
    }
}
