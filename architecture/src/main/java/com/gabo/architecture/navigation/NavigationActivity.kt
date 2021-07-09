package com.gabo.architecture.navigation

import androidx.navigation.NavController

/**
 *  This interface should be implemented by our activities that use the NavigationComponent.
 *  It provides access to the navController.
 */
interface NavigationActivity {
    val navController : NavController
}