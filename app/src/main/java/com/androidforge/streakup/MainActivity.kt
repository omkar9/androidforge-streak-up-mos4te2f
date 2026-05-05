package com.androidforge.streakup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.androidforge.streakup.presentation.admob.AdMobManager
import com.androidforge.streakup.presentation.navigation.AppNavHost
import com.androidforge.streakup.presentation.navigation.Screen
import com.androidforge.streakup.presentation.theme.StreakUpTheme
import com.androidforge.streakup.presentation.ui.onboarding.OnboardingUiState
import com.androidforge.streakup.presentation.ui.onboarding.OnboardingViewModel
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adMobManager: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // Handle splash screen before content is set

        // Initialize AdMob SDK
        MobileAds.initialize(this) { initializationStatus ->
            Timber.d("AdMob initialized: ${initializationStatus.adapterStatusMap.map { it.key to it.value.initializationState }}")
            adMobManager.loadInterstitialAd() // Load interstitial ad after initialization
        }

        enableEdgeToEdge()
        setContent {
            StreakUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(adMobManager = adMobManager)
                }
            }
        }
    }
}

@Composable
fun MainScreen(adMobManager: AdMobManager, onboardingViewModel: OnboardingViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val onboardingState by onboardingViewModel.uiState.collectAsStateWithLifecycle()

    val startDestination = when (onboardingState) {
        is OnboardingUiState.Loading -> {
            // While loading onboarding status, we can show a placeholder or let the splash screen linger.
            // For navigation, we defer until status is known. If MainScreen is shown, default to onboarding.
            Screen.Onboarding.route
        }
        is OnboardingUiState.Success -> {
            if ((onboardingState as OnboardingUiState.Success).isOnboardingComplete) {
                Screen.HabitList.route
            } else {
                Screen.Onboarding.route
            }
        }
        is OnboardingUiState.Error,
        is OnboardingUiState.Offline -> {
            // If there's an error or offline status, default to onboarding to ensure user sees it.
            Screen.Onboarding.route
        }
    }

    // Only set up NavHost once startDestination is resolved (i.e., not in Loading state)
    if (onboardingState !is OnboardingUiState.Loading) {
        AppNavHost(navController = navController, adMobManager = adMobManager, startDestination = startDestination)
    }
}