package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.media.MediaMetadataRetriever
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.RecapGenerationService
import com.example.model.CapCutMarker
import com.example.model.GeneratedRecapScript
import com.example.model.RecapProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class StudioNavScreen {
    data object HomeUpload : StudioNavScreen()
    data object Page2Preview : StudioNavScreen()
    data object Settings : StudioNavScreen()
}

class AniRecapStudioViewModel(application: Application) : AndroidViewModel(application) {

    init {
        AppSettings.init(application)
    }

    // Navigation
    private val _currentScreen = MutableStateFlow<StudioNavScreen>(StudioNavScreen.HomeUpload)
    val currentScreen: StateFlow<StudioNavScreen> = _currentScreen.asStateFlow()

    // Page 1: Input Fields — NO sample/default data, user must provide everything
    val videoUri = MutableStateFlow<Uri?>(null)
    val videoFileName = MutableStateFlow("")
    val videoFileSize = MutableStateFlow("")
    val videoDurationSeconds = MutableStateFlow(0)
    val animeOrMovieName = MutableStateFlow("")
    val episodes = MutableStateFlow("")
    val sourceLanguage = MutableStateFlow("Auto Detect")
    val targetLanguage = MutableStateFlow("Khmer & English target")
    val selectedModel = MutableStateFlow("Gemini 3.8")

    // Validation & Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Analysis State
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisProgressMessage = MutableStateFlow("")
    val analysisProgressMessage: StateFlow<String> = _analysisProgressMessage.asStateFlow()

    // Page 2: Project & Results — starts as null (no fake preload)
    private val _activeProject = MutableStateFlow<RecapProject?>(null)
    val activeProject: StateFlow<RecapProject?> = _activeProject.asStateFlow()

    // Video monitor playback state. Kept here (not in the composable) so the
    // playhead and the poster frame survive tab switches and re-navigation.
    val isMonitorPlaying = MutableStateFlow(false)
    val monitorPositionSeconds = MutableStateFlow(0f)
    val monitorTotalSeconds = MutableStateFlow(0)

    // Real poster frame pulled from the uploaded file, shown inside the monitor.
    private val _videoThumbnail = MutableStateFlow<Bitmap?>(null)
    val videoThumbnail: StateFlow<Bitmap?> = _videoThumbnail.asStateFlow()

    fun navigateTo(screen: StudioNavScreen) {
        _currentScreen.value = screen
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun onVideoPicked(uri: Uri, name: String, sizeStr: String) {
        videoUri.value = uri
        videoFileName.value = name
        videoFileSize.value = sizeStr
        _errorMessage.value = null
        monitorPositionSeconds.value = 0f
        isMonitorPlaying.value = false

        // Extract real video duration + a poster frame from the uploaded file
        viewModelScope.launch {
            val duration = extractVideoDuration(uri)
            videoDurationSeconds.value = duration
            monitorTotalSeconds.value = duration
            _videoThumbnail.value = extractPosterFrame(uri, duration)
        }
    }

    /**
     * Grabs one decoded frame ~8% into the video so the monitor shows a real
     * thumbnail instead of an empty black box.
     */
    private suspend fun extractPosterFrame(uri: Uri, durationSeconds: Int): Bitmap? =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(getApplication<Application>().applicationContext, uri)
                // ~8% into the clip (seconds * 80 == 8% of the runtime in ms)
                val seekMs = (durationSeconds.coerceAtLeast(2) * 80L).coerceAtLeast(500L)
                val frame = retriever.getFrameAtTime(seekMs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                retriever.release()
                frame
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    private suspend fun extractVideoDuration(uri: Uri): Int {
        return withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(getApplication<Application>().applicationContext, uri)
                val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                retriever.release()
                (durationMs / 1000).toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }

    /**
     * Validate inputs before analysis. Returns error message or null if valid.
     */
    fun validateInputs(): String? {
        if (videoUri.value == null) {
            return "សូមជ្រើសរើសវីដេអូជាមុនសិន (Please upload a video first)"
        }
        if (animeOrMovieName.value.isBlank()) {
            return "សូមបញ្ចូលឈ្មោះ Anime/Movie (Please enter the anime or movie name)"
        }
        return null
    }

    fun runAnalyze() {
        if (_isAnalyzing.value) return

        // Validate — no fake data, require real inputs
        val validationError = validateInputs()
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        val uri = videoUri.value ?: return

        viewModelScope.launch {
            _isAnalyzing.value = true
            _errorMessage.value = null

            try {
                _analysisProgressMessage.value = "1/5: Reading uploaded video file..."
                kotlinx.coroutines.delay(400)

                _analysisProgressMessage.value = "2/5: Uploading video to Gemini AI for analysis..."

                _analysisProgressMessage.value = "3/5: Analyzing video scenes, dialogue & story with ${selectedModel.value}..."

                _analysisProgressMessage.value = "4/5: Generating Khmer recap script from real video content..."

                _analysisProgressMessage.value = "5/5: Creating CapCut cut markers based on actual video timeline..."

                val (generatedScript, capCutMarkers) = RecapGenerationService.analyzeAndGenerateRecap(
                    context = getApplication<Application>().applicationContext,
                    videoUri = uri,
                    animeName = animeOrMovieName.value.trim(),
                    episodes = episodes.value.trim(),
                    sourceLang = sourceLanguage.value,
                    targetLang = targetLanguage.value,
                    modelName = selectedModel.value,
                    videoFileName = videoFileName.value,
                    videoDurationSeconds = videoDurationSeconds.value
                )

                val project = RecapProject(
                    id = "proj_${System.currentTimeMillis()}",
                    animeOrMovieName = animeOrMovieName.value.trim(),
                    episodes = episodes.value.trim(),
                    sourceLanguage = sourceLanguage.value,
                    targetLanguage = targetLanguage.value,
                    selectedModel = selectedModel.value,
                    videoUri = uri.toString(),
                    videoFileName = videoFileName.value,
                    videoFileSize = videoFileSize.value,
                    videoDurationSeconds = videoDurationSeconds.value,
                    script = generatedScript,
                    capCutGuide = capCutMarkers
                )

                _activeProject.value = project
                resetMonitor(videoDurationSeconds.value)
                _videoThumbnail.value = extractPosterFrame(uri, videoDurationSeconds.value)

                // Jump to Page 2 directly after analysis
                _currentScreen.value = StudioNavScreen.Page2Preview
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Analysis failed: ${e.message ?: "Unknown error"}. Please check your API key in Settings."
            } finally {
                _isAnalyzing.value = false
                _analysisProgressMessage.value = ""
            }
        }
    }

    fun updateScriptContent(newScriptText: String) {
        val current = _activeProject.value ?: return
        val currentScript = current.script ?: return
        _activeProject.value = current.copy(
            script = currentScript.copy(fullFormattedNarration = newScriptText)
        )
    }

    fun seekMonitorTo(seconds: Float) {
        val limit = monitorTotalSeconds.value.coerceAtLeast(1)
        monitorPositionSeconds.value = seconds.coerceIn(0f, limit.toFloat())
    }

    fun toggleMonitorPlay() {
        isMonitorPlaying.value = !isMonitorPlaying.value
    }

    fun stopMonitor() {
        isMonitorPlaying.value = false
    }

    /** Resets the playhead when a brand new recap is generated. */
    fun resetMonitor(totalSeconds: Int) {
        monitorTotalSeconds.value = totalSeconds
        monitorPositionSeconds.value = 0f
        isMonitorPlaying.value = false
    }

    /** Jump the monitor to a CapCut marker's in-point (seconds). */
    fun jumpToCut(startSeconds: Int) {
        seekMonitorTo(startSeconds.toFloat())
        isMonitorPlaying.value = false
    }
}
