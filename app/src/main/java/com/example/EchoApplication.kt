package com.example

import android.app.Application
import com.example.auth.RevenueCatConfig
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class EchoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Enable debug logging for testing and development
        Purchases.logLevel = LogLevel.DEBUG

        // Configure RevenueCat SDK before any Activity or ViewModel starts
        Purchases.configure(
            PurchasesConfiguration.Builder(this, RevenueCatConfig.REVENUECAT_API_KEY).build()
        )
    }
}
