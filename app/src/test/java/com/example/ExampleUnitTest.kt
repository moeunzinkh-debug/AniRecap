package com.example

import com.example.data.RecapGenerationService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRecapGeneration_createsScriptAndCapCutMarkers() = runBlocking {
        val (script, markers) = RecapGenerationService.analyzeAndGenerateRecap(
            animeName = "Solo Leveling",
            episodes = "Episode 1",
            sourceLang = "Auto Detect",
            targetLang = "Khmer & English",
            modelName = "Gemini 3.8",
            videoFileName = "solo_leveling_ep1.mp4"
        )

        assertNotNull(script)
        assertTrue(script.hook.isNotBlank())
        assertTrue(script.openingPhrase.contains("នៅដើមសាច់រឿង"))
        assertTrue(markers.isNotEmpty())
        assertEquals(1, markers.first().cutIndex)
        assertTrue(markers.first().capCutEditorTip.isNotBlank())
    }
}
