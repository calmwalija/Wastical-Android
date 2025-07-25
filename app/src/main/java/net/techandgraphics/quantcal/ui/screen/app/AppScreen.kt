package net.techandgraphics.quantcal.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun AppScreen(
  navController: NavHostController = rememberNavController(),
) {
  AppNavHost(navController)
}
