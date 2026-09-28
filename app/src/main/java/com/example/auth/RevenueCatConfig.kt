package com.example.auth

import com.example.BuildConfig

/**
 * Configuration holder for RevenueCat Android SDK.
 * Reads REVENUECAT_API_KEY from BuildConfig (.env file).
 */
object RevenueCatConfig {
    // RevenueCat Public API Key (read dynamically from BuildConfig / .env)
    val REVENUECAT_API_KEY: String = try {
        BuildConfig.REVENUECAT_API_KEY.ifBlank { "goog_placeholder_api_key" }
    } catch (_: Throwable) {
        "goog_placeholder_api_key"
    }

    // RevenueCat Entitlement Identifier
    const val ENTITLEMENT_ID = "premium"
}
