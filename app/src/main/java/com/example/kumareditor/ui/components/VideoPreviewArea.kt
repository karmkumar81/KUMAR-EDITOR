package com.example.kumareditor.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kumareditor.data.AspectRatioPreset
import com.example.kumareditor.data.MediaClip
import com.example.kumareditor.data.MediaType
import com.example.kumareditor.data.StickerOverlay
import com.example.kumareditor.data.TextOverlay
import com.example.kumareditor.data.VideoFilter
import com.example.kumareditor.ui.theme.KumarAccent
import com.example.kumareditor.ui.theme.KumarAmber
import com.example.kumareditor.ui.theme.KumarBorder
import com.example.kumareditor.ui.theme.KumarCardBg
import com.example.kumareditor.ui.theme.KumarCardHover
import com.example.kumareditor.ui.theme.KumarDarkBg
import com.example.kumareditor.ui.theme.KumarHeaderBg
import com.example.kumareditor.ui.theme.KumarPlayhead
import com.example.kumareditor.ui.theme.KumarPrimary
import com.example.kumareditor.ui.theme.KumarSecondary
import com.example.kumareditor.ui.theme.KumarTextMuted
import com.example.kumareditor.ui.theme.KumarTextPrimary
import com.example.kumareditor.ui.theme.KumarTextSecondary

@Composable
fun VideoPreviewArea(
    clips: List<MediaClip>,
    currentTimeMs: Long,
    totalDurationMs: Long,
    isPlaying: Boolean,
    aspectRatioPreset: AspectRatioPreset,
    isConvertingPhoto: Boolean,
    textOverlays: List<TextOverlay>,
    stickers: List<StickerOverlay>,
    onTogglePlay: () -> Unit,
    onImportVideoClick: () -> Unit,
    onImportPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Find active clip at current time
    var accumulated = 0L
    var activeClip: MediaClip? = null
    var activeClipProgress = 0f

    for (clip in clips) {
        val duration = clip.effectiveDurationMs
        if (currentTimeMs in accumulated until (accumulated + duration)) {
            activeClip = clip
            activeClipProgress = ((currentTimeMs - accumulated).toFloat() / duration).coerceIn(0f, 1f)
            break
        }
        accumulated += duration
    }

    if (activeClip == null && clips.isNotEmpty()) {
        activeClip = clips.last()
        activeClipProgress = 1f
    }

    // Active text overlays at this time
    val visibleTexts = textOverlays.filter {
        currentTimeMs in it.startOffsetMs..(it.startOffsetMs + it.durationMs)
    }

    // Active stickers at this time
    val visibleStickers = stickers.filter {
        currentTimeMs in it.startOffsetMs..(it.startOffsetMs + it.durationMs)
    }

    fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val mins = totalSec / 60
        val secs = totalSec % 60
        return "%02d:%02d".format(mins, secs)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KumarDarkBg)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Screen Box with Aspect Ratio
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val aspect = aspectRatioPreset.ratioWidth / aspectRatioPreset.ratioHeight

            Box(
                modifier = Modifier
                    .aspectRatio(aspect, matchHeightConstraintsFirst = true)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .border(1.dp, KumarBorder, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (clips.isNotEmpty()) onTogglePlay()
                    }
                    .testTag("preview_display_frame"),
                contentAlignment = Alignment.Center
            ) {
                if (clips.isEmpty()) {
                    // Empty state matching original design
                    EmptyPreviewState(
                        isConverting = isConvertingPhoto,
                        onImportVideo = onImportVideoClick,
                        onImportPhoto = onImportPhotoClick
                    )
                } else {
                    // Active Video Canvas Preview
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = maxWidth
                        val canvasHeight = maxHeight

                        // Clip visual background
                        if (activeClip?.uriString != null) {
                            AsyncImage(
                                model = activeClip.uriString,
                                contentDescription = activeClip.name,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Synthesized dynamic canvas frame representing sample / live video
                            SynthesizedVideoCanvas(
                                clip = activeClip ?: clips.first(),
                                progress = activeClipProgress,
                                isPlaying = isPlaying
                            )
                        }

                        // Filter tint overlay
                        ApplyFilterOverlay(filter = activeClip?.filter ?: VideoFilter.NONE)

                        // Render visible text overlays
                        visibleTexts.forEach { textOverlay ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset {
                                        IntOffset(
                                            x = ((canvasWidth.toPx() * textOverlay.xPercent) - 100).toInt().coerceAtLeast(10),
                                            y = (canvasHeight.toPx() * textOverlay.yPercent).toInt().coerceAtLeast(10)
                                        )
                                    }
                                    .background(
                                        Color.Black.copy(alpha = 0.45f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = textOverlay.text,
                                    color = Color(textOverlay.colorHex),
                                    fontSize = textOverlay.fontSizeSp.sp,
                                    fontWeight = if (textOverlay.styleName == "Bold") FontWeight.Black else FontWeight.Bold,
                                    fontFamily = if (textOverlay.styleName == "Typewriter") FontFamily.Monospace else FontFamily.SansSerif,
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = Color.Black,
                                            offset = Offset(2f, 2f),
                                            blurRadius = 4f
                                        )
                                    )
                                )
                            }
                        }

                        // Render visible stickers
                        visibleStickers.forEach { sticker ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset {
                                        IntOffset(
                                            x = (canvasWidth.toPx() * sticker.xPercent).toInt(),
                                            y = (canvasHeight.toPx() * sticker.yPercent).toInt()
                                        )
                                    }
                            ) {
                                Text(
                                    text = sticker.symbol,
                                    fontSize = 42.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Subtle play / pause indicator pulse on canvas
                        if (!isPlaying && clips.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .border(1.dp, KumarPlayhead, CircleShape)
                                    .align(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Photo conversion overlay
                if (isConvertingPhoto) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = KumarPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Converting photo to video...",
                                color = KumarTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Creating a 5-second video",
                                color = KumarTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // HUD Info Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KumarHeaderBg, RoundedCornerShape(6.dp))
                .border(0.5.dp, KumarBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Time: ",
                    color = KumarTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "${formatTime(currentTimeMs)} / ${formatTime(totalDurationMs)}",
                    color = KumarTextPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Resolution
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Resolution: ",
                    color = KumarTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = activeClip?.resolution ?: "1920 × 1080",
                    color = KumarTextPrimary,
                    fontSize = 12.sp
                )
            }

            // Active File
            if (activeClip != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Clip: ",
                        color = KumarTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = activeClip.name.take(18) + if (activeClip.name.length > 18) "..." else "",
                        color = KumarAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPreviewState(
    isConverting: Boolean,
    onImportVideo: () -> Unit,
    onImportPhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎬",
            fontSize = 44.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start your project",
            color = KumarTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Import a video or photo to begin editing",
            color = KumarTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onImportVideo,
                enabled = !isConverting,
                colors = ButtonDefaults.buttonColors(containerColor = KumarPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("import_video_empty_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.VideoFile,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Import Video", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("MP4, WebM, MOV", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            OutlinedButton(
                onClick = onImportPhoto,
                enabled = !isConverting,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KumarBorder),
                modifier = Modifier.testTag("import_photo_empty_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = KumarTextPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Import Photo", color = KumarTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Creates 5s clip", color = KumarTextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun SynthesizedVideoCanvas(
    clip: MediaClip,
    progress: Float,
    isPlaying: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Dynamic cinematic background based on clip name & type
        val brush = when {
            clip.name.contains("Cyber", ignoreCase = true) -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF0F172A),
                    Color(0xFF311042),
                    Color(0xFF064E3B)
                ),
                start = Offset(0f, 0f),
                end = Offset(width, height)
            )
            clip.name.contains("Ocean", ignoreCase = true) -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0C4A6E),
                    Color(0xFF0369A1),
                    Color(0xFFF97316)
                )
            )
            clip.type == MediaType.PHOTO -> Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4338CA),
                    Color(0xFF1E1B4B)
                ),
                center = Offset(width / 2, height / 2),
                radius = width * (0.6f + progress * 0.2f) // Ken-Burns zoom simulation
            )
            else -> Brush.linearGradient(
                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
            )
        }

        drawRect(brush = brush)

        // Geometric grid overlay representing digital studio timeline
        val gridStep = 40.dp.toPx()
        var x = 0f
        while (x < width) {
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridStep
        }
        var y = 0f
        while (y < height) {
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridStep
        }

        // Motion scanning line indicating playback
        if (isPlaying) {
            val scanX = (progress * width + waveOffset) % width
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.25f),
                start = Offset(scanX, 0f),
                end = Offset(scanX, height),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun ApplyFilterOverlay(filter: VideoFilter) {
    when (filter) {
        VideoFilter.NONE -> {}
        VideoFilter.VIVID -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFF0055).copy(alpha = 0.08f))
        )
        VideoFilter.WARM -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
        )
        VideoFilter.COOL -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0284C7).copy(alpha = 0.15f))
        )
        VideoFilter.SEPIA -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF78350F).copy(alpha = 0.25f))
        )
        VideoFilter.GRAYSCALE -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000).copy(alpha = 0.35f))
        )
        VideoFilter.VIGNETTE -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                    )
                )
        )
        VideoFilter.NEON_CYBER -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00F0FF).copy(alpha = 0.12f),
                            Color(0xFFFF003C).copy(alpha = 0.12f)
                        )
                    )
                )
        )
        VideoFilter.DRAMATIC -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )
    }
}
