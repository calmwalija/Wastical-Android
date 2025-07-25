@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.info.method

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyInfoMethodNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentMethod> {
    with(hiltViewModel<CompanyInfoMethodViewModel>()) {
      val state = state.collectAsState().value
      CompanyInfoMethodScreen(state) { event ->
        when (event) {
          CompanyInfoMethodEvent.Button.BackHandler -> navController.navigateUp()
          CompanyInfoMethodEvent.Button.Plan -> Unit
          else -> Unit
        }
      }
    }
  }
}
