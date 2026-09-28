package com.example.kumareditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kumareditor.data.ActiveToolTab
import com.example.kumareditor.data.AspectRatioPreset
import com.example.kumareditor.data.MediaClip
import com.example.kumareditor.data.SampleProjects
import com.example.kumareditor.data.VideoFilter
import com.example.kumareditor.ui.EditorViewModel
import com.example.kumareditor.ui.theme.KumarAccent
import com.example.kumareditor.ui.theme.KumarAmber
import com.example.kumareditor.ui.theme.KumarBorder
import com.example.kumareditor.ui.theme.KumarBorderLight
import com.example.kumareditor.ui.theme.KumarCardBg
import com.example.kumareditor.ui.theme.KumarCardHover
import com.example.kumareditor.ui.theme.KumarDarkBg
import com.example.kumareditor.ui.theme.KumarHeaderBg
import com.example.kumareditor.ui.theme.KumarPlayhead
import com.example.kumareditor.ui.theme.KumarPrimary
import com.example.kumareditor.ui.theme.KumarRed
import com.example.kumareditor.ui.theme.KumarSecondary
import com.example.kumareditor.ui.theme.KumarTextMuted
import com.example.kumareditor.ui.theme.KumarTextPrimary
import com.example.kumareditor.ui.theme.KumarTextSecondary

@Composable
fun ToolDialogContainer(
    viewModel: EditorViewModel,
    activeTab: ActiveToolTab,
    selectedClip: MediaClip?,
    onDismiss: () -> Unit,
    onLaunchVideoPicker: () -> Unit,
    onLaunchPhotoPicker: () -> Unit
) {
    if (activeTab == ActiveToolTab.NONE) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KumarHeaderBg,
        titleContentColor = KumarTextPrimary,
        textContentColor = KumarTextSecondary,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (activeTab) {
                        ActiveToolTab.IMPORT -> "📥 Import Media"
                        ActiveToolTab.SPLIT -> "✂️ Split & Trim Clip"
                        ActiveToolTab.AUDIO -> "🎵 Audio & Sound Effects"
                        ActiveToolTab.TEXT -> "📝 Add Text Overlay"
                        ActiveToolTab.MEDIA -> "🖼️ Media & Stickers"
                        ActiveToolTab.EFFECTS -> "✨ Video Effects & Filters"
                        ActiveToolTab.TRANSITIONS -> "⚡ Clip Transitions"
                        ActiveToolTab.AI_TOOLS -> "🤖 AI Editing Tools"
                        ActiveToolTab.SETTINGS -> "⚙ Project Settings"
                        ActiveToolTab.EXPORT -> "🚀 Export Video"
                        ActiveToolTab.NONE -> ""
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = KumarTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            when (activeTab) {
                ActiveToolTab.IMPORT -> ImportDialogContent(
                    onImportVideo = {
                        onDismiss()
                        onLaunchVideoPicker()
                    },
                    onImportPhoto = {
                        onDismiss()
                        onLaunchPhotoPicker()
                    },
                    onAddSampleClip = { name, isPhoto ->
                        viewModel.importMedia(name, null, isPhoto)
                        onDismiss()
                    }
                )
                ActiveToolTab.SPLIT -> SplitDialogContent(
                    selectedClip = selectedClip,
                    onSplit = {
                        viewModel.splitClipAtPlayhead()
                        onDismiss()
                    },
                    onDuplicate = {
                        viewModel.duplicateSelectedClip()
                        onDismiss()
                    },
                    onDelete = {
                        viewModel.deleteSelectedClip()
                        onDismiss()
                    },
                    onSpeedChange = { speed ->
                        viewModel.setClipSpeed(speed)
                    }
                )
                ActiveToolTab.AUDIO -> AudioDialogContent(
                    onAddAudio = { title, category, durationMs ->
                        viewModel.addAudioTrack(title, category, durationMs)
                        onDismiss()
                    }
                )
                ActiveToolTab.TEXT -> TextDialogContent(
                    onAddText = { text, style, colorHex, fontSize ->
                        viewModel.addTextOverlay(text, style, colorHex, fontSize)
                        onDismiss()
                    }
                )
                ActiveToolTab.MEDIA -> MediaStickersDialogContent(
                    onAddSticker = { emoji, name ->
                        viewModel.addSticker(emoji, name)
                        onDismiss()
                    }
                )
                ActiveToolTab.EFFECTS -> EffectsDialogContent(
                    currentFilter = selectedClip?.filter ?: VideoFilter.NONE,
                    onSelectFilter = { filter ->
                        viewModel.setClipFilter(filter)
                    }
                )
                ActiveToolTab.TRANSITIONS -> TransitionsDialogContent(onDismiss = onDismiss)
                ActiveToolTab.AI_TOOLS -> AiToolsDialogContent(
                    onTriggerAi = { toolName ->
                        when (toolName) {
                            "Auto Subtitles" -> {
                                viewModel.addTextOverlay("Generated by AI Studio", "Cinema", 0xFF38BDF8, 20)
                            }
                            "AI Scene Splitter" -> {
                                viewModel.splitClipAtPlayhead()
                            }
                            "Smart Audio Enhance" -> {
                                viewModel.addAudioTrack("AI Enhanced Master Track", "Audio", 25000L)
                            }
                            "Color Grade Match" -> {
                                viewModel.setClipFilter(VideoFilter.DRAMATIC)
                            }
                            "Speed Curve Assist" -> {
                                viewModel.setClipSpeed(1.5f)
                            }
                        }
                        onDismiss()
                    }
                )
                ActiveToolTab.SETTINGS -> SettingsDialogContent(
                    viewModel = viewModel,
                    onDismiss = onDismiss
                )
                ActiveToolTab.EXPORT -> ExportDialogContent(
                    onStartExport = { res, fps ->
                        viewModel.startExport(res, fps)
                        onDismiss()
                    }
                )
                ActiveToolTab.NONE -> {}
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ImportDialogContent(
    onImportVideo: () -> Unit,
    onImportPhoto: () -> Unit,
    onAddSampleClip: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onImportVideo,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KumarPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("🎥 Select Video from Device", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onImportPhoto,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, KumarBorder)
        ) {
            Text("🖼️ Select Photo (Creates 5s Video)", color = KumarTextPrimary)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Or quick add demo clips:",
            fontSize = 12.sp,
            color = KumarTextMuted,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(KumarCardBg)
                    .border(1.dp, KumarBorder, RoundedCornerShape(6.dp))
                    .clickable { onAddSampleClip("Neon Drive 4K.mp4", false) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🎬 Neon Drive", fontSize = 11.sp, color = KumarTextPrimary)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(KumarCardBg)
                    .border(1.dp, KumarBorder, RoundedCornerShape(6.dp))
                    .clickable { onAddSampleClip("Sunset Horizon.jpg", true) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🖼️ Sunset Still", fontSize = 11.sp, color = KumarTextPrimary)
            }
        }
    }
}

@Composable
private fun SplitDialogContent(
    selectedClip: MediaClip?,
    onSplit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    var speed by remember(selectedClip) { mutableFloatStateOf(selectedClip?.speed ?: 1.0f) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (selectedClip != null) {
            Text(
                text = "Target Clip: ${selectedClip.name}",
                fontSize = 13.sp,
                color = KumarAccent,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = "No clip selected. Move playhead inside a clip to edit.",
                fontSize = 12.sp,
                color = KumarTextMuted
            )
        }

        Button(
            onClick = onSplit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KumarPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("✂️ Cut / Split at Playhead", fontWeight = FontWeight.Bold)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onDuplicate,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KumarBorder)
            ) {
                Text("Duplicate", color = KumarTextPrimary)
            }

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KumarRed.copy(alpha = 0.5f))
            ) {
                Text("Delete", color = KumarRed)
            }
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Playback Speed", fontSize = 12.sp, color = KumarTextSecondary)
                Text("${speed}x", fontSize = 12.sp, color = KumarTextPrimary, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = speed,
                onValueChange = {
                    speed = it
                    onSpeedChange(it)
                },
                valueRange = 0.5f..2.0f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = KumarPrimary,
                    activeTrackColor = KumarPrimary,
                    inactiveTrackColor = KumarBorderLight
                )
            )
        }
    }
}

@Composable
private fun AudioDialogContent(
    onAddAudio: (String, String, Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.height(280.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SampleProjects.availableSoundtracks) { (title, meta) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(KumarCardBg)
                    .border(1.dp, KumarBorder, RoundedCornerShape(8.dp))
                    .clickable { onAddAudio(title, meta, 25000L) }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = KumarAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KumarTextPrimary)
                        Text(text = meta, fontSize = 11.sp, color = KumarTextSecondary)
                    }
                }
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Track",
                    tint = KumarTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TextDialogContent(
    onAddText: (String, String, Long, Int) -> Unit
) {
    var textInput by remember { mutableStateOf("New Title") }
    var selectedStyle by remember { mutableStateOf("Modern") }
    var selectedColor by remember { mutableLongStateOf(0xFFFFFFFF) }
    var fontSize by remember { mutableIntStateOf(24) }

    val styles = listOf("Modern", "Bold", "Neon", "Cinema", "Typewriter")
    val colors = listOf(
        0xFFFFFFFF to "White",
        0xFFFACC15 to "Yellow",
        0xFF38BDF8 to "Cyan",
        0xFFEC4899 to "Pink",
        0xFF10B981 to "Green",
        0xFFEF4444 to "Red"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Overlay Text") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = KumarPrimary,
                unfocusedBorderColor = KumarBorder
            )
        )

        Text("Style", fontSize = 12.sp, color = KumarTextSecondary)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            styles.forEach { style ->
                val isSelected = selectedStyle == style
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) KumarPrimary else KumarCardBg)
                        .border(1.dp, if (isSelected) KumarPrimary else KumarBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedStyle = style }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = style,
                        fontSize = 11.sp,
                        color = if (isSelected) Color.White else KumarTextSecondary
                    )
                }
            }
        }

        Text("Color", fontSize = 12.sp, color = KumarTextSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            colors.forEach { (colorValue, _) ->
                val isSelected = selectedColor == colorValue
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(colorValue))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) KumarPlayhead else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = colorValue },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (textInput.isNotBlank()) {
                    onAddText(textInput, selectedStyle, selectedColor, fontSize)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KumarPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Add Text to Timeline", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MediaStickersDialogContent(
    onAddSticker: (String, String) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SampleProjects.availableStickers.forEach { (emoji, name) ->
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(KumarCardBg)
                    .border(1.dp, KumarBorder, RoundedCornerShape(8.dp))
                    .clickable { onAddSticker(emoji, name) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = emoji, fontSize = 24.sp)
                    Text(text = name, fontSize = 9.sp, color = KumarTextSecondary)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EffectsDialogContent(
    currentFilter: VideoFilter,
    onSelectFilter: (VideoFilter) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VideoFilter.values().forEach { filter ->
            val isSelected = filter == currentFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) KumarPrimary.copy(alpha = 0.3f) else KumarCardBg)
                    .border(
                        1.dp,
                        if (isSelected) KumarPrimary else KumarBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectFilter(filter) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = filter.badge, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = filter.displayName,
                        fontSize = 12.sp,
                        color = if (isSelected) KumarTextPrimary else KumarTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun TransitionsDialogContent(onDismiss: () -> Unit) {
    LazyColumn(
        modifier = Modifier.height(260.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SampleProjects.availableTransitions) { (title, desc) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDismiss() },
                colors = CardDefaults.cardColors(containerColor = KumarCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, KumarBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = title, fontWeight = FontWeight.Bold, color = KumarTextPrimary, fontSize = 13.sp)
                    Text(text = desc, color = KumarTextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AiToolsDialogContent(onTriggerAi: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.height(280.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SampleProjects.availableAiTools) { (title, desc, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(KumarCardBg)
                    .border(1.dp, KumarBorder, RoundedCornerShape(8.dp))
                    .clickable { onTriggerAi(title) }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, color = KumarTextPrimary, fontSize = 13.sp)
                    Text(text = desc, color = KumarTextSecondary, fontSize = 11.sp)
                }
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = KumarAmber,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsDialogContent(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit
) {
    val presets = listOf(
        AspectRatioPreset.RATIO_16_9,
        AspectRatioPreset.RATIO_9_16,
        AspectRatioPreset.RATIO_1_1,
        AspectRatioPreset.RATIO_4_5
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Canvas Aspect Ratio", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KumarTextPrimary)
        presets.forEach { preset ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(KumarCardBg)
                    .border(1.dp, KumarBorder, RoundedCornerShape(6.dp))
                    .clickable {
                        viewModel.setAspectRatio(preset)
                        onDismiss()
                    }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📐", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = preset.label, fontSize = 12.sp, color = KumarTextPrimary)
            }
        }
    }
}

@Composable
private fun ExportDialogContent(
    onStartExport: (String, Int) -> Unit
) {
    var resolution by remember { mutableStateOf("1080p FHD") }
    var fps by remember { mutableIntStateOf(30) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Export Quality Preset", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KumarTextPrimary)

        listOf("1080p FHD (1920×1080)", "4K UHD (3840×2160)", "720p HD (1280×720)").forEach { res ->
            val label = res.substringBefore(" ")
            val isSelected = resolution == label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) KumarPrimary.copy(alpha = 0.25f) else KumarCardBg)
                    .border(1.dp, if (isSelected) KumarPrimary else KumarBorder, RoundedCornerShape(6.dp))
                    .clickable { resolution = label }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = res,
                    fontSize = 12.sp,
                    color = if (isSelected) KumarTextPrimary else KumarTextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Text("Frame Rate", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KumarTextPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(24, 30, 60).forEach { rate ->
                val isSelected = fps == rate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) KumarPrimary else KumarCardBg)
                        .clickable { fps = rate }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$rate fps",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = { onStartExport(resolution, fps) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KumarPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("🚀 Render & Export Video", fontWeight = FontWeight.Bold)
        }
    }
}
