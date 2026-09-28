package com.example.kumareditor.data

import java.util.UUID

object SampleProjects {

    fun createInitialClips(): List<MediaClip> = listOf(
        MediaClip(
            id = UUID.randomUUID().toString(),
            name = "Cyber City Neon.mp4",
            uriString = null,
            durationMs = 12000L,
            type = MediaType.VIDEO,
            filter = VideoFilter.NEON_CYBER,
            resolution = "1920 × 1080"
        ),
        MediaClip(
            id = UUID.randomUUID().toString(),
            name = "Ocean Drone Sunset.mp4",
            uriString = null,
            durationMs = 10000L,
            type = MediaType.VIDEO,
            filter = VideoFilter.WARM,
            resolution = "3840 × 2160"
        ),
        MediaClip(
            id = UUID.randomUUID().toString(),
            name = "Title Still_5s.webm",
            uriString = null,
            durationMs = 5000L,
            type = MediaType.PHOTO,
            filter = VideoFilter.NONE,
            resolution = "1920 × 1080"
        )
    )

    fun createInitialAudio(): List<AudioClip> = listOf(
        AudioClip(
            id = UUID.randomUUID().toString(),
            title = "Midnight Synthwave Beat",
            category = "Music",
            durationMs = 27000L,
            volume = 0.75f
        )
    )

    fun createInitialText(): List<TextOverlay> = listOf(
        TextOverlay(
            id = UUID.randomUUID().toString(),
            text = "KUMAR VIDEO EDITOR",
            startOffsetMs = 500L,
            durationMs = 4500L,
            colorHex = 0xFFFFFFFF,
            fontSizeSp = 24,
            styleName = "Modern"
        )
    )

    fun createInitialStickers(): List<StickerOverlay> = listOf(
        StickerOverlay(
            id = UUID.randomUUID().toString(),
            symbol = "🎬",
            name = "Clapper",
            startOffsetMs = 0L,
            durationMs = 3000L,
            xPercent = 0.5f,
            yPercent = 0.35f
        )
    )

    val availableSoundtracks = listOf(
        Pair("Midnight Synthwave Beat", "Music (3:45)"),
        Pair("Lo-Fi Study Chill", "Chillhop (2:30)"),
        Pair("Cinematic Trailer Drone", "Orchestral (1:50)"),
        Pair("Upbeat Pop Energy", "Pop (2:15)"),
        Pair("Ambient Horizon Pad", "Electronic (3:10)"),
        Pair("Ding Bell Notification", "SFX (0:02)"),
        Pair("Whoosh Transition Swoosh", "SFX (0:01)"),
        Pair("Vinyl Tape Rewind", "SFX (0:03)")
    )

    val availableStickers = listOf(
        "🎬" to "Clapperboard",
        "🔥" to "Fire",
        "✨" to "Sparkles",
        "⭐" to "Star",
        "🚀" to "Rocket",
        "🎧" to "Headphones",
        "💎" to "Diamond",
        "🏆" to "Trophy",
        "⚡" to "Lightning",
        "❤️" to "Heart",
        "🎥" to "Movie Camera",
        "💯" to "100 Marks"
    )

    val availableTransitions = listOf(
        "Direct Cut" to "Immediate switch between clips without blending",
        "Cross Dissolve" to "Smooth cinematic alpha blend between cuts",
        "Fade to Black" to "Classic dramatic dip to black before next scene",
        "Slide Left" to "Dynamic horizontal push transition",
        "Zoom In Pulse" to "High energy zoom blur punch cut"
    )

    val availableAiTools = listOf(
        Triple("Auto Subtitles", "Generate synchronized captions using speech detection", "📝"),
        Triple("AI Scene Splitter", "Automatically detect camera cuts & slice clips", "✂️"),
        Triple("Smart Audio Enhance", "Remove background hiss and optimize voice clarity", "🎵"),
        Triple("Color Grade Match", "AI matches lighting & tones across different clips", "🎨"),
        Triple("Speed Curve Assist", "Generate dynamic ramping slow-mo highlights", "⚡")
    )
}
