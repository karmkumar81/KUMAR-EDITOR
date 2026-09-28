package com.example.kumareditor.data

enum class MediaType {
    VIDEO,
    PHOTO
}

enum class VideoFilter(val displayName: String, val badge: String) {
    NONE("Original", "✨"),
    VIVID("Vivid Boost", "🌟"),
    WARM("Golden Warm", "☀️"),
    COOL("Cinema Cool", "❄️"),
    SEPIA("Vintage Sepia", "🎞️"),
    GRAYSCALE("Mono Noir", "🖤"),
    VIGNETTE("Moody Dark", "🌑"),
    NEON_CYBER("Cyber Neon", "⚡"),
    DRAMATIC("Dramatic Film", "🎭")
}

enum class AspectRatioPreset(val label: String, val ratioWidth: Float, val ratioHeight: Float) {
    RATIO_16_9("16:9 Widescreen", 16f, 9f),
    RATIO_9_16("9:16 Shorts/Reels", 9f, 16f),
    RATIO_1_1("1:1 Square", 1f, 1f),
    RATIO_4_5("4:5 Instagram", 4f, 5f)
}

data class MediaClip(
    val id: String,
    val name: String,
    val uriString: String? = null,
    val durationMs: Long,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    val type: MediaType = MediaType.VIDEO,
    val filter: VideoFilter = VideoFilter.NONE,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val resolution: String = "1920 × 1080"
) {
    val effectiveDurationMs: Long
        get() {
            val net = (durationMs - trimStartMs - trimEndMs).coerceAtLeast(500L)
            return (net / speed).toLong()
        }
}

data class AudioClip(
    val id: String,
    val title: String,
    val category: String,
    val durationMs: Long,
    val volume: Float = 0.8f,
    val isMuted: Boolean = false
)

data class TextOverlay(
    val id: String,
    val text: String,
    val startOffsetMs: Long = 0L,
    val durationMs: Long = 5000L,
    val colorHex: Long = 0xFFFFFFFF,
    val fontSizeSp: Int = 22,
    val styleName: String = "Modern",
    val xPercent: Float = 0.5f,
    val yPercent: Float = 0.75f
)

data class StickerOverlay(
    val id: String,
    val symbol: String,
    val name: String,
    val startOffsetMs: Long = 0L,
    val durationMs: Long = 4000L,
    val xPercent: Float = 0.5f,
    val yPercent: Float = 0.35f
)

enum class ActiveToolTab {
    NONE,
    IMPORT,
    SPLIT,
    AUDIO,
    TEXT,
    MEDIA,
    EFFECTS,
    TRANSITIONS,
    AI_TOOLS,
    SETTINGS,
    EXPORT
}

data class ProjectHistoryState(
    val clips: List<MediaClip>,
    val audioClips: List<AudioClip>,
    val textOverlays: List<TextOverlay>,
    val stickers: List<StickerOverlay>,
    val selectedClipId: String?
)
