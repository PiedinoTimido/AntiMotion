package com.MotionSickness.antimotion.ui.overlay

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.MotionSickness.antimotion.engine.MotionVector
import com.MotionSickness.antimotion.ui.theme.AntiMotionTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

@Composable
fun MotionOverlay(motionFlow: Flow<MotionVector>) {
    val motionVector by motionFlow.collectAsState(initial = MotionVector())
    
    // Smooth transitions for the motion values to add that "expressive" feel
    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }
    val animRotation = remember { Animatable(0f) }
    
    // Global alpha to fade out the entire overlay when not moving
    val globalAlpha = remember { Animatable(0f) }

    LaunchedEffect(motionVector) {
        launch {
            animX.animateTo(
                targetValue = motionVector.x,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            animY.animateTo(
                targetValue = motionVector.y,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            animRotation.animateTo(
                targetValue = motionVector.rotation,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        launch {
            // Determine if the device is moving based on vector magnitude
            val magnitude = sqrt(motionVector.x * motionVector.x + motionVector.y * motionVector.y)
            val isMoving = magnitude > 5f || abs(motionVector.rotation) > 1f
            
            globalAlpha.animateTo(
                targetValue = if (isMoving) 1f else 0f,
                animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
            )
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    
    if (globalAlpha.value > 0.01f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            // Calculate intensity for dynamic dot size
            val intensity = (abs(animX.value) + abs(animY.value)) / 100f
            val dotRadiusBase = 12f * (1f + intensity.coerceIn(0f, 0.5f))
            
            rotate(animRotation.value, pivot = Offset(centerX, centerY)) {
                val spacing = 160f
                
                // Overdraw to prevent empty spaces during extreme motion
                val cols = (size.width / spacing).toInt() + 8
                val rows = (size.height / spacing).toInt() + 8
                
                for (i in -cols/2..cols/2) {
                    for (j in -rows/2..rows/2) {
                        val xPos = centerX + i * spacing + animX.value
                        val yPos = centerY + j * spacing + animY.value
                        
                        val distFromCenter = Offset(xPos - centerX, yPos - centerY).getDistance()
                        val maxDist = size.minDimension / 1.1f
                        
                        // Fade out dots towards the center and far edges
                        // This creates a "portal" effect that is less intrusive
                        var alpha = if (distFromCenter < 100f) {
                            (distFromCenter / 100f) * 0.4f
                        } else {
                            (1f - (distFromCenter / maxDist)).coerceIn(0f, 0.5f)
                        }
                        
                        // Apply the global movement-based alpha
                        alpha *= globalAlpha.value
                        
                        if (alpha > 0.05f) {
                            val color = if ((i + j) % 2 == 0) primaryColor else tertiaryColor
                            
                            drawCircle(
                                color = color.copy(alpha = alpha),
                                radius = dotRadiusBase,
                                center = Offset(xPos, yPos)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MotionOverlayPreview() {
    AntiMotionTheme {
        MotionOverlay(
            motionFlow = flowOf(
                MotionVector(x = 60f, y = -40f, rotation = 20f)
            )
        )
    }
}
