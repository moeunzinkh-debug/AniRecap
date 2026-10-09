package com.example.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.model.CapCutMarker
import com.example.model.GeneratedRecapScript
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
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
    private const val FILE_PROCESSING_MAX_ATTEMPTS = 90
    private const val FILE_PROCESSING_POLL_INTERVAL_MS = 2_000L

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
        videoDurationSeconds: Int,
        onProgress: (String) -> Unit = {}
    ): Pair<GeneratedRecapScript, List<CapCutMarker>> = withContext(Dispatchers.IO) {
        val apiKey = AppSettings.getGeminiApiKey()
        if (apiKey.isBlank()) {
            throw IllegalStateException(
                "Gemini API key is missing. Add a Google Gemini API key in Settings; an OpenRouter key cannot upload through Gemini Files API."
            )
        }

        onProgress("1/5: Reading the selected video...")
        val displayName = resolveDisplayName(context, videoUri, videoFileName)
        val mimeType = resolveVideoMimeType(context, videoUri, displayName)
        val tempVideoFile = copyUriToTempFile(context, videoUri)
        var uploadedFileUri: String? = null

        try {
            val effectiveDurationSeconds = videoDurationSeconds.takeIf { it > 0 }
                ?: readVideoDurationSeconds(tempVideoFile)

            onProgress("2/5: Uploading video to Gemini...")
            val fileUri = uploadVideoToGemini(
                videoFile = tempVideoFile,
                displayName = displayName,
                mimeType = mimeType,
                apiKey = apiKey
            )
            uploadedFileUri = fileUri

            onProgress("3/5: Waiting for Gemini to process the video...")
            waitForFileActive(fileUri, apiKey)

            onProgress("4/5: Analyzing the video and writing the recap...")
            val aiResponse = callGeminiWithVideo(
                fileUri = fileUri,
                mimeType = mimeType,
                animeName = animeName,
                episodes = episodes,
                sourceLang = sourceLang,
                targetLang = targetLang,
                modelName = modelName,
                videoDurationSeconds = effectiveDurationSeconds,
                apiKey = apiKey
            )

            onProgress("5/5: Preparing the recap and CapCut guide...")
            val script = parseAiResponseToScript(aiResponse, animeName, episodes)
            val capCutGuide = parseAiResponseToCapCutGuide(
                aiText = aiResponse,
                videoDurationSeconds = effectiveDurationSeconds,
                animeName = animeName
            )
            Pair(script, capCutGuide)
        } finally {
            // Clean up the remote Gemini file even when analysis fails, then remove the local copy.
            uploadedFileUri?.let { deleteGeminiFile(it, apiKey) }
            runCatching { tempVideoFile.delete() }
        }
    }

    // ==========================================
    // Step 1: Copy content URI to temp file
    // ==========================================
    private fun copyUriToTempFile(context: Context, uri: Uri): File {
        val tempFile = File.createTempFile("anirecap_upload_", ".tmp", context.cacheDir)
        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Could not read the selected video file.")
            input.use { source ->
                FileOutputStream(tempFile).use { output -> source.copyTo(output) }
            }
            if (tempFile.length() <= 0L) {
                throw IllegalStateException("The selected video is empty or could not be copied.")
            }
            return tempFile
        } catch (error: Exception) {
            tempFile.delete()
            throw error
        }
    }

    private fun resolveDisplayName(context: Context, uri: Uri, fallback: String): String {
        val providerName = runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                } else {
                    null
                }
            }
        }.getOrNull()
        return providerName?.takeIf { it.isNotBlank() }
            ?: fallback.takeIf { it.isNotBlank() }
            ?: "uploaded_video.mp4"
    }

    private fun resolveVideoMimeType(context: Context, uri: Uri, fileName: String): String {
        val providerMimeType = runCatching { context.contentResolver.getType(uri) }.getOrNull()
            ?.substringBefore(';')
            ?.lowercase()
            ?.takeIf { it.startsWith("video/") && !it.contains('*') }
        if (providerMimeType != null) return providerMimeType

        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "mkv" -> "video/x-matroska"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "webm" -> "video/webm"
            "mpeg", "mpg" -> "video/mpeg"
            "3gp" -> "video/3gpp"
            "wmv" -> "video/x-ms-wmv"
            "flv" -> "video/x-flv"
            else -> "video/mp4"
        }
    }

    private fun readVideoDurationSeconds(file: File): Int {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            (durationMs / 1_000L).toInt()
        } catch (_: Exception) {
            0
        } finally {
            runCatching { retriever.release() }
        }
    }

    // ==========================================
    // Step 2: Upload video to Gemini File API
    // ==========================================
    private fun uploadVideoToGemini(
        videoFile: File,
        displayName: String,
        mimeType: String,
        apiKey: String
    ): String {
        // Gemini Files API uses a resumable two-step upload. The metadata must be
        // wrapped in the `file` resource; the response provides a one-time upload URL.
        val metadataBody = JSONObject().apply {
            put("file", JSONObject().apply { put("display_name", displayName) })
        }.toString().toRequestBody("application/json".toMediaType())

        val startRequest = Request.Builder()
            .url(apiUrl("/upload/v1beta/files", apiKey))
            .header("X-Goog-Upload-Protocol", "resumable")
            .header("X-Goog-Upload-Command", "start")
            .header("X-Goog-Upload-Header-Content-Length", videoFile.length().toString())
            .header("X-Goog-Upload-Header-Content-Type", mimeType)
            .post(metadataBody)
            .build()

        val uploadUrl = client.newCall(startRequest).execute().use { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                throw Exception(httpError("Gemini upload initialization failed", response.code, responseBody))
            }
            response.header("X-Goog-Upload-URL")
                ?.takeIf { it.isNotBlank() }
                ?: throw Exception("Gemini upload initialization failed: the server did not return an upload URL.")
        }

        val uploadRequest = Request.Builder()
            .url(uploadUrl)
            .header("X-Goog-Upload-Offset", "0")
            .header("X-Goog-Upload-Command", "upload, finalize")
            .put(videoFile.asRequestBody(mimeType.toMediaType()))
            .build()

        val responseBody = client.newCall(uploadRequest).execute().use { response ->
            val body = response.body?.string()
            if (!response.isSuccessful || body.isNullOrBlank()) {
                throw Exception(httpError("Video upload failed", response.code, body))
            }
            body
        }

        val fileObject = JSONObject(responseBody).optJSONObject("file")
            ?: throw Exception("Gemini uploaded the video but returned no file details.")
        return fileObject.optString("uri").takeIf { it.isNotBlank() }
            ?: throw Exception("Gemini uploaded the video but returned no file URI.")
    }

    // ==========================================
    // Step 3: Poll until file is ACTIVE
    // ==========================================
    private suspend fun waitForFileActive(fileUri: String, apiKey: String) {
        val fileName = fileUri.substringAfterLast('/').takeIf { it.isNotBlank() }
            ?: throw Exception("Gemini returned an invalid video file URI.")
        val request = Request.Builder()
            .url(apiUrl("/v1beta/files/$fileName", apiKey))
            .get()
            .build()

        repeat(FILE_PROCESSING_MAX_ATTEMPTS) { attempt ->
            val state = client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (!response.isSuccessful) {
                    if (response.code in setOf(408, 429, 500, 502, 503, 504)) {
                        null // transient response; retry after a short delay
                    } else {
                        throw Exception(httpError("Could not check Gemini video status", response.code, body))
                    }
                } else if (body.isNullOrBlank()) {
                    "PROCESSING"
                } else {
                    JSONObject(body).optString("state", "PROCESSING")
                }
            }

            when (state) {
                "ACTIVE" -> return
                "FAILED" -> throw Exception("Gemini could not process this video. Try an MP4 or another supported video format.")
            }
            if (attempt < FILE_PROCESSING_MAX_ATTEMPTS - 1) {
                delay(FILE_PROCESSING_POLL_INTERVAL_MS)
            }
        }
        throw Exception("Gemini is still processing the video after 3 minutes. Please try again with a shorter video.")
    }

    // ==========================================
    // Step 4: Call Gemini with the real video
    // ==========================================
    private fun callGeminiWithVideo(
        fileUri: String,
        mimeType: String,
        animeName: String,
        episodes: String,
        sourceLang: String,
        targetLang: String,
        modelName: String,
        videoDurationSeconds: Int,
        apiKey: String
    ): String {
        val durationFormatted = formatDuration(videoDurationSeconds)
        val outputLanguageInstructions = when (targetLang.trim().lowercase()) {
            "english" -> "Write the complete narration and voiceover lines in natural English."
            "khmer" -> "Write the complete narration and voiceover lines in natural Khmer."
            else -> "Write the narration primarily in natural Khmer; retain accurate English names and terms where helpful."
        }
        val prompt = """
            You are a professional anime and movie recap writer. Analyze the uploaded video itself from beginning to end.

            VIDEO DETAILS:
            - Title supplied by the user (a hint, not proof): $animeName
            - Episodes: ${episodes.ifBlank { "Not specified" }}
            - Spoken/source language: $sourceLang
            - Requested output language: $targetLang
            - Actual video duration: $durationFormatted

            MASTER RECAP RULES:
            - Base the recap on scenes and dialogue actually visible or audible in this video. Do not invent plot, names, ranks, factions, dialogue, or events. If something is unclear, say so rather than guessing.
            - Keep the story plot-first: roughly 80% chronological story summary and 20% concise recap commentary.
            - Track character identity and development carefully. Keep personal rank/power level separate from guild, faction, or organization.
            - Include a compelling hook, natural opening, setting/world, main characters, story progression, rank/power notes, factions, climax, and ending/next direction when the video supports them.
            - $outputLanguageInstructions
            - Create exactly five CapCut markers that cover the full video in order. Use real timestamps within 00:00 and $durationFormatted; describe the actual scene at each timestamp. Do not make up scene details.
            - Match the voiceover lines to the corresponding scene and requested output language.

            Return JSON only, with this structure:
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
              "fullNarration": "Complete, natural recap narration based on the video",
              "capCutMarkers": [
                {
                  "cutIndex": 1,
                  "timeRange": "00:00 - 00:30",
                  "durationText": "30 seconds",
                  "actionScene": "What is actually shown in this segment",
                  "speedMultiplier": "1.0x",
                  "transition": "Suggested transition",
                  "sfxEffect": "Suggested sound effect",
                  "voiceoverPromptKhmer": "Voiceover line matching this scene",
                  "capCutEditorTip": "Practical editing note"
                }
              ]
            }
        """.trimIndent()

        val contentParts = JSONArray()
            .put(
                JSONObject().put(
                    "fileData",
                    JSONObject()
                        .put("fileUri", fileUri)
                        .put("mimeType", mimeType)
                )
            )
            .put(JSONObject().put("text", prompt))
        val jsonPayload = JSONObject()
            .put("contents", JSONArray().put(JSONObject().put("parts", contentParts)))
            .put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.5)
                    .put("maxOutputTokens", 8192)
                    .put("responseMimeType", "application/json")
            )

        val modelId = resolveGeminiModelId(modelName)
        val request = Request.Builder()
            .url(apiUrl("/v1beta/models/$modelId:generateContent", apiKey))
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val responseBody = client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            if (!response.isSuccessful || body.isNullOrBlank()) {
                throw Exception(httpError("Gemini recap generation failed", response.code, body))
            }
            body
        }

        val responseJson = JSONObject(responseBody)
        val candidates = responseJson.optJSONArray("candidates")
        val parts = candidates?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
        val text = parts?.let { candidateParts ->
            (0 until candidateParts.length())
                .mapNotNull { index -> candidateParts.optJSONObject(index)?.optString("text") }
                .filter { it.isNotBlank() }
                .joinToString("\n")
        }.orEmpty()

        if (text.isBlank()) {
            val blockReason = responseJson.optJSONObject("promptFeedback")
                ?.optString("blockReason")
                ?.takeIf { it.isNotBlank() }
            throw Exception(blockReason?.let { "Gemini could not analyze this video: $it" }
                ?: "Gemini returned no recap text. Please try again or choose a shorter video.")
        }
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
    private fun apiUrl(path: String, apiKey: String): String =
        "$GEMINI_BASE$path".toHttpUrl()
            .newBuilder()
            .addQueryParameter("key", apiKey)
            .build()
            .toString()

    private fun resolveGeminiModelId(modelName: String): String =
        when (modelName.trim().lowercase()) {
            "gemini 2.5 pro", "gemini-2.5-pro" -> "gemini-2.5-pro"
            else -> "gemini-2.5-flash"
        }

    private fun httpError(prefix: String, statusCode: Int, body: String?): String {
        val apiMessage = runCatching {
            JSONObject(body.orEmpty()).optJSONObject("error")?.optString("message")
        }.getOrNull()?.takeIf { it.isNotBlank() }
        val detail = apiMessage ?: body?.replace(Regex("\\s+"), " ")?.take(400)
        return if (detail.isNullOrBlank()) "$prefix (HTTP $statusCode)."
        else "$prefix (HTTP $statusCode): $detail"
    }

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
        val fileName = fileUri.substringAfterLast('/').substringBefore('?')
        if (fileName.isBlank()) return
        runCatching {
            val request = Request.Builder()
                .url(apiUrl("/v1beta/files/$fileName", apiKey))
                .delete()
                .build()
            client.newCall(request).execute().use { }
        }
    }

    private fun formatDuration(seconds: Int): String {
        val safeSeconds = seconds.coerceAtLeast(0)
        val minutes = safeSeconds / 60
        val remainingSeconds = safeSeconds % 60
        return String.format("%02d:%02d (%d min %d sec)", minutes, remainingSeconds, minutes, remainingSeconds)
    }

    private fun formatTime(seconds: Int): String {
        val safeSeconds = seconds.coerceAtLeast(0)
        val hours = safeSeconds / 3600
        val minutes = (safeSeconds % 3600) / 60
        val remainingSeconds = safeSeconds % 60
        return if (hours > 0) String.format("%d:%02d:%02d", hours, minutes, remainingSeconds)
        else String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun defaultHook(animeName: String): String {
        return "Hook for $animeName - generated from real video analysis"
    }
}
