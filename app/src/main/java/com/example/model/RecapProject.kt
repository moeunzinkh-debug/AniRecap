package com.example.model

data class CapCutMarker(
    val cutIndex: Int,
    val timeRange: String,
    val durationText: String,
    val actionScene: String,
    val speedMultiplier: String,
    val transition: String,
    val sfxEffect: String,
    val voiceoverPromptKhmer: String,
    val capCutEditorTip: String
)

data class GeneratedRecapScript(
    val title: String,
    val hook: String,
    val openingPhrase: String,
    val worldExplanation: String,
    val characterIntro: String,
    val mainStoryProgression: String,
    val rankHierarchyNotes: String,
    val factionsGuildsNotes: String,
    val climaxBattle: String,
    val endingDirection: String,
    val fullFormattedNarration: String
)

data class RecapProject(
    val id: String,
    val animeOrMovieName: String,
    val episodes: String,
    val sourceLanguage: String = "Auto Detect",
    val targetLanguage: String = "Khmer & English",
    val selectedModel: String = "Gemini 2.5 Flash",
    val videoUri: String? = null,
    val videoFileName: String = "",
    val videoFileSize: String = "",
    val videoDurationSeconds: Int = 0,
    val isAnalyzing: Boolean = false,
    val script: GeneratedRecapScript? = null,
    val capCutGuide: List<CapCutMarker> = emptyList(),
    val bgmRecommendation: String = "Epic Orchestral / Dark Cyberpunk Suspense",
    val capCutTemplateAdvice: String = "Format 16:9 for YouTube / 9:16 Crop with Smart Auto Reframe"
)
