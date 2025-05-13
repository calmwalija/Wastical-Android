package net.techandgraphics.wastemanagement.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import net.techandgraphics.wastemanagement.ui.MainViewModel

@Composable
fun AppScreen(
  viewModel: MainViewModel,
  navController: NavHostController = rememberNavController()
) {
  AppNavHost(navController, viewModel)
}
