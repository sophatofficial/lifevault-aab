package com.example.admob

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * AdMob Configuration for LifeVault Application.
 *
 * NOTE: The IDs below are Google AdMob standard TEST Ad Unit IDs.
 * Test IDs allow safe testing of ads without violating Google AdMob policies or risking account suspension.
 *
 * HOW TO SWAP FOR PRODUCTION:
 * 1. Create your application and ad units in your Google AdMob Console (https://admob.google.com).
 * 2. Update AndroidManifest.xml (<meta-data android:name="com.google.android.gms.ads.APPLICATION_ID" ... />) with your real AdMob App ID.
 * 3. Replace the constants below with your real AdMob App ID and Ad Unit IDs before releasing to production.
 */
object AdMobConfig {
    // AdMob App ID (Current: Test App ID / Replace with your real production App ID e.g. "ca-app-pub-8109614254307698~9889727419")
    const val APP_ID = "ca-app-pub-3907415412770701~3347511713"

    // TEST BANNER AD UNIT ID
    // Production replacement: Swap "ca-app-pub-3907415412770701/6300978111" with your real AdMob Banner Ad Unit ID (e.g. "ca-app-pub-8109614254307698/5539418863")
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3907415412770701/6300978111"

    // TEST INTERSTITIAL AD UNIT ID
    // Production replacement: Swap "ca-app-pub-3907415412770701/1033173712" with your real AdMob Interstitial Ad Unit ID (e.g. "ca-app-pub-8109614254307698/4226337191")
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3907415412770701/1033173712"

    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            try {
                MobileAds.initialize(context) { status ->
                    Log.d("AdMobConfig", "MobileAds initialized: $status")
                }
                isInitialized = true
            } catch (e: Exception) {
                Log.e("AdMobConfig", "AdMob init error", e)
            }
        }
    }

    private var interstitialAd: InterstitialAd? = null

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("AdMobConfig", "Interstitial Ad Loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.d("AdMobConfig", "Interstitial Ad failed to load: ${error.message}")
                }
            }
        )
    }

    fun showInterstitialAd(activity: android.app.Activity, onAdDismissed: () -> Unit = {}) {
        interstitialAd?.let { ad ->
            ad.show(activity)
            interstitialAd = null
            // Preload next interstitial
            loadInterstitialAd(activity)
            onAdDismissed()
        } ?: run {
            Log.d("AdMobConfig", "Interstitial Ad not ready yet")
            onAdDismissed()
        }
    }
}

/**
 * Composables for rendering standard AdMob Banner Ads with a fallback banner UI.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    onUpgradeClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFF0D1C2D))
            .border(1.dp, Color(0xFF3B494C)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = AdMobConfig.BANNER_AD_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { adView ->
                adView.loadAd(AdRequest.Builder().build())
            }
        )

        // Overlay fallback styled banner if ad is loading or offline mode
        FallbackAdBannerContent(onUpgradeClick = onUpgradeClick)
    }
}

@Composable
private fun FallbackAdBannerContent(onUpgradeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFF0D1C2D))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF273647))
                    .border(1.dp, Color(0xFF849396), RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "AD",
                    color = Color(0xFFBAC9CC),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Upgrade to LifeVault Pro for Unlimited Cloud Backups",
                color = Color(0xFFBAC9CC),
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Ad Info",
            tint = Color(0xFF849396),
            modifier = Modifier
                .clickable { onUpgradeClick() }
                .padding(4.dp)
        )
    }
}
