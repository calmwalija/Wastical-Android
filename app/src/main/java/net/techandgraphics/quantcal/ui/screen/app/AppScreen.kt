package net.techandgraphics.quantcal.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import net.techandgraphics.quantcal.ui.activity.MainActivityState
import net.techandgraphics.quantcal.ui.activity.MainViewModel

@Composable
fun AppScreen(
  viewModel: MainViewModel,
  navController: NavHostController = rememberNavController(),
) {
  AppNavHost(navController, viewModel)
}
