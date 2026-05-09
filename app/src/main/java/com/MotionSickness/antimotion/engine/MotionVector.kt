package com.MotionSickness.antimotion.engine

/**
 * Represents the processed motion data.
 * @param x Horizontal translation (from accelerometer/gyroscope mix)
 * @param y Vertical translation
 * @param rotation Rotation angle in degrees
 */
data class MotionVector(
    val x: Float = 0f,
    val y: Float = 0f,
    val rotation: Float = 0f
)
