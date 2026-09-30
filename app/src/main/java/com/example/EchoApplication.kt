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

                val configuration = PurchasesConfiguration.Builder(this, RevenueCatConfig.REVENUECAT_API_KEY).build()
                Purchases.configure(configuration)
                Log.i("EchoApplication", "RevenueCat SDK configured successfully.")
            } catch (e: Exception) {
                Log.w("EchoApplication", "Could not initialize RevenueCat SDK: ${e.message}")
            }
        } else {
            Log.i("EchoApplication", "RevenueCat API key not configured or is placeholder. Running in local sanctuary / demo mode.")
        }
    }
}
