package com.example.kumareditor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumareditor.data.ActiveToolTab
import com.example.kumareditor.data.AspectRatioPreset
import com.example.kumareditor.data.AudioClip
import com.example.kumareditor.data.MediaClip
import com.example.kumareditor.data.MediaType
import com.example.kumareditor.data.ProjectHistoryState
import com.example.kumareditor.data.SampleProjects
import com.example.kumareditor.data.StickerOverlay
import com.example.kumareditor.data.TextOverlay
import com.example.kumareditor.data.VideoFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class EditorViewModel : ViewModel() {

    private val _clips = MutableStateFlow<List<MediaClip>>(SampleProjects.createInitialClips())
    val clips: StateFlow<List<MediaClip>> = _clips.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(_clips.value.firstOrNull()?.id)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _audioClips = MutableStateFlow<List<AudioClip>>(SampleProjects.createInitialAudio())
    val audioClips: StateFlow<List<AudioClip>> = _audioClips.asStateFlow()

    private val _textOverlays = MutableStateFlow<List<TextOverlay>>(SampleProjects.createInitialText())
    val textOverlays: StateFlow<List<TextOverlay>> = _textOverlays.asStateFlow()

    private val _stickers = MutableStateFlow<List<StickerOverlay>>(SampleProjects.createInitialStickers())
    val stickers: StateFlow<List<StickerOverlay>> = _stickers.asStateFlow()

    private val _currentTimeMs = MutableStateFlow(0L)
    val currentTimeMs: StateFlow<Long> = _currentTimeMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _aspectRatio = MutableStateFlow(AspectRatioPreset.RATIO_16_9)
    val aspectRatio: StateFlow<AspectRatioPreset> = _aspectRatio.asStateFlow()

    private val _activeToolTab = MutableStateFlow(ActiveToolTab.NONE)
    val activeToolTab: StateFlow<ActiveToolTab> = _activeToolTab.asStateFlow()

    private val _statusMessage = MutableStateFlow("Status : Ready ✅")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _isConvertingPhoto = MutableStateFlow(false)
    val isConvertingPhoto: StateFlow<Boolean> = _isConvertingPhoto.asStateFlow()

    private val _exportProgress = MutableStateFlow<Float?>(null)
    val exportProgress: StateFlow<Float?> = _exportProgress.asStateFlow()

    private val _exportFinishedMessage = MutableStateFlow<String?>(null)
    val exportFinishedMessage: StateFlow<String?> = _exportFinishedMessage.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<ProjectHistoryState>()
    private val redoStack = mutableListOf<ProjectHistoryState>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    val totalDurationMs: StateFlow<Long> = _clips.combine(_audioClips) { currentClips, _ ->
        currentClips.sumOf { it.effectiveDurationMs }.coerceAtLeast(1000L)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 27000L)

    private var playbackJob: Job? = null

    init {
        pushHistorySnapshot()
    }

    private fun pushHistorySnapshot() {
        val snapshot = ProjectHistoryState(
            clips = _clips.value,
            audioClips = _audioClips.value,
            textOverlays = _textOverlays.value,
            stickers = _stickers.value,
            selectedClipId = _selectedClipId.value
        )
        undoStack.add(snapshot)
        if (undoStack.size > 25) {
            undoStack.removeAt(0)
        }
        redoStack.clear()
        _canUndo.value = undoStack.size > 1
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.size <= 1) return
        val current = undoStack.removeAt(undoStack.lastIndex)
        redoStack.add(current)
        val previous = undoStack.last()
        restoreSnapshot(previous)
        _canUndo.value = undoStack.size > 1
        _canRedo.value = true
        _statusMessage.value = "Action undone ↶"
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val state = redoStack.removeAt(redoStack.lastIndex)
        undoStack.add(state)
        restoreSnapshot(state)
        _canUndo.value = true
        _canRedo.value = redoStack.isNotEmpty()
        _statusMessage.value = "Action redone ↷"
    }

    private fun restoreSnapshot(state: ProjectHistoryState) {
        _clips.value = state.clips
        _audioClips.value = state.audioClips
        _textOverlays.value = state.textOverlays
        _stickers.value = state.stickers
        _selectedClipId.value = state.selectedClipId
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        val total = totalDurationMs.value
        if (_currentTimeMs.value >= total - 100) {
            _currentTimeMs.value = 0L
        }
        _isPlaying.value = true
        _statusMessage.value = "Playing..."
        startPlaybackLoop()
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        _statusMessage.value = "Paused"
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 50L
            while (_isPlaying.value) {
                delay(stepMs)
                val next = _currentTimeMs.value + stepMs
                val max = totalDurationMs.value
                if (next >= max) {
                    _currentTimeMs.value = max
                    pause()
                    _statusMessage.value = "Playback finished"
                    break
                } else {
                    _currentTimeMs.value = next
                }
            }
        }
    }

    fun seekTo(timeMs: Long) {
        val total = totalDurationMs.value
        _currentTimeMs.value = timeMs.coerceIn(0L, total)
        updateActiveClipSelectionForTime(_currentTimeMs.value)
    }

    fun jumpSeconds(deltaSec: Int) {
        seekTo(_currentTimeMs.value + deltaSec * 1000L)
    }

    private fun updateActiveClipSelectionForTime(timeMs: Long) {
        var elapsed = 0L
        for (clip in _clips.value) {
            val end = elapsed + clip.effectiveDurationMs
            if (timeMs in elapsed until end) {
                _selectedClipId.value = clip.id
                return
            }
            elapsed = end
        }
    }

    fun selectClip(id: String) {
        _selectedClipId.value = id
        // Seek playhead to beginning of this clip
        var elapsed = 0L
        for (clip in _clips.value) {
            if (clip.id == id) {
                seekTo(elapsed)
                break
            }
            elapsed += clip.effectiveDurationMs
        }
    }

    fun setActiveToolTab(tab: ActiveToolTab) {
        _activeToolTab.value = if (_activeToolTab.value == tab) ActiveToolTab.NONE else tab
    }

    fun setAspectRatio(preset: AspectRatioPreset) {
        _aspectRatio.value = preset
        _statusMessage.value = "Aspect ratio: ${preset.label}"
    }

    fun importMedia(name: String, uriString: String?, isPhoto: Boolean, durationMs: Long = 10000L) {
        if (isPhoto) {
            viewModelScope.launch {
                _isConvertingPhoto.value = true
                _statusMessage.value = "Converting photo to 5-second video..."
                delay(1200) // smooth realistic conversion experience
                pushHistorySnapshot()
                val convertedName = if (name.contains(".")) {
                    name.substringBeforeLast(".") + "_5s.webm"
                } else {
                    "${name}_5s.webm"
                }
                val newClip = MediaClip(
                    id = UUID.randomUUID().toString(),
                    name = convertedName,
                    uriString = uriString,
                    durationMs = 5000L,
                    type = MediaType.PHOTO,
                    resolution = "1920 × 1080"
                )
                _clips.value = _clips.value + newClip
                _selectedClipId.value = newClip.id
                _isConvertingPhoto.value = false
                _statusMessage.value = "Imported photo as video clip: $convertedName"
            }
        } else {
            pushHistorySnapshot()
            val newClip = MediaClip(
                id = UUID.randomUUID().toString(),
                name = name,
                uriString = uriString,
                durationMs = durationMs.coerceAtLeast(3000L),
                type = MediaType.VIDEO,
                resolution = "1920 × 1080"
            )
            _clips.value = _clips.value + newClip
            _selectedClipId.value = newClip.id
            _statusMessage.value = "Imported video: $name"
        }
    }

    fun splitClipAtPlayhead() {
        val currentClips = _clips.value
        val playhead = _currentTimeMs.value
        var accumulated = 0L

        var targetIndex = -1
        var splitPointWithinClip = 0L

        for ((index, clip) in currentClips.withIndex()) {
            val clipDuration = clip.effectiveDurationMs
            val clipStart = accumulated
            val clipEnd = accumulated + clipDuration
            if (playhead > clipStart + 500 && playhead < clipEnd - 500) {
                targetIndex = index
                splitPointWithinClip = ((playhead - clipStart) * clip.speed).toLong()
                break
            }
            accumulated = clipEnd
        }

        if (targetIndex != -1) {
            pushHistorySnapshot()
            val originalClip = currentClips[targetIndex]
            val firstDuration = splitPointWithinClip
            val secondDuration = (originalClip.durationMs - originalClip.trimStartMs - originalClip.trimEndMs) - firstDuration

            val clip1 = originalClip.copy(
                id = UUID.randomUUID().toString(),
                name = "${originalClip.name} [Part 1]",
                durationMs = firstDuration + originalClip.trimStartMs,
                trimStartMs = originalClip.trimStartMs,
                trimEndMs = 0L
            )
            val clip2 = originalClip.copy(
                id = UUID.randomUUID().toString(),
                name = "${originalClip.name} [Part 2]",
                durationMs = secondDuration,
                trimStartMs = 0L,
                trimEndMs = originalClip.trimEndMs
            )

            val updated = currentClips.toMutableList()
            updated.removeAt(targetIndex)
            updated.add(targetIndex, clip1)
            updated.add(targetIndex + 1, clip2)

            _clips.value = updated
            _selectedClipId.value = clip2.id
            _statusMessage.value = "Split clip into 2 segments ✂️"
        } else {
            _statusMessage.value = "Move playhead inside a clip (min 0.5s from ends) to split"
        }
    }

    fun deleteSelectedClip() {
        val id = _selectedClipId.value ?: return
        if (_clips.value.size <= 1) {
            _statusMessage.value = "Cannot delete the only clip in project"
            return
        }
        pushHistorySnapshot()
        val updated = _clips.value.filter { it.id != id }
        _clips.value = updated
        _selectedClipId.value = updated.firstOrNull()?.id
        seekTo(0L)
        _statusMessage.value = "Deleted clip"
    }

    fun duplicateSelectedClip() {
        val id = _selectedClipId.value ?: return
        val clip = _clips.value.find { it.id == id } ?: return
        pushHistorySnapshot()
        val copy = clip.copy(
            id = UUID.randomUUID().toString(),
            name = "${clip.name} (Copy)"
        )
        val index = _clips.value.indexOfFirst { it.id == id }
        val updated = _clips.value.toMutableList()
        updated.add(index + 1, copy)
        _clips.value = updated
        _selectedClipId.value = copy.id
        _statusMessage.value = "Duplicated clip"
    }

    fun setClipFilter(filter: VideoFilter) {
        val id = _selectedClipId.value ?: return
        pushHistorySnapshot()
        _clips.value = _clips.value.map {
            if (it.id == id) it.copy(filter = filter) else it
        }
        _statusMessage.value = "Filter: ${filter.displayName}"
    }

    fun setClipSpeed(speed: Float) {
        val id = _selectedClipId.value ?: return
        pushHistorySnapshot()
        _clips.value = _clips.value.map {
            if (it.id == id) it.copy(speed = speed) else it
        }
        _statusMessage.value = "Clip speed: ${speed}x"
    }

    fun addAudioTrack(title: String, category: String, durationMs: Long) {
        pushHistorySnapshot()
        val newAudio = AudioClip(
            id = UUID.randomUUID().toString(),
            title = title,
            category = category,
            durationMs = durationMs
        )
        _audioClips.value = _audioClips.value + newAudio
        _statusMessage.value = "Added audio track: $title"
    }

    fun removeAudioTrack(id: String) {
        pushHistorySnapshot()
        _audioClips.value = _audioClips.value.filter { it.id != id }
        _statusMessage.value = "Removed audio track"
    }

    fun toggleAudioMute(id: String) {
        _audioClips.value = _audioClips.value.map {
            if (it.id == id) it.copy(isMuted = !it.isMuted) else it
        }
    }

    fun addTextOverlay(text: String, style: String, colorHex: Long, fontSizeSp: Int) {
        pushHistorySnapshot()
        val newText = TextOverlay(
            id = UUID.randomUUID().toString(),
            text = text,
            startOffsetMs = _currentTimeMs.value,
            durationMs = 4000L,
            colorHex = colorHex,
            fontSizeSp = fontSizeSp,
            styleName = style
        )
        _textOverlays.value = _textOverlays.value + newText
        _statusMessage.value = "Added text overlay: $text"
    }

    fun removeTextOverlay(id: String) {
        pushHistorySnapshot()
        _textOverlays.value = _textOverlays.value.filter { it.id != id }
        _statusMessage.value = "Removed text overlay"
    }

    fun addSticker(symbol: String, name: String) {
        pushHistorySnapshot()
        val newSticker = StickerOverlay(
            id = UUID.randomUUID().toString(),
            symbol = symbol,
            name = name,
            startOffsetMs = _currentTimeMs.value,
            durationMs = 3500L
        )
        _stickers.value = _stickers.value + newSticker
        _statusMessage.value = "Added sticker: $symbol"
    }

    fun removeSticker(id: String) {
        pushHistorySnapshot()
        _stickers.value = _stickers.value.filter { it.id != id }
        _statusMessage.value = "Removed sticker"
    }

    fun startExport(resolution: String, fps: Int) {
        viewModelScope.launch {
            _exportProgress.value = 0f
            _statusMessage.value = "Exporting at $resolution ($fps fps)..."
            for (step in 1..20) {
                delay(120)
                _exportProgress.value = step / 20f
            }
            delay(200)
            _exportProgress.value = null
            _exportFinishedMessage.value = "Project rendered successfully!\nResolution: $resolution @ ${fps}fps\nReady to save or share."
            _statusMessage.value = "Export completed successfully! 🎉"
        }
    }

    fun dismissExportFinished() {
        _exportFinishedMessage.value = null
    }
}
