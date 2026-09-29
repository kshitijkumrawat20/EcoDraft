package com.example

import android.app.Application
import android.util.Log
import com.example.auth.RevenueCatConfig
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class EchoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val keyPrefix = RevenueCatConfig.REVENUECAT_API_KEY.take(8)
        Log.i("EchoApplication", "RevenueCat API Key prefix: '$keyPrefix' (isKeyConfigured=${RevenueCatConfig.isKeyConfigured})")

        // Only configure RevenueCat SDK if a real, valid public key is supplied
        if (RevenueCatConfig.isKeyConfigured) {
            try {
                // Enable debug logging for testing and development
                Purchases.logLevel = LogLevel.DEBUG

                Log.i("EchoApplication", "Configuring Purchases with key prefix: $keyPrefix")
                // Configure RevenueCat SDK before any Activity or ViewModel starts
                Purchases.configure(
                    PurchasesConfiguration.Builder(this, RevenueCatConfig.REVENUECAT_API_KEY).build()
                )
                Log.i("EchoApplication", "RevenueCat SDK configured successfully.")
            } catch (e: Exception) {
                Log.w("EchoApplication", "Could not initialize RevenueCat SDK: ${e.message}")
            }
        } else {
            if (RevenueCatConfig.REVENUECAT_API_KEY.startsWith("test_")) {
                Log.w("EchoApplication", "RevenueCat API key starts with 'test_'. Android SDK requires a public Google Play key ('goog_...'). Operating in local sanctuary / demo mode.")
            } else {
                Log.i("EchoApplication", "RevenueCat API key not configured or is placeholder. Running in local sanctuary / demo mode.")
            }
        }
    }
}
