package com.example.auth

import com.example.BuildConfig

/**
 * Configuration holder for RevenueCat Android SDK.
 * Reads REVENUECAT_API_KEY from BuildConfig (.env file).
 */
object RevenueCatConfig {
    // RevenueCat Entitlement Identifier
    const val ENTITLEMENT_ID = "premium"

    // RevenueCat Public API Key (read dynamically from BuildConfig / .env)
    val REVENUECAT_API_KEY: String = try {
        BuildConfig.REVENUECAT_API_KEY.trim()
    } catch (_: Throwable) {
        ""
    }

    /**
     * Determines whether a valid, production public Android RevenueCat API key is provided.
     * RevenueCat Android SDK strictly requires a public Google Play key ('goog_...') or Amazon key ('amzn_...').
     * Test keys ('test_...'), web keys, or placeholders will fail authentication in the Android SDK.
     */
    val isKeyConfigured: Boolean
        get() {
            val key = REVENUECAT_API_KEY
            if (key.isBlank()) return false
            if (key.contains("placeholder", ignoreCase = true)) return false
            if (key == "goog_placeholder_api_key") return false
            if (!key.startsWith("goog_") && !key.startsWith("amzn_")) return false
            if (key.length < 15) return false
            return true
        }
}
