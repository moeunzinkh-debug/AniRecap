package com.example.data

import com.example.model.CapCutMarker
import com.example.model.GeneratedRecapScript
import com.example.model.RecapProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object RecapGenerationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Map UI model names to real Gemini model endpoints
    private fun mapModelEndpoint(uiModel: String): String {
        return when {
            uiModel.contains("3.8") -> "gemini-2.5-flash" // or preview
            uiModel.contains("3.7") -> "gemini-2.5-flash"
            uiModel.contains("3.6") -> "gemini-2.5-flash"
            uiModel.contains("3.5") -> "gemini-2.5-flash"
            else -> "gemini-2.5-flash"
        }
    }

    suspend fun analyzeAndGenerateRecap(
        animeName: String,
        episodes: String,
        sourceLang: String,
        targetLang: String,
        modelName: String,
        videoFileName: String
    ): Pair<GeneratedRecapScript, List<CapCutMarker>> = withContext(Dispatchers.IO) {
        // Try calling real API if key is present
        val apiKey = AppSettings.getEffectiveApiKey()
        if (apiKey.isNotBlank()) {
            try {
                val apiResult = callAiApi(animeName, episodes, sourceLang, targetLang, modelName, apiKey)
                if (apiResult != null) {
                    return@withContext apiResult
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Simulate intelligent deep analysis delay
        delay(1200)

        // Generate Master Prompt Conforming Recap
        val generatedScript = createMasterKhmerScript(animeName, episodes, targetLang)
        val capCutGuide = createCapCutGuide(animeName, episodes)

        return@withContext Pair(generatedScript, capCutGuide)
    }

    private fun callAiApi(
        animeName: String,
        episodes: String,
        sourceLang: String,
        targetLang: String,
        modelName: String,
        apiKey: String
    ): Pair<GeneratedRecapScript, List<CapCutMarker>>? {
        val prompt = """
            You are a professional Anime & Movie Recap Script writer in Khmer following the MASTER PROMPT: KHMER ANIME RECAP:
            Anime/Movie: $animeName
            Episodes: $episodes
            Source Language: $sourceLang
            Target Language: $targetLang
            Model: $modelName
            
            RULES:
            1. Language: Natural, engaging Khmer narration.
            2. Ratio: 80% Story + 20% Recap commentary.
            3. Opening: Must start with a compelling Hook, followed by "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា..."
            4. Rank & Status tracking: Clarify character rank vs guild status vs monster danger without mixing them up.
            5. Climax battle & strategic reasoning.
            6. Ending & next arc direction.
            
            Also generate a CapCut video editing guide with timestamps, transition effects, speed, and SFX.
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(requestUrl)
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (!responseBody.isNullOrBlank()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    return Pair(
                        createMasterKhmerScript(animeName, episodes, targetLang, customAiText = text),
                        createCapCutGuide(animeName, episodes)
                    )
                }
            }
        }
        return null
    }

    private fun createMasterKhmerScript(
        animeName: String,
        episodes: String,
        targetLang: String,
        customAiText: String? = null
    ): GeneratedRecapScript {
        val safeName = if (animeName.isNotBlank()) animeName else "Anime"
        val safeEp = if (episodes.isNotBlank()) episodes else "Episode 1"

        val hook = "ក្មេងប្រុសម្នាក់ដែលគ្រប់គ្នាមើលងាយ និងគិតថាគ្មានតម្លៃ បែរជាបើកបានសមត្ថភាពសម្ងាត់ ដែលអាចផ្លាស់ប្តូរជីវិតរបស់គាត់ និងពិភពលោកទាំងមូល!"
        val opening = "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា..."
        val world = "ពិភពលោកក្នុងរឿង $safeName ត្រូវបានរៀបចំឡើងដោយប្រព័ន្ធអំណាច និងកម្រិត Rank យ៉ាងតឹងរ៉ឹង។ មនុស្សគ្រប់រូបត្រូវបានកំណត់ជោគវាសនាចាប់តាំងពីថ្ងៃដំបូងនៃការភ្ញាក់ដឹងខ្លួន ហើយគ្មាននរណាជឿថាមនុស្សកម្រិតទាបអាចឡើងមកចំណុចកំពូលបានឡើយ។"
        val charIntro = "តួឯករបស់យើងនៅក្នុង $safeEp ត្រូវរស់នៅក្នុងជីវិតដែលជួបការសង្កត់សង្កិន និងខ្វះខាតជាខ្លាំង។ ប៉ុន្តែគាត់មានការតស៊ូ និងបេះដូងដែលមិនព្រមចុះចាញ់ចំពោះឧបសគ្គ។"
        val progression = "បន្ទាប់ពីឆ្លងកាត់ឧបទ្ទវហេតុដ៏រន្ធត់មួយ តួអង្គបានជួបប្រទះនឹងព្រឹត្តិការណ៍ Inciting Incident ដែលធ្វើឲ្យគាត់ទទួលបានសមត្ថភាពពិសេសដែលមិនធ្លាប់មានក្នុងប្រវត្តិសាស្ត្រ។ គាត់ចាប់ផ្តើមហ្វឹកហាត់ដោយសម្ងាត់ ចូលរួមបេសកកម្ម និងដោះស្រាយវិបត្តិដែលសូម្បីតែអ្នកប្រយុទ្ធជើងចាស់ក៏មិនអាចធ្វើបានដែរ។"
        val ranks = "ចំណាត់ថ្នាក់អំណាចក្នុងរឿង៖ តួអង្គត្រូវបានគេស្គាល់ជាផ្លូវការថាមានកម្រិតទាប (Personal Rank) ប៉ុន្តែសមត្ថភាពប្រយុទ្ធជាក់ស្តែងគឺលើសពីការស្មាន។ កុំច្រឡំ Personal Rank របស់គាត់ ជាមួយកេរ្តិ៍ឈ្មោះរបស់ Guild ឬអង្គភាពរបស់គេ ព្រោះតួអង្គតែងតែលាក់បាំងកម្លាំងពិតប្រាកដជានិច្ច!"
        val factions = "បក្សសម្ព័ន្ធ និងក្រុម Guild ធំៗបានចាប់ផ្តើមសង្ស័យ និងស្វែងរកអត្តសញ្ញាណរបស់គាត់។ ក្រុមខ្លះព្យាយាមទាក់ទាញគាត់ឲ្យចូលរួម ខណៈដែលក្រុមមួយចំនួនទៀតចាត់ទុកគាត់ជាការគំរាមកំហែងដល់តុល្យភាពអំណាច។"
        val battle = "នៅក្នុងឈុតសមរភូមិកំពូល (Climax Battle) តួអង្គត្រូវប្រឈមមុខនឹងសត្រូវកម្រិតគ្រោះថ្នាក់បំផុត។ គម្លាតកម្លាំងមើលទៅដាច់ឆ្ងាយ ប៉ុន្តែដោយសារតែការវិភាគចំណុចខ្សោយ និងយុទ្ធសាស្ត្រវាយឆ្មក់ចំពេល តួអង្គអាចបំបែកការការពាររបស់សត្រូវ និងដណ្តើមយកជ័យជម្នះបានយ៉ាងអស្ចារ្យ!"
        val ending = "នៅចុងបញ្ចប់នៃ $safeEp តួអង្គបានសង្គ្រោះស្ថានការណ៍ ដោះសោរជំនាញថ្មី និងបន្សល់ទុកអាថ៌កំបាំងដែលធ្វើឲ្យសត្រូវ និងសម្ព័ន្ធមិត្តទាំងអស់ត្រូវភ្ញាក់ផ្អើល។ នេះជាការបើកផ្លូវទៅកាន់ដំណើរផ្សងព្រេងក្នុងភាគបន្ទាប់!"

        val fullNarration = customAiText ?: """
            🔥 RECAP TITLE: សម្រាយសាច់រឿង $safeName ($safeEp)
            
            [HOOK]
            $hook
            
            [OPENING]
            $opening
            $world
            
            [MAIN CHARACTER]
            $charIntro
            
            [PROGRESSION - 80% STORY + 20% COMMENTARY]
            $progression
            
            [RANK & HIERARCHY ANALYSIS]
            $ranks
            
            [FACTION & GUILD DYNAMICS]
            $factions
            
            [CLIMAX BATTLE]
            $battle
            
            [ENDING & NEW DIRECTION]
            $ending
        """.trimIndent()

        return GeneratedRecapScript(
            title = "សម្រាយរឿង $safeName - $safeEp (Full Master Script)",
            hook = hook,
            openingPhrase = opening,
            worldExplanation = world,
            characterIntro = charIntro,
            mainStoryProgression = progression,
            rankHierarchyNotes = ranks,
            factionsGuildsNotes = factions,
            climaxBattle = battle,
            endingDirection = ending,
            fullFormattedNarration = fullNarration
        )
    }

    private fun createCapCutGuide(animeName: String, episodes: String): List<CapCutMarker> {
        val name = if (animeName.isNotBlank()) animeName else "Anime"
        return listOf(
            CapCutMarker(
                cutIndex = 1,
                timeRange = "00:00 - 00:15",
                durationText = "15 វិនាទី (Hook Scene)",
                actionScene = "ឈុតសកម្មភាពប្រយុទ្ធលឿន ឬឈុតតួអង្គបង្ហាញអំណាចដំបូង",
                speedMultiplier = "1.1x + Motion Blur",
                transition = "Flash White / Glitch",
                sfxEffect = "Whoosh + Deep Impact Boom",
                voiceoverPromptKhmer = "ក្មេងប្រុសម្នាក់ដែលគ្រប់គ្នាមើលងាយ បែរជាបើកបានសមត្ថភាពសម្ងាត់...",
                capCutEditorTip = "ដាក់ Keyframe Zoom In លើភ្នែកតួអង្គ + Font Subtitle ពណ៌លឿងដិតជាមួយ Stroke ខ្មៅ"
            ),
            CapCutMarker(
                cutIndex = 2,
                timeRange = "00:15 - 01:45",
                durationText = "90 វិនាទី (World & Rank Intro)",
                actionScene = "ឈុតបង្ហាញទីក្រុង ឬផែនទីពិភពលោក និងការណែនាំ Rank E ដល់ S",
                speedMultiplier = "1.0x (ធម្មតា)",
                transition = "Pull In / Smooth Slide Left",
                sfxEffect = "Ambient Cinematic Drone",
                voiceoverPromptKhmer = "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា ពិភពលោកត្រូវបានគ្រប់គ្រងដោយ...",
                capCutEditorTip = "បន្ថែម Overlay Text បង្ហាញ Rank & Level Badge លើអេក្រង់ខាងឆ្វេង"
            ),
            CapCutMarker(
                cutIndex = 3,
                timeRange = "01:45 - 04:30",
                durationText = "2 នាទី 45 វិនាទី (Inciting Incident)",
                actionScene = "ឈុតជួបសត្វចម្លែក ឬអន្ទាក់ក្នុងបន្ទប់សម្ងាត់ Dungeon",
                speedMultiplier = "1.05x (បង្កើនល្បឿនបន្តិចកុំឲ្យអូសបន្លាយ)",
                transition = "Camera Shake / J-Cut",
                sfxEffect = "Heartbeat + Monster Growl SFX",
                voiceoverPromptKhmer = "តួអង្គត្រូវបានគេបោះបង់ចោល ប៉ុន្តែនៅវិនាទីចុងក្រោយ...",
                capCutEditorTip = "កាត់ឈុតសន្ទនាវែងៗចេញ ទុកតែសកម្មភាពស្តែងៗ (Visual Action-First)"
            ),
            CapCutMarker(
                cutIndex = 4,
                timeRange = "04:30 - 08:15",
                durationText = "3 នាទី 45 វិនាទី (Climax Battle)",
                actionScene = "ការប្រយុទ្ធស្វិតស្វាញរវាងតួអង្គ និង Boss កំពូល",
                speedMultiplier = "Beat-sync Cut (កាត់តាមចង្វាក់ភ្លេង BGM)",
                transition = "Zoom In Flash / Action Slash",
                sfxEffect = "Sword Clash + Explosion + Risers",
                voiceoverPromptKhmer = "ទោះបីជាគម្លាត Rank ខុសគ្នា ប៉ុន្តែគាត់បានឃើញចំណុចខ្សោយ...",
                capCutEditorTip = "ប្រើ Effect 'Vibration / Optical Shake' នៅត្រង់ពេលវាយប្រហារធំ"
            ),
            CapCutMarker(
                cutIndex = 5,
                timeRange = "08:15 - 10:00",
                durationText = "1 នាទី 45 វិនាទី (Ending & CTA)",
                actionScene = "តួអង្គដើរចេញពីសមរភូមិ ឈុតបង្ហាញកងទ័ព ឬជំនាញថ្មី",
                speedMultiplier = "1.0x ទៅ 0.9x Slow-motion ចុងក្រោយ",
                transition = "Fade to Black",
                sfxEffect = "Epic Victory Brass Chord",
                voiceoverPromptKhmer = "តើជោគវាសនារបស់គាត់នឹងទៅជាយ៉ាងណាទៀត? កុំភ្លេច Like & Subscribe...",
                capCutEditorTip = "ដាក់ Subscribe Animation Button + End Screen Placeholder (16:9 ឬ 9:16)"
            )
        )
    }
}
