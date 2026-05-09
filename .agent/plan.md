# Project Plan

AntiMotion: An Android app that replicates iPhone's 'Vehicle Motion Cues' to reduce motion sickness.
Features:
- Quick Settings tile to toggle the feature on/off.
- No main Activity or UI (except for permission requests).
- Displays an overlay with animated dots that react to vehicle motion (accelerometer/gyroscope).
- Automatically activates the overlay when vehicle motion is detected (if toggled on).
- Requests 'Display over other apps' and 'Activity Recognition' permissions.
- Material Design 3 aesthetic for any system-provided UI (like permission dialogs or the tile).

## Project Brief

# AntiMotion Project Brief

AntiMotion is an Android utility designed to mitigate
 motion sickness by providing visual "Vehicle Motion Cues," similar to features found in modern mobile operating systems. It uses real-time
 sensor data to project an interactive overlay that synchronizes visual perception with physical movement.

## Features

- **Dynamic Motion Overlay**: A system-wide visual overlay featuring animated dots that react fluidly to vehicle acceleration and turns using the device's accelerometer
 and gyroscope.
- **Quick Settings Integration**: A dedicated System Tile allowing users to toggle the motion cue overlay on or off instantly
 from the notification shade.
- **Automated Vehicle Detection**: Utilizes activity recognition to automatically activate the motion cues when the device
 detects it is inside a moving vehicle.
- **Seamless Permission Management**: A streamlined Material 3 interface to handle necessary system-
level permissions, including "Display over other apps" and "Activity Recognition."

## High-Level Technical Stack

-
 **Language**: Kotlin
- **UI Framework**: Jetpack Compose (for the motion overlay and permission handling)
- **
Concurrency**: Kotlin Coroutines & Flow (for real-time sensor data processing)
- **Code Generation**: KSP
 (Kotlin Symbol Processing)
- **Android Core**: 
    - **SensorManager**: For high-frequency accelerometer and gyroscope sampling
.
    - **Foreground Services**: To ensure the overlay and motion detection remain active while using other apps.
    - **Activity
 Recognition API**: To detect driving/vehicular movement states.

## Implementation Steps
**Total Duration:** 49m 29s

### Task_1_Infrastructure: Set up the core infrastructure: MainActivity for permission handling (Overlay, Activity Recognition) with Edge-to-Edge support, the Quick Settings TileService, and the ForegroundService skeleton.
- **Status:** COMPLETED
- **Updates:** Implemented MainActivity for permission handling (Overlay, Activity Recognition, Post Notifications), MotionService (Foreground Service skeleton with notification), and MotionTileService (Quick Settings Tile). Verified successful build. Generated adaptive app icon.
- **Acceptance Criteria:**
  - MainActivity handles 'Display over other apps' and 'Activity Recognition' permissions
  - Quick Settings Tile correctly toggles the ForegroundService
  - Service displays a mandatory persistent notification
- **Duration:** 7m 34s

### Task_2_Sensor_Processing: Implement the SensorEngine to capture and process accelerometer and gyroscope data, streaming motion vectors via Kotlin Flow.
- **Status:** COMPLETED
- **Updates:** Implemented SensorEngine using callbackFlow for Accelerometer and Gyroscope. Applied low-pass filtering for smooth motion vectors. Integrated engine into MotionService with appropriate lifecycle management. Verified with successful build.
- **Acceptance Criteria:**
  - SensorManager collects high-frequency motion data
  - Raw data is processed into smooth motion vectors suitable for UI animation
  - Flow-based architecture ensures efficient data delivery to the service
- **Duration:** 1m 37s

### Task_3_Overlay_UI: Develop the Jetpack Compose-based overlay window using WindowManager, featuring animated dots that react to sensor motion signals with a vibrant M3 theme.
- **Status:** COMPLETED
- **Updates:** Developed MotionOverlayContent using Jetpack Compose Canvas with spring-based animations. Implemented MotionOverlayManager for WindowManager integration. Integrated overlay into MotionService. Implemented ActivityRecognitionManager for vehicle detection and PreferenceManager for settings. Verified successful build.
- **Acceptance Criteria:**
  - Overlay renders dots over other applications successfully
  - Animation logic translates motion vectors into fluid dot movement
  - UI uses a vibrant Material 3 color scheme and energetic aesthetic
- **Duration:** 6m 45s

### Task_4_Automation_Verification: Integrate Google Play Services Activity Recognition for automatic activation, create an adaptive app icon, and perform final stability verification.
- **Status:** COMPLETED
- **Updates:** Integrated Google Play Services Activity Recognition for automatic activation. Created adaptive app icon. Refined MainActivity with Material 3, Edge-to-Edge support, and moved all hardcoded strings to strings.xml. Verified successful build and basic functionality.
- **Acceptance Criteria:**
  - Activity Recognition automatically starts cues when vehicular movement is detected
  - Adaptive app icon is implemented and matches app function
  - App follows Material Design 3 and Edge-to-Edge guidelines
  - Build passes, all tests pass, and app does not crash during final verification
- **Duration:** 33m 33s

