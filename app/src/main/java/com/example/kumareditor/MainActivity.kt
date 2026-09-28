package com.example.kumareditor

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kumareditor.data.ActiveToolTab
import com.example.kumareditor.ui.EditorViewModel
import com.example.kumareditor.ui.components.HeaderBar
import com.example.kumareditor.ui.components.MultiTrackTimeline
import com.example.kumareditor.ui.components.Statusbar
import com.example.kumareditor.ui.components.ToolDialogContainer
import com.example.kumareditor.ui.components.ToolsRow
import com.example.kumareditor.ui.components.VideoPreviewArea
import com.example.kumareditor.ui.theme.KumarDarkBg
import com.example.kumareditor.ui.theme.KumarEditorTheme
import com.example.kumareditor.ui.theme.KumarHeaderBg
import com.example.kumareditor.ui.theme.KumarPrimary
import com.example.kumareditor.ui.theme.KumarTextPrimary
import com.example.kumareditor.ui.theme.KumarTextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KumarEditorTheme {
                val clips by viewModel.clips.collectAsState()
                val selectedClipId by viewModel.selectedClipId.collectAsState()
                val audioClips by viewModel.audioClips.collectAsState()
                val textOverlays by viewModel.textOverlays.collectAsState()
                val stickers by viewModel.stickers.collectAsState()
                val currentTimeMs by viewModel.currentTimeMs.collectAsState()
                val totalDurationMs by viewModel.totalDurationMs.collectAsState()
                val isPlaying by viewModel.isPlaying.collectAsState()
                val aspectRatio by viewModel.aspectRatio.collectAsState()
                val activeToolTab by viewModel.activeToolTab.collectAsState()
                val canUndo by viewModel.canUndo.collectAsState()
                val canRedo by viewModel.canRedo.collectAsState()
                val statusMessage by viewModel.statusMessage.collectAsState()
                val isConvertingPhoto by viewModel.isConvertingPhoto.collectAsState()
                val exportProgress by viewModel.exportProgress.collectAsState()
                val exportFinishedMessage by viewModel.exportFinishedMessage.collectAsState()

                // Zero-permission Android Photo Picker for Video
                val videoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri: Uri? ->
                    if (uri != null) {
                        val fileName = queryDisplayName(uri) ?: "Imported Video.mp4"
                        viewModel.importMedia(fileName, uri.toString(), isPhoto = false)
                    }
                }

                // Zero-permission Android Photo Picker for Photo
                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri: Uri? ->
                    if (uri != null) {
                        val fileName = queryDisplayName(uri) ?: "Imported Photo.jpg"
                        viewModel.importMedia(fileName, uri.toString(), isPhoto = true)
                    }
                }

                val selectedClip = clips.find { it.id == selectedClipId }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = KumarDarkBg,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 1. Top Header Bar
                        HeaderBar(
                            canUndo = canUndo,
                            canRedo = canRedo,
                            onUndo = { viewModel.undo() },
                            onRedo = { viewModel.redo() },
                            onExportClick = { viewModel.setActiveToolTab(ActiveToolTab.EXPORT) },
                            onSettingsClick = { viewModel.setActiveToolTab(ActiveToolTab.SETTINGS) }
                        )

                        // 2. Tools Menu Row
                        ToolsRow(
                            activeTab = activeToolTab,
                            onTabSelected = { tab ->
                                if (tab == ActiveToolTab.IMPORT) {
                                    viewModel.setActiveToolTab(ActiveToolTab.IMPORT)
                                } else {
                                    viewModel.setActiveToolTab(tab)
                                }
                            }
                        )

                        // 3. Center Preview Area
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            VideoPreviewArea(
                                clips = clips,
                                currentTimeMs = currentTimeMs,
                                totalDurationMs = totalDurationMs,
                                isPlaying = isPlaying,
                                aspectRatioPreset = aspectRatio,
                                isConvertingPhoto = isConvertingPhoto,
                                textOverlays = textOverlays,
                                stickers = stickers,
                                onTogglePlay = { viewModel.togglePlayPause() },
                                onImportVideoClick = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                onImportPhotoClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )
                        }

                        // 4. Multi-Track Timeline
                        MultiTrackTimeline(
                            clips = clips,
                            selectedClipId = selectedClipId,
                            audioClips = audioClips,
                            textOverlays = textOverlays,
                            stickers = stickers,
                            currentTimeMs = currentTimeMs,
                            totalDurationMs = totalDurationMs,
                            isPlaying = isPlaying,
                            onSeek = { ms -> viewModel.seekTo(ms) },
                            onTogglePlay = { viewModel.togglePlayPause() },
                            onJump = { deltaSec -> viewModel.jumpSeconds(deltaSec) },
                            onSelectClip = { clipId -> viewModel.selectClip(clipId) },
                            onSplitClip = { viewModel.splitClipAtPlayhead() },
                            onDuplicateClip = { viewModel.duplicateSelectedClip() },
                            onDeleteClip = { viewModel.deleteSelectedClip() },
                            onToggleAudioMute = { audioId -> viewModel.toggleAudioMute(audioId) }
                        )

                        // 5. Bottom Statusbar
                        Statusbar(
                            statusMessage = statusMessage,
                            exportProgress = exportProgress
                        )
                    }

                    // Active Tool Dialog/Sheet
                    ToolDialogContainer(
                        viewModel = viewModel,
                        activeTab = activeToolTab,
                        selectedClip = selectedClip,
                        onDismiss = { viewModel.setActiveToolTab(ActiveToolTab.NONE) },
                        onLaunchVideoPicker = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        onLaunchPhotoPicker = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    // Export Finished Notification Dialog
                    if (exportFinishedMessage != null) {
                        AlertDialog(
                            onDismissRequest = { viewModel.dismissExportFinished() },
                            containerColor = KumarHeaderBg,
                            title = {
                                Text(
                                    text = "🎬 Export Successful",
                                    color = KumarTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = exportFinishedMessage ?: "",
                                    color = KumarTextSecondary,
                                    fontSize = 13.sp
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = { viewModel.dismissExportFinished() },
                                    colors = ButtonDefaults.buttonColors(containerColor = KumarPrimary)
                                ) {
                                    Text("Done")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun queryDisplayName(uri: Uri): String? {
        var name: String? = null
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex)
                }
            }
        } catch (_: Exception) {
        }
        return name
    }
}
