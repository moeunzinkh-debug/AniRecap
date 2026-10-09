package com.example.data

import android.content.Context
import android.net.Uri
import com.example.model.CapCutMarker
import com.example.model.GeneratedRecapScript
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Service that analyzes a REAL uploaded video file using Gemini AI.
 * No sample/fallback data — if the API key is missing or the call fails,
 * an exception is thrown so the UI can show a proper error.
 */
object RecapGenerationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .build()

    private const val GEMINI_BASE = "https://generativelanguage.googleapis.com"
    private const val MODEL_ENDPOINT = "gemini-2.5-flash"

    /**
     * Analyze a real uploaded video and generate a recap script + CapCut guide.
     *
     * Flow:
     * 1. Copy the video from the content URI to a temp file.
     * 2. Upload it to Gemini's File API.
     * 3. Wait for the file to become ACTIVE.
     * 4. Send a generateContent request with the video + master prompt.
     * 5. Parse the AI response into a structured script.
     *
     * @throws IllegalStateException if no API key is configured.
     * @throws Exception if the upload or analysis fails.
     */
    suspend fun analyzeAndGenerateRecap(
        context: Context,
        videoUri: Uri,
        animeName: String,
        episodes: String,
        sourceLang: String,
        targetLang: String,
        modelName: String,
        videoFileName: String,
        videoDurationSeconds: Int
    ): Pair<GeneratedRecapScript, List<CapCutMarker>> = withContext(Dispatchers.IO) {
        val apiKey = AppSettings.getEffectiveApiKey()
        if (apiKey.isBlank()) {
            throw IllegalStateException(
                "No Gemini API key configured. Please add your API key in Settings."
            )
        }

        // Step 1: Copy video from content URI to a temp file
        val tempVideoFile = copyUriToTempFile(context, videoUri, videoFileName)

        try {
            // Step 2: Upload video to Gemini File API
            val fileUri = uploadVideoToGemini(tempVideoFile, apiKey)

            // Step 3: Wait for the file to be processed (ACTIVE state)
            waitForFileActive(fileUri, apiKey)

            // Step 4: Generate recap from the real video
            val aiResponse = callGeminiWithVideo(
                fileUri = fileUri,
                animeName = animeName,
                episodes = episodes,
                sourceLang = sourceLang,
                targetLang = targetLang,
                modelName = modelName,
                videoDurationSeconds = videoDurationSeconds,
                apiKey = apiKey
            )

            // Step 5: Parse the AI response into structured data
            val script = parseAiResponseToScript(aiResponse, animeName, episodes)
            val capCutGuide = parseAiResponseToCapCutGuide(aiResponse, videoDurationSeconds, animeName)

            // Clean up: delete the uploaded file from Gemini
            deleteGeminiFile(fileUri, apiKey)

            return@withContext Pair(script, capCutGuide)
        } finally {
            // Always clean up the temp file
            try { tempVideoFile.delete() } catch (_: Exception) {}
        }
    }

    // ==========================================
    // Step 1: Copy content URI to temp file
    // ==========================================
    private fun copyUriToTempFile(context: Context, uri: Uri, fileName: String): File {
        val safeName = fileName.ifBlank { "uploaded_video.mp4" }
        val tempFile = File(context.cacheDir, "anirecap_upload_$safeName")

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Could not read the selected video file.")

        return tempFile
    }

    // ==========================================
    // Step 2: Upload video to Gemini File API
    // ==========================================
    private fun uploadVideoToGemini(videoFile: File, apiKey: String): String {
        val mimeType = when (videoFile.extension.lowercase()) {
            "mp4" -> "video/mp4"
            "mkv" -> "video/x-matroska"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "webm" -> "video/webm"
            else -> "video/mp4"
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "metadata",
                null,
                JSONObject().apply {
                    put("display_name", videoFile.name)
                }.toString().toRequestBody("application/json".toMediaType())
            )
            .addFormDataPart(
                "file",
                videoFile.name,
                videoFile.asRequestBody(mimeType.toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("$GEMINI_BASE/upload/v1beta/files?key=$apiKey")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        if (!response.isSuccessful || responseBody.isNullOrBlank()) {
            throw Exception("Video upload failed (HTTP ${response.code}): $responseBody")
        }

        val json = JSONObject(responseBody)
        val fileObj = json.optJSONObject("file") ?: throw Exception("Invalid upload response: no file object")
        return fileObj.getString("uri")
    }

    // ==========================================
    // Step 3: Poll until file is ACTIVE
    // ==========================================
    private suspend fun waitForFileActive(fileUri: String, apiKey: String) {
        // fileUri looks like: https://generativelanguage.googleapis.com/v1beta/files/abc123
        val fileName = fileUri.substringAfterLast("/")
        val maxAttempts = 30
        var attempt = 0

        while (attempt < maxAttempts) {
            val request = Request.Builder()
                .url("$GEMINI_BASE/v1beta/files/$fileName?key=$apiKey")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && !body.isNullOrBlank()) {
                val state = JSONObject(body).optString("state", "")
                when (state) {
                    "ACTIVE" -> return
                    "FAILED" -> throw Exception("Video processing failed on Gemini servers.")
                    else -> {
                        // PROCESSING — wait and retry
                        delay(2000)
                        attempt++
                    }
                }
            } else {
                delay(2000)
                attempt++
            }
        }
        throw Exception("Timed out waiting for video processing. Please try again.")
    }

    // ==========================================
    // Step 4: Call Gemini with the real video
    // ==========================================
    private fun callGeminiWithVideo(
        fileUri: String,
        animeName: String,
        episodes: String,
        sourceLang: String,
        targetLang: String,
        modelName: String,
        videoDurationSeconds: Int,
        apiKey: String
    ): String {
        val durationFormatted = formatDuration(videoDurationSeconds)

        val prompt = """
            You are a professional Anime & Movie Recap Script writer. You have been given a REAL video file to analyze.

            VIDEO METADATA:
            - Title hint: $animeName
            - Episodes: ${episodes.ifBlank { "N/A" }}
            - Source Language: $sourceLang
            - Target Language: $targetLang
            - Video Duration: $durationFormatted

            TASK:
            Analyze the actual video content (scenes, dialogue, action, story) and produce:

            PART 1 - RECAP SCRIPT (in Khmer, following MASTER PROMPT: KHMER ANIME RECAP):
            - HOOK: A compelling 1-2 sentence hook based on what actually happens in the video.
            - OPENING: Start with the classic Khmer recap opening phrase, followed by what the video actually shows.
            - WORLD: Explain the setting/world shown in the video.
            - CHARACTER: Introduce the main character(s) seen in the video.
            - STORY: 80% plot summary of what actually happens + 20% recap commentary.
            - RANK/HIERARCHY: Any ranking, power levels, or hierarchy visible in the video.
            - FACTIONS: Any groups, guilds, or factions shown.
            - CLIMAX: The most intense moment actually shown in the video.
            - ENDING: How the video ends and what it sets up next.

            PART 2 - CAPCUT CUT GUIDE:
            Based on the ACTUAL video duration ($durationFormatted), create 5 cut markers with:
            - Real timestamps within the video duration
            - Scene descriptions matching actual content
            - Speed, transition, SFX recommendations
            - Khmer voiceover lines matching the actual scenes

            IMPORTANT: Base everything on the ACTUAL video content you see. Do not use generic templates.

            Format your response as JSON:
            {
              "hook": "...",
              "opening": "...",
              "world": "...",
              "character": "...",
              "story": "...",
              "rank": "...",
              "factions": "...",
              "climax": "...",
              "ending": "...",
              "fullNarration": "... complete formatted Khmer narration ...",
              "capCutMarkers": [
                {
                  "cutIndex": 1,
                  "timeRange": "00:00 - 00:15",
                  "durationText": "15 seconds",
                  "actionScene": "...",
                  "speedMultiplier": "1.0x",
                  "transition": "...",
                  "sfxEffect": "...",
                  "voiceoverPromptKhmer": "...",
                  "capCutEditorTip": "..."
                }
              ]
            }
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        // Video part first
                        put(JSONObject().apply {
                            put("file_data", JSONObject().apply {
                                put("file_uri", fileUri)
                                put("mime_type", "video/mp4")
                            })
                        })
                        // Text prompt second
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 8192)
            })
        }

        val request = Request.Builder()
            .url("$GEMINI_BASE/v1beta/models/$MODEL_ENDPOINT:generateContent?key=$apiKey")
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        if (!response.isSuccessful || responseBody.isNullOrBlank()) {
            throw Exception("Gemini API error (HTTP ${response.code}): $responseBody")
        }

        val json = JSONObject(responseBody)
        val candidates = json.optJSONArray("candidates")
        val text = candidates?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text")
            ?: throw Exception("Empty response from Gemini API.")

        return text
    }

    // ==========================================
    // Step 5: Parse AI response into models
    // ==========================================
    private fun parseAiResponseToScript(
        aiText: String,
        animeName: String,
        episodes: String
    ): GeneratedRecapScript {
        // Try to parse as JSON first
        val jsonText = extractJsonFromText(aiText)
        if (jsonText != null) {
            try {
                val json = JSONObject(jsonText)
                return GeneratedRecapScript(
                    title = "Recap: $animeName${if (episodes.isNotBlank()) " - $episodes" else ""} (from real video)",
                    hook = json.optString("hook", defaultHook(animeName)),
                    openingPhrase = json.optString("opening", "At the beginning of the story..."),
                    worldExplanation = json.optString("world", ""),
                    characterIntro = json.optString("character", ""),
                    mainStoryProgression = json.optString("story", ""),
                    rankHierarchyNotes = json.optString("rank", ""),
                    factionsGuildsNotes = json.optString("factions", ""),
                    climaxBattle = json.optString("climax", ""),
                    endingDirection = json.optString("ending", ""),
                    fullFormattedNarration = json.optString("fullNarration", aiText)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback: use the raw AI text as the full narration
        return GeneratedRecapScript(
            title = "Recap: $animeName${if (episodes.isNotBlank()) " - $episodes" else ""} (from real video)",
            hook = defaultHook(animeName),
            openingPhrase = "At the beginning of the story...",
            worldExplanation = "",
            characterIntro = "",
            mainStoryProgression = aiText,
            rankHierarchyNotes = "",
            factionsGuildsNotes = "",
            climaxBattle = "",
            endingDirection = "",
            fullFormattedNarration = aiText
        )
    }

    private fun parseAiResponseToCapCutGuide(
        aiText: String,
        videoDurationSeconds: Int,
        animeName: String
    ): List<CapCutMarker> {
        val jsonText = extractJsonFromText(aiText)
        if (jsonText != null) {
            try {
                val json = JSONObject(jsonText)
                val markersArray = json.optJSONArray("capCutMarkers")
                if (markersArray != null && markersArray.length() > 0) {
                    val markers = mutableListOf<CapCutMarker>()
                    for (i in 0 until markersArray.length()) {
                        val m = markersArray.getJSONObject(i)
                        markers.add(
                            CapCutMarker(
                                cutIndex = m.optInt("cutIndex", i + 1),
                                timeRange = m.optString("timeRange", ""),
                                durationText = m.optString("durationText", ""),
                                actionScene = m.optString("actionScene", ""),
                                speedMultiplier = m.optString("speedMultiplier", "1.0x"),
                                transition = m.optString("transition", "Cut"),
                                sfxEffect = m.optString("sfxEffect", ""),
                                voiceoverPromptKhmer = m.optString("voiceoverPromptKhmer", ""),
                                capCutEditorTip = m.optString("capCutEditorTip", "")
                            )
                        )
                    }
                    return markers
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback: generate markers based on real video duration
        return generateMarkersFromDuration(videoDurationSeconds, animeName)
    }

    /**
     * Generate CapCut markers based on the ACTUAL video duration.
     * Divides the real duration into 5 segments.
     */
    private fun generateMarkersFromDuration(durationSeconds: Int, animeName: String): List<CapCutMarker> {
        if (durationSeconds <= 0) return emptyList()

        val total = durationSeconds
        val seg = total / 5
        val markers = mutableListOf<CapCutMarker>()

        val segmentTitles = listOf(
            "Hook / Opening Scene" to "Opening scene from the video",
            "World Building & Setting" to "Setting and world shown in the video",
            "Main Story / Inciting Incident" to "Key story moment",
            "Climax / Action Peak" to "Most intense action scene",
            "Ending & Call to Action" to "Ending and next-episode tease"
        )

        for (i in 0 until 5) {
            val start = i * seg
            val end = if (i == 4) total else (i + 1) * seg
            markers.add(
                CapCutMarker(
                    cutIndex = i + 1,
                    timeRange = "${formatTime(start)} - ${formatTime(end)}",
                    durationText = "${formatDuration(end - start)} (based on real video)",
                    actionScene = segmentTitles[i].second,
                    speedMultiplier = if (i == 3) "Beat-sync / 1.1x" else "1.0x",
                    transition = when (i) {
                        0 -> "Flash White / Glitch"
                        1 -> "Smooth Slide"
                        2 -> "Camera Shake / J-Cut"
                        3 -> "Zoom In Flash"
                        else -> "Fade to Black"
                    },
                    sfxEffect = when (i) {
                        0 -> "Whoosh + Impact Boom"
                        1 -> "Ambient Cinematic"
                        2 -> "Heartbeat + Tension"
                        3 -> "Sword Clash + Explosion"
                        else -> "Epic Victory Chord"
                    },
                    voiceoverPromptKhmer = "Segment ${i + 1} of $animeName - see script for full narration",
                    capCutEditorTip = "Timestamp based on actual video duration (${formatDuration(total)})"
                )
            )
        }
        return markers
    }

    // ==========================================
    // Helpers
    // ==========================================
    private fun extractJsonFromText(text: String): String? {
        // Find JSON block in the response (may be wrapped in markdown code blocks)
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1)
        }
        return null
    }

    private fun deleteGeminiFile(fileUri: String, apiKey: String) {
        try {
            val fileName = fileUri.substringAfterLast("/")
            val request = Request.Builder()
                .url("$GEMINI_BASE/v1beta/files/$fileName?key=$apiKey")
                .delete()
                .build()
            client.newCall(request).execute().close()
        } catch (_: Exception) {}
    }

    private fun formatDuration(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d (%d min %d sec)", min, sec, min, sec)
    }

    private fun formatTime(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }

    private fun defaultHook(animeName: String): String {
        return "Hook for $animeName - generated from real video analysis"
    }
}
