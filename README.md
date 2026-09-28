# Kumar Video Editor (Android)

A modern, high-performance mobile video editor built with Kotlin and Jetpack Compose. Kumar Video Editor features multi-track timeline editing, real-time preview player, clip splitting & trimming, video filters, text and sticker overlays, soundtrack mixing, and video export tools.

## ✨ Features

- **Multi-Track Timeline**:
  - Interactive scrubbing ruler with timestamp markers (00:00, 00:05, 00:10...)
  - Glowing playhead indicator with tap-to-seek and drag-to-scrub support
  - Dedicated tracks for Video/Photos, Audio/Music, Text overlays, and Media stickers
  - Clip reordering, selection, splitting, duplicating, and deleting
- **Preview & Playback**:
  - Real-time video player screen with HUD showing current timecode, duration, active clip name, and resolution
  - Dynamic aspect ratio switching (16:9 Landscape, 9:16 Shorts/Reels, 1:1 Square, 4:5 Portrait)
  - Live filter grading & color matrix rendering
  - Live animated overlays for text titles and sticker badges
- **Media Import**:
  - Zero-permission Android Photo Picker for picking device videos (`PickVisualMedia.VideoOnly`) and photos (`PickVisualMedia.ImageOnly`)
  - Automatic photo conversion into 5-second video clips with simulated Ken-Burns zoom effect
  - Built-in sample presets for instant editing
- **Editing Tools**:
  - ✂️ **Split & Cut**: Split clips precisely at the playhead position into two segments; speed ramping (0.5x to 2x)
  - 🎵 **Audio Mixing**: Add background tracks, beats, and sound effects; volume control and mute toggles
  - 📝 **Text Overlays**: Add stylized titles and subtitles with customizable styles (Modern, Bold, Neon, Cinema, Typewriter), colors, and font sizes
  - 🖼️ **Media Stickers**: Place stickers and emojis onto the canvas at custom timestamp intervals
  - ✨ **Effects & Filters**: Color grading presets including Vivid Boost, Golden Warm, Cinema Cool, Vintage Sepia, Mono Noir, Moody Dark Vignette, Cyber Neon, and Dramatic Film
  - ⚡ **Transitions**: Direct Cut, Cross Dissolve, Fade to Black, Slide Left, Zoom In
  - 🤖 **AI Tools**: Auto Subtitles generator, AI Scene Splitter, Smart Audio Enhance, and Color Grade Match
- **Project Actions**:
  - Full Undo (↶) and Redo (↷) history stack
  - Export dialog with resolution options (1080p FHD, 4K UHD, 720p HD) and framerate selection (24fps, 30fps, 60fps)
  - Live export progress rendering and completion dialog

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2
- **UI Toolkit**: Jetpack Compose with Material 3 (Dark Studio Theme)
- **Architecture**: MVVM with Kotlin Coroutines and StateFlow
- **Image/Media Loading**: Coil Compose
- **Target SDK**: Android 36 (Android 15+)
- **Min SDK**: Android 26 (Android 8.0 Oreo)