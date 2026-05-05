package com.androidforge.streakup.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androidforge.streakup.presentation.admob.AdMobManager
import com.androidforge.streakup.presentation.ui.add_edit_habit.AddEditHabitScreen
import com.androidforge.streakup.presentation.ui.habit_detail.HabitDetailScreen
import com.androidforge.streakup.presentation.ui.habit_list.HabitListScreen
import com.androidforge.streakup.presentation.ui.onboarding.OnboardingScreen
import com.androidforge.streakup.presentation.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    adMobManager: AdMobManager,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { defaultEnterTransition(this) },
        exitTransition = { defaultExitTransition(this) },
        popEnterTransition = { defaultPopEnterTransition(this) },
        popExitTransition = { defaultPopExitTransition(this) }
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(onOnboardingComplete = {
                navController.navigate(Screen.HabitList.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(route = Screen.HabitList.route) {
            HabitListScreen(
                onNavigateToAddEditHabit = { habitId ->
                    navController.navigate(Screen.AddEditHabit.createRoute(habitId))
                },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(Screen.HabitDetail.createRoute(habitId))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                adMobManager = adMobManager
            )
        }
        composable(
            route = Screen.AddEditHabit.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.LongType; defaultValue = -1L; nullable = true }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId")
            AddEditHabitScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId")
            requireNotNull(habitId)
            HabitDetailScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditHabit = { id -> navController.navigate(Screen.AddEditHabit.createRoute(id)) }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

private val ANIM_DURATION = 400 // milliseconds

// Gentle fade-in and slide from bottom, with a slight bounce (approximated with spring)
private fun defaultEnterTransition(scope: AnimatedContentTransitionScope): EnterTransition {
    return fadeIn(animationSpec = tween(ANIM_DURATION)) +
            slideInVertically(animationSpec = tween(ANIM_DURATION)) { fullHeight -> fullHeight / 4 }
}

private fun defaultExitTransition(scope: AnimatedContentTransitionScope): ExitTransition {
    return fadeOut(animationSpec = tween(ANIM_DURATION)) +
            slideOutVertically(animationSpec = tween(ANIM_DURATION)) { fullHeight -> fullHeight / 4 }
}

private fun defaultPopEnterTransition(scope: AnimatedContentTransitionScope): EnterTransition {
    return fadeIn(animationSpec = tween(ANIM_DURATION)) +
            slideInVertically(animationSpec = tween(ANIM_DURATION)) { fullHeight -> -fullHeight / 4 }
}

private fun defaultPopExitTransition(scope: AnimatedContentTransitionScope): ExitTransition {
    return fadeOut(animationSpec = tween(ANIM_DURATION)) +
            slideOutVertically(animationSpec = tween(ANIM_DURATION)) { fullHeight -> -fullHeight / 4 }
}