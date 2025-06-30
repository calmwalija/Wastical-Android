package net.techandgraphics.wastemanagement.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import net.techandgraphics.wastemanagement.ui.activity.MainActivityState

@Composable
fun AppScreen(
  state: MainActivityState,
  navController: NavHostController = rememberNavController()
) {
  AppNavHost(navController, state)
}
