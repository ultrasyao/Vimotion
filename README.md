# Vimotion Android

Vimotion is a native Android video-editor UI foundation.

## Configuration
- Package: `com.vimotion.editor`
- Minimum Android: 12 / API 31
- Kotlin + Jetpack Compose
- C++ JNI / CMake native foundation
- Dark premium UI, purple accent
- Build target: debug APK

## Current screens
- Home / recent projects
- Create Project (name, resolution, aspect ratio, frame rate, background)
- Editor workspace mockup (preview area, transport controls, layer tracks, tool strip)
- Settings

## Build with GitHub Actions
1. Create a GitHub repository.
2. Upload the contents of this folder to the repository root.
3. Push to `main`, or run **Build Vimotion APK** from Actions.
4. Download artifact `Vimotion-debug`.
5. Install `app-debug.apk` on an Android 12+ device.

## Build locally
Requires JDK 17, Android SDK 35, CMake 3.22.1, and Gradle 8.9.

```bash
gradle --no-daemon :app:assembleDebug
```

## Important scope note
This is milestone 0.1.0: a UI and navigation foundation. Project creation currently stores projects in app memory only. Timeline tracks and editor controls are visual interaction prototypes, not a functioning non-linear video engine. Media import, persistent project serialization, real keyframes, graph interpolation, masking, effects, audio mixing, and export/rendering are future milestones.
