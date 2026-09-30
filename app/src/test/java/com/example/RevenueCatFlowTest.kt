package com.example

import android.Manifest
import androidx.test.core.app.ApplicationProvider
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = EchoApplication::class)
class RevenueCatFlowTest {

    @Test
    fun `test Purchases configuration and purchase flow attempts`() {
        ShadowLog.stream = System.out

        println("=== TEST RUN: EchoApplication onCreate execution ===")
        val app = ApplicationProvider.getApplicationContext<EchoApplication>()
        shadowOf(app).grantPermissions(Manifest.permission.INTERNET)
        app.onCreate()

        println("Purchases.isConfigured = ${Purchases.isConfigured}")
        assertTrue("Purchases must be configured successfully without throwing", Purchases.isConfigured)

        println("=== TEST ATTEMPT 1: Fetching offerings ===")
        val latch1 = CountDownLatch(1)
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                println("Attempt 1 getOfferings result: code=${error.code}, message=${error.message}, underlying=${error.underlyingErrorMessage}")
                latch1.countDown()
            },
            onSuccess = { offerings ->
                println("Attempt 1 getOfferings result: SUCCESS, current offering = ${offerings.current?.identifier}, packages = ${offerings.current?.availablePackages?.map { it.identifier }}")
                latch1.countDown()
            }
        )
        latch1.await(5, TimeUnit.SECONDS)

        println("=== TEST ATTEMPT 2: Fetching offerings again (second consecutive run) ===")
        val latch2 = CountDownLatch(1)
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                println("Attempt 2 getOfferings result: code=${error.code}, message=${error.message}, underlying=${error.underlyingErrorMessage}")
                latch2.countDown()
            },
            onSuccess = { offerings ->
                println("Attempt 2 getOfferings result: SUCCESS, current offering = ${offerings.current?.identifier}, packages = ${offerings.current?.availablePackages?.map { it.identifier }}")
                latch2.countDown()
            }
        )
        latch2.await(5, TimeUnit.SECONDS)
        println("=== COMPLETED BOTH ATTEMPTS ===")
    }
}
