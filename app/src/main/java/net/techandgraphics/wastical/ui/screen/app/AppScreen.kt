package net.techandgraphics.wastical.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import net.techandgraphics.wastical.ui.activity.MainActivity

@Composable
fun AppScreen(
  navController: NavHostController = rememberNavController(),
  activity: MainActivity,
) {
  AppNavHost(navController, activity)
}
