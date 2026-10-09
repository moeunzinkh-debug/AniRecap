package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

enum class AppLanguage {
    KHMER,
    ENGLISH
}

data class ApiKeyEntry(
    val id: String,
    val label: String,
    val key: String,
    val provider: String = "Google Gemini" // "Google Gemini" or "OpenRouter"
)

object AppSettings {
    private const val PREF_NAME = "anirecap_preferences"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_LANGUAGE = "app_language"
    private const val KEY_API_KEYS = "api_keys_json"
    private const val KEY_ACTIVE_KEY_ID = "active_api_key_id"
    private const val KEY_CUSTOM_PROVIDER = "custom_provider_enabled"
    private const val KEY_OPENROUTER_URL = "openrouter_base_url"
    private const val KEY_OPENROUTER_KEY = "openrouter_api_key"
    private const val KEY_OPENROUTER_MODEL = "openrouter_model"

    private lateinit var prefs: SharedPreferences

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.KHMER)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _apiKeys = MutableStateFlow<List<ApiKeyEntry>>(emptyList())
    val apiKeys: StateFlow<List<ApiKeyEntry>> = _apiKeys.asStateFlow()

    private val _activeKeyId = MutableStateFlow<String?>(null)
    val activeKeyId: StateFlow<String?> = _activeKeyId.asStateFlow()

    private val _useCustomProvider = MutableStateFlow(false)
    val useCustomProvider: StateFlow<Boolean> = _useCustomProvider.asStateFlow()

    private val _openRouterBaseUrl = MutableStateFlow("https://openrouter.ai/api/v1")
    val openRouterBaseUrl: StateFlow<String> = _openRouterBaseUrl.asStateFlow()

    private val _openRouterApiKey = MutableStateFlow("")
    val openRouterApiKey: StateFlow<String> = _openRouterApiKey.asStateFlow()

    private val _openRouterModel = MutableStateFlow("google/gemini-flash-1.5")
    val openRouterModel: StateFlow<String> = _openRouterModel.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        loadSettings()
    }

    private fun loadSettings() {
        val themeStr = prefs.getString(KEY_THEME, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        _themeMode.value = try { AppThemeMode.valueOf(themeStr) } catch (e: Exception) { AppThemeMode.SYSTEM }

        val langStr = prefs.getString(KEY_LANGUAGE, AppLanguage.KHMER.name) ?: AppLanguage.KHMER.name
        _language.value = try { AppLanguage.valueOf(langStr) } catch (e: Exception) { AppLanguage.KHMER }

        _useCustomProvider.value = prefs.getBoolean(KEY_CUSTOM_PROVIDER, false)
        _openRouterBaseUrl.value = prefs.getString(KEY_OPENROUTER_URL, "https://openrouter.ai/api/v1") ?: "https://openrouter.ai/api/v1"
        _openRouterApiKey.value = prefs.getString(KEY_OPENROUTER_KEY, "") ?: ""
        _openRouterModel.value = prefs.getString(KEY_OPENROUTER_MODEL, "google/gemini-2.5-flash") ?: "google/gemini-2.5-flash"

        // Load API keys
        val keysJson = prefs.getString(KEY_API_KEYS, null)
        val loadedKeys = mutableListOf<ApiKeyEntry>()
        if (!keysJson.isNullOrBlank()) {
            try {
                val array = JSONArray(keysJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    loadedKeys.add(
                        ApiKeyEntry(
                            id = obj.getString("id"),
                            label = obj.getString("label"),
                            key = obj.getString("key"),
                            provider = obj.optString("provider", "Google Gemini")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // If no keys saved yet, inject default BuildConfig key if available
        if (loadedKeys.isEmpty()) {
            val buildConfigKey = getBuildConfigKey()
            if (buildConfigKey.isNotBlank()) {
                val defaultKey = ApiKeyEntry(
                    id = "default_key_1",
                    label = "System Default Gemini Key",
                    key = buildConfigKey,
                    provider = "Google Gemini"
                )
                loadedKeys.add(defaultKey)
            }
        }

        _apiKeys.value = loadedKeys
        _activeKeyId.value = prefs.getString(KEY_ACTIVE_KEY_ID, loadedKeys.firstOrNull()?.id)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME, mode.name).apply()
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString(KEY_LANGUAGE, lang.name).apply()
    }

    fun addApiKey(label: String, key: String, provider: String = "Google Gemini") {
        val newEntry = ApiKeyEntry(
            id = "key_${System.currentTimeMillis()}",
            label = if (label.isNotBlank()) label else "API Key ${_apiKeys.value.size + 1}",
            key = key.trim(),
            provider = provider
        )
        val updated = _apiKeys.value + newEntry
        _apiKeys.value = updated
        if (_activeKeyId.value == null) {
            _activeKeyId.value = newEntry.id
            prefs.edit().putString(KEY_ACTIVE_KEY_ID, newEntry.id).apply()
        }
        saveApiKeysToPrefs(updated)
    }

    fun removeApiKey(id: String) {
        val updated = _apiKeys.value.filter { it.id != id }
        _apiKeys.value = updated
        if (_activeKeyId.value == id) {
            val newActive = updated.firstOrNull()?.id
            _activeKeyId.value = newActive
            prefs.edit().putString(KEY_ACTIVE_KEY_ID, newActive).apply()
        }
        saveApiKeysToPrefs(updated)
    }

    fun setActiveKeyId(id: String) {
        _activeKeyId.value = id
        prefs.edit().putString(KEY_ACTIVE_KEY_ID, id).apply()
    }

    fun setCustomProvider(enabled: Boolean, baseUrl: String, apiKey: String, model: String) {
        _useCustomProvider.value = enabled
        _openRouterBaseUrl.value = baseUrl
        _openRouterApiKey.value = apiKey
        _openRouterModel.value = model

        prefs.edit()
            .putBoolean(KEY_CUSTOM_PROVIDER, enabled)
            .putString(KEY_OPENROUTER_URL, baseUrl)
            .putString(KEY_OPENROUTER_KEY, apiKey)
            .putString(KEY_OPENROUTER_MODEL, model)
            .apply()
    }

    fun getEffectiveApiKey(): String {
        if (_useCustomProvider.value && _openRouterApiKey.value.isNotBlank()) {
            return _openRouterApiKey.value
        }
        val active = _apiKeys.value.firstOrNull { it.id == _activeKeyId.value }
        if (active != null && active.key.isNotBlank()) {
            return active.key
        }
        return getBuildConfigKey()
    }

    private fun getBuildConfigKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (e: Throwable) {
            ""
        }
    }

    private fun saveApiKeysToPrefs(keys: List<ApiKeyEntry>) {
        val array = JSONArray()
        for (k in keys) {
            val obj = JSONObject().apply {
                put("id", k.id)
                put("label", k.label)
                put("key", k.key)
                put("provider", k.provider)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_API_KEYS, array.toString()).apply()
    }
}
