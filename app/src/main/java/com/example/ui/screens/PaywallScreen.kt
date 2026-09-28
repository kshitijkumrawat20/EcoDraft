package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.EchoViewModel
import com.example.ui.theme.EchoBackground
import com.example.ui.theme.EchoDustyRoseFeelings
import com.example.ui.theme.EchoOnPrimary
import com.example.ui.theme.EchoOnSurface
import com.example.ui.theme.EchoOnSurfaceVariant
import com.example.ui.theme.EchoOutline
import com.example.ui.theme.EchoOutlineVariant
import com.example.ui.theme.EchoPrimary
import com.example.ui.theme.EchoPrimaryContainer
import com.example.ui.theme.EchoSageFacts
import com.example.ui.theme.EchoSecondary
import com.example.ui.theme.EchoSoftBlueNextSteps
import com.example.ui.theme.EchoSurfaceContainer
import com.example.ui.theme.EchoSurfaceContainerLow
import com.example.ui.theme.Newsreader
import com.example.ui.theme.PublicSans
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith

@Composable
fun PaywallScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val activity = context as? Activity
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()

    var currentOfferingPackage by remember { mutableStateOf<Package?>(null) }
    var isLoadingOffering by remember { mutableStateOf(true) }

    // Fetch RevenueCat offerings dynamically
    LaunchedEffect(Unit) {
        if (Purchases.isConfigured) {
            Purchases.sharedInstance.getOfferingsWith(
                onError = {
                    isLoadingOffering = false
                },
                onSuccess = { offerings ->
                    currentOfferingPackage = offerings.current?.availablePackages?.firstOrNull()
                    isLoadingOffering = false
                }
            )
        } else {
            isLoadingOffering = false
        }
    }

    // If user became premium while on paywall screen, auto navigate back
    LaunchedEffect(isPremium) {
        if (isPremium) {
            viewModel.navigateBack()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("paywall_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("close_paywall_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EchoSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "REVENUECAT PREMIUM",
                    fontFamily = PublicSans,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    color = EchoPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.2f), thickness = 0.8.dp)

            // Scrollable Paywall Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Header & Hero Copy
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(EchoSurfaceContainerLow)
                            .border(1.dp, EchoPrimaryContainer.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Premium Icon",
                            tint = EchoPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Decompress without boundaries.",
                        fontFamily = Newsreader,
                        fontStyle = FontStyle.Italic,
                        fontSize = 26.sp,
                        lineHeight = 34.sp,
                        color = EchoOnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Unlock unlimited bedside voice entries, deep search, plain text exports, and weekly emotional pattern synthesis.",
                        fontFamily = PublicSans,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = EchoSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Premium Features List
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EchoSurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PaywallFeatureRow(
                            icon = Icons.Default.Mic,
                            iconColor = EchoPrimary,
                            title = "Unlimited Voice Entries",
                            description = "Bypass the 3 entries/week free limit. Record your thoughts anytime."
                        )

                        HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

                        PaywallFeatureRow(
                            icon = Icons.Default.Search,
                            iconColor = EchoSageFacts,
                            title = "Full-Text Thought Search",
                            description = "Instantly search across all your past transcripts and facts."
                        )

                        HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

                        PaywallFeatureRow(
                            icon = Icons.Default.CloudDone,
                            iconColor = EchoDustyRoseFeelings,
                            title = "Formatted Plain Text Export",
                            description = "Export your organized entries into plain text files for sharing or archiving."
                        )

                        HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

                        PaywallFeatureRow(
                            icon = Icons.Default.Insights,
                            iconColor = EchoSoftBlueNextSteps,
                            title = "Weekly Pattern Synthesis Reports",
                            description = "Gemini AI analyzes recurring tension loops and emotional peaks across your week."
                        )
                    }
                }

                // Pricing Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF181B24),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EchoPrimaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EchoPrimaryContainer,
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Text(
                                text = "MOST POPULAR",
                                fontFamily = PublicSans,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoOnPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Echo Drafts Premium Pass",
                            fontFamily = Newsreader,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = EchoOnSurface
                        )

                        Text(
                            text = currentOfferingPackage?.product?.price?.formatted
                                ?: "$3.99 / month",
                            fontFamily = PublicSans,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoPrimary
                        )

                        Text(
                            text = "Includes 7-Day Free Trial • Cancel anytime in Google Play",
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoSecondary
                        )
                    }
                }

                // Call to Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (activity != null && currentOfferingPackage != null) {
                                viewModel.purchasePackage(activity, currentOfferingPackage!!)
                            } else {
                                viewModel.showMessage("Simulating RevenueCat purchase...")
                                viewModel.simulatePremiumUnlock()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EchoPrimaryContainer,
                            contentColor = EchoOnPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("subscribe_button")
                    ) {
                        if (isLoadingOffering) {
                            CircularProgressIndicator(
                                color = EchoOnPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start 7-Day Free Trial",
                                    fontFamily = PublicSans,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { viewModel.restorePurchases() },
                            modifier = Modifier.testTag("restore_purchases_button")
                        ) {
                            Text(
                                text = "Restore Purchases",
                                fontFamily = PublicSans,
                                fontSize = 12.sp,
                                color = EchoSecondary
                            )
                        }

                        TextButton(
                            onClick = { viewModel.simulatePremiumUnlock() },
                            modifier = Modifier.testTag("demo_unlock_button")
                        ) {
                            Text(
                                text = "Demo Unlock",
                                fontFamily = PublicSans,
                                fontSize = 12.sp,
                                color = EchoPrimary
                            )
                        }
                    }

                    Text(
                        text = "Powered by RevenueCat Android SDK. Payment will be charged to your Google Play account at confirmation of purchase.",
                        fontFamily = PublicSans,
                        fontSize = 10.sp,
                        color = EchoOutline,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallFeatureRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(EchoSurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = PublicSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = EchoOnSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontFamily = Newsreader,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = EchoSecondary
            )
        }
    }
}
