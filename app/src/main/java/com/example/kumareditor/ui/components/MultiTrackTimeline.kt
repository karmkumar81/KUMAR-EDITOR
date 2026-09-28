package com.example.kumareditor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kumareditor.data.AudioClip
import com.example.kumareditor.data.MediaClip
import com.example.kumareditor.data.MediaType
import com.example.kumareditor.data.StickerOverlay
import com.example.kumareditor.data.TextOverlay
import com.example.kumareditor.ui.theme.KumarAmber
import com.example.kumareditor.ui.theme.KumarBorder
import com.example.kumareditor.ui.theme.KumarBorderLight
import com.example.kumareditor.ui.theme.KumarCardBg
import com.example.kumareditor.ui.theme.KumarCardHover
import com.example.kumareditor.ui.theme.KumarDarkBg
import com.example.kumareditor.ui.theme.KumarHeaderBg
import com.example.kumareditor.ui.theme.KumarPlayhead
import com.example.kumareditor.ui.theme.KumarPrimary
import com.example.kumareditor.ui.theme.KumarPurple
import com.example.kumareditor.ui.theme.KumarRed
import com.example.kumareditor.ui.theme.KumarSecondary
import com.example.kumareditor.ui.theme.KumarTextMuted
import com.example.kumareditor.ui.theme.KumarTextPrimary
import com.example.kumareditor.ui.theme.KumarTextSecondary

@Composable
fun MultiTrackTimeline(
    clips: List<MediaClip>,
    selectedClipId: String?,
    audioClips: List<AudioClip>,
    textOverlays: List<TextOverlay>,
    stickers: List<StickerOverlay>,
    currentTimeMs: Long,
    totalDurationMs: Long,
    isPlaying: Boolean,
    onSeek: (Long) -> Unit,
    onTogglePlay: () -> Unit,
    onJump: (Int) -> Unit,
    onSelectClip: (String) -> Unit,
    onSplitClip: () -> Unit,
    onDuplicateClip: () -> Unit,
    onDeleteClip: () -> Unit,
    onToggleAudioMute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // Scale: 1000ms = 24.dp
    val dpPerSecond = 24.dp
    val msToDpFactor = dpPerSecond.value / 1000f

    val safeTotalSec = (totalDurationMs / 1000).coerceAtLeast(10L)
    val timelineWidthDp = (safeTotalSec * dpPerSecond.value + 200).dp

    val playheadOffsetDp = (currentTimeMs * msToDpFactor).dp

    // Auto-scroll to follow playhead when playing
    LaunchedEffect(currentTimeMs) {
        if (isPlaying) {
            val playheadPx = with(density) { playheadOffsetDp.toPx() }
            val viewportWidth = scrollState.viewportSize
            if (playheadPx > scrollState.value + viewportWidth - 200) {
                scrollState.animateScrollTo((playheadPx - viewportWidth / 2).toInt().coerceAtLeast(0))
            }
        }
    }

    fun formatSeconds(sec: Long): String {
        val m = sec / 60
        val s = sec % 60
        return "%02d:%02d".format(m, s)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KumarHeaderBg)
            .border(width = 1.dp, color = KumarBorder)
    ) {
        // Top Toolbar inside timeline: quick controls (Play/Pause, Seek -5s, +5s, Split, Duplicate, Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KumarCardBg)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Transport controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onJump(-5) },
                    modifier = Modifier.size(32.dp).testTag("jump_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Jump Back 5s",
                        tint = KumarTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(KumarPrimary)
                        .clickable { onTogglePlay() }
                        .testTag("timeline_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onJump(5) },
                    modifier = Modifier.size(32.dp).testTag("jump_forward_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Jump Forward 5s",
                        tint = KumarTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "${formatSeconds(currentTimeMs / 1000)} / ${formatSeconds(totalDurationMs / 1000)}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = KumarTextPrimary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Clip action shortcuts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Split button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(KumarCardHover)
                        .border(1.dp, KumarBorderLight, RoundedCornerShape(6.dp))
                        .clickable { onSplitClip() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("split_quick_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✂️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Split",
                            color = KumarTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Duplicate button
                IconButton(
                    onClick = onDuplicateClip,
                    modifier = Modifier.size(30.dp).testTag("duplicate_clip_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Duplicate Clip",
                        tint = KumarTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Delete button
                IconButton(
                    onClick = onDeleteClip,
                    modifier = Modifier.size(30.dp).testTag("delete_clip_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Clip",
                        tint = KumarRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Timeline tracks area with Left track headers and Right scrolling canvas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Track Headers column (fixed left sidebar)
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(KumarCardBg)
                    .border(width = 0.5.dp, color = KumarBorder)
            ) {
                // Ruler space
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .background(KumarDarkBg)
                        .border(width = 0.5.dp, color = KumarBorder),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Tracks",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = KumarTextMuted,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Video Track Label
                TrackLabelRow(
                    icon = "🎬",
                    label = "Video",
                    height = 54.dp
                )

                // Audio Track Label
                TrackLabelRow(
                    icon = "🎵",
                    label = "Audio",
                    height = 36.dp
                )

                // Text Track Label
                TrackLabelRow(
                    icon = "📝",
                    label = "Text",
                    height = 32.dp
                )

                // Media Track Label
                TrackLabelRow(
                    icon = "🖼️",
                    label = "Media",
                    height = 32.dp
                )
            }

            // Scrollable Timeline Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .width(timelineWidthDp)
                        .fillMaxHeight()
                        .pointerInput(timelineWidthDp) {
                            detectTapGestures { offset ->
                                val targetTimeMs = ((offset.x / density.density) / msToDpFactor).toLong()
                                onSeek(targetTimeMs)
                            }
                        }
                        .pointerInput(timelineWidthDp) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val targetTimeMs = ((change.position.x / density.density) / msToDpFactor).toLong()
                                onSeek(targetTimeMs)
                            }
                        }
                ) {
                    // Tracks rows
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Ruler row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .background(KumarDarkBg)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val tickCount = (safeTotalSec / 5).toInt() + 4
                                for (i in 0..tickCount) {
                                    val sec = i * 5L
                                    val x = sec * dpPerSecond.toPx()
                                    drawLine(
                                        color = Color(0xFF475569),
                                        start = Offset(x, 14.dp.toPx()),
                                        end = Offset(x, 26.dp.toPx()),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                            }

                            // Ruler labels
                            val tickCount = (safeTotalSec / 5).toInt() + 4
                            for (i in 0..tickCount) {
                                val sec = i * 5L
                                val xDp = (sec * dpPerSecond.value).dp
                                Text(
                                    text = formatSeconds(sec),
                                    fontSize = 9.sp,
                                    color = KumarTextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier
                                        .offset(x = xDp + 2.dp, y = 2.dp)
                                )
                            }
                        }

                        // Video Track row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .background(KumarCardBg.copy(alpha = 0.6f))
                                .border(0.5.dp, KumarBorder)
                                .padding(vertical = 4.dp)
                        ) {
                            var offsetMs = 0L
                            clips.forEach { clip ->
                                val clipWidthDp = (clip.effectiveDurationMs * msToDpFactor).dp.coerceAtLeast(40.dp)
                                val clipLeftDp = (offsetMs * msToDpFactor).dp
                                val isSelected = clip.id == selectedClipId

                                Box(
                                    modifier = Modifier
                                        .offset(x = clipLeftDp)
                                        .width(clipWidthDp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (clip.type == MediaType.PHOTO) {
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF5B21B6), Color(0xFF7C3AED))
                                                )
                                            } else {
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                                                )
                                            }
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) KumarPlayhead else KumarBorderLight,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onSelectClip(clip.id) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f, fill = false)
                                        ) {
                                            Text(
                                                text = if (clip.type == MediaType.PHOTO) "🖼️" else "🎬",
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = clip.name,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }
                                        Text(
                                            text = formatSeconds(clip.effectiveDurationMs / 1000),
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }
                                }
                                offsetMs += clip.effectiveDurationMs
                            }
                        }

                        // Audio Track row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .background(KumarDarkBg)
                                .border(0.5.dp, KumarBorder)
                                .padding(vertical = 3.dp)
                        ) {
                            if (audioClips.isEmpty()) {
                                Text(
                                    text = "No audio clips added",
                                    color = KumarTextMuted,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(start = 12.dp, top = 6.dp)
                                )
                            } else {
                                audioClips.forEach { audio ->
                                    val audioWidthDp = (audio.durationMs * msToDpFactor).dp.coerceAtLeast(60.dp)
                                    Box(
                                        modifier = Modifier
                                            .width(audioWidthDp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (audio.isMuted) Color(0xFF334155)
                                                else Color(0xFF065F46)
                                            )
                                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("🎵", fontSize = 10.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = audio.title,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    maxLines = 1
                                                )
                                            }
                                            IconButton(
                                                onClick = { onToggleAudioMute(audio.id) },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (audio.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                                    contentDescription = "Mute",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Text Track row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(KumarCardBg.copy(alpha = 0.4f))
                                .border(0.5.dp, KumarBorder)
                                .padding(vertical = 3.dp)
                        ) {
                            if (textOverlays.isEmpty()) {
                                Text(
                                    text = "No text overlays",
                                    color = KumarTextMuted,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                                )
                            } else {
                                textOverlays.forEach { textOverlay ->
                                    val textWidthDp = (textOverlay.durationMs * msToDpFactor).dp.coerceAtLeast(50.dp)
                                    val textLeftDp = (textOverlay.startOffsetMs * msToDpFactor).dp
                                    Box(
                                        modifier = Modifier
                                            .offset(x = textLeftDp)
                                            .width(textWidthDp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF701A75))
                                            .border(1.dp, Color(0xFFD946EF).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = "T: ${textOverlay.text}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // Media Track row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(KumarDarkBg)
                                .border(0.5.dp, KumarBorder)
                                .padding(vertical = 3.dp)
                        ) {
                            if (stickers.isEmpty()) {
                                Text(
                                    text = "No media stickers",
                                    color = KumarTextMuted,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                                )
                            } else {
                                stickers.forEach { sticker ->
                                    val stickerWidthDp = (sticker.durationMs * msToDpFactor).dp.coerceAtLeast(40.dp)
                                    val stickerLeftDp = (sticker.startOffsetMs * msToDpFactor).dp
                                    Box(
                                        modifier = Modifier
                                            .offset(x = stickerLeftDp)
                                            .width(stickerWidthDp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF78350F))
                                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = "${sticker.symbol} ${sticker.name}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Playhead vertical line with cursor handle
                    Box(
                        modifier = Modifier
                            .offset(x = playheadOffsetDp)
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(KumarPlayhead)
                    ) {
                        // Playhead top triangle handle
                        Box(
                            modifier = Modifier
                                .offset(x = (-6).dp, y = 0.dp)
                                .size(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(KumarPlayhead)
                                .shadow(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "▼",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackLabelRow(
    icon: String,
    label: String,
    height: androidx.compose.ui.unit.Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .border(width = 0.5.dp, color = KumarBorder)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = KumarTextPrimary
        )
    }
}
