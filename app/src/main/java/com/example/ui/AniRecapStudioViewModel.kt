package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
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

    // Page 1: Input Fields
    val videoUri = MutableStateFlow<Uri?>(null)
    val videoFileName = MutableStateFlow("solo_leveling_ep1.mp4")
    val videoFileSize = MutableStateFlow("450 MB")
    val animeOrMovieName = MutableStateFlow("Solo Leveling")
    val episodes = MutableStateFlow("Episode 1 - 12")
    val sourceLanguage = MutableStateFlow("Auto Detect")
    val targetLanguage = MutableStateFlow("Khmer & English target")
    val selectedModel = MutableStateFlow("Gemini 3.8")

    // Analysis State
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisProgressMessage = MutableStateFlow("")
    val analysisProgressMessage: StateFlow<String> = _analysisProgressMessage.asStateFlow()

    // Page 2: Project & Results
    private val _activeProject = MutableStateFlow<RecapProject?>(null)
    val activeProject: StateFlow<RecapProject?> = _activeProject.asStateFlow()

    // Video monitor playback state
    val isMonitorPlaying = MutableStateFlow(false)
    val monitorCurrentSeconds = MutableStateFlow(0)
    val monitorTotalSeconds = MutableStateFlow(600) // 10 minutes preview

    init {
        // Preload an initial ready project so user can see Page 2 immediately if navigated
        viewModelScope.launch {
            val (script, guide) = RecapGenerationService.analyzeAndGenerateRecap(
                animeName = animeOrMovieName.value,
                episodes = episodes.value,
                sourceLang = sourceLanguage.value,
                targetLang = targetLanguage.value,
                modelName = selectedModel.value,
                videoFileName = videoFileName.value
            )
            _activeProject.value = RecapProject(
                id = "proj_default",
                animeOrMovieName = animeOrMovieName.value,
                episodes = episodes.value,
                sourceLanguage = sourceLanguage.value,
                targetLanguage = targetLanguage.value,
                selectedModel = selectedModel.value,
                videoFileName = videoFileName.value,
                script = script,
                capCutGuide = guide
            )
        }
    }

    fun navigateTo(screen: StudioNavScreen) {
        _currentScreen.value = screen
    }

    fun onVideoPicked(uri: Uri, name: String, sizeStr: String) {
        videoUri.value = uri
        videoFileName.value = name
        videoFileSize.value = sizeStr
    }

    fun runAnalyze() {
        if (_isAnalyzing.value) return

        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisProgressMessage.value = "1/4: Analyzing video stream & dialogue (Auto-detecting language)..."
            kotlinx.coroutines.delay(600)

            _analysisProgressMessage.value = "2/4: Applying Master Prompt: Plot-First 80% Story + 20% Recap with ${selectedModel.value}..."
            kotlinx.coroutines.delay(700)

            _analysisProgressMessage.value = "3/4: Calculating Rank & Faction Hierarchy tracking matrix..."
            kotlinx.coroutines.delay(600)

            _analysisProgressMessage.value = "4/4: Generating CapCut cut markers & timestamp roadmaps..."

            val (generatedScript, capCutMarkers) = RecapGenerationService.analyzeAndGenerateRecap(
                animeName = animeOrMovieName.value,
                episodes = episodes.value,
                sourceLang = sourceLanguage.value,
                targetLang = targetLanguage.value,
                modelName = selectedModel.value,
                videoFileName = videoFileName.value
            )

            val project = RecapProject(
                id = "proj_${System.currentTimeMillis()}",
                animeOrMovieName = animeOrMovieName.value,
                episodes = episodes.value,
                sourceLanguage = sourceLanguage.value,
                targetLanguage = targetLanguage.value,
                selectedModel = selectedModel.value,
                videoUri = videoUri.value?.toString(),
                videoFileName = videoFileName.value,
                videoFileSize = videoFileSize.value,
                script = generatedScript,
                capCutGuide = capCutMarkers
            )

            _activeProject.value = project
            _isAnalyzing.value = false
            _analysisProgressMessage.value = ""

            // Jump to Page 2 directly after analysis
            _currentScreen.value = StudioNavScreen.Page2Preview
        }
    }

    fun updateScriptContent(newScriptText: String) {
        val current = _activeProject.value ?: return
        val currentScript = current.script ?: return
        _activeProject.value = current.copy(
            script = currentScript.copy(fullFormattedNarration = newScriptText)
        )
    }

    fun seekMonitorTo(seconds: Int) {
        monitorCurrentSeconds.value = seconds.coerceIn(0, monitorTotalSeconds.value)
    }

    fun toggleMonitorPlay() {
        isMonitorPlaying.value = !isMonitorPlaying.value
    }
}
