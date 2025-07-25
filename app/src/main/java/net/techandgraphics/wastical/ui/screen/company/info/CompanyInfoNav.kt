@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.info

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyInfoNav(navController: NavHostController) {
  composable<CompanyRoute.CompanyInfo> {
    with(hiltViewModel<CompanyInfoViewModel>()) {
      val state = state.collectAsState().value
      CompanyInfoScreen(state) { event ->
        when (event) {
          CompanyInfoEvent.Button.BackHandler -> navController.navigateUp()
          is CompanyInfoEvent.Goto -> when (event) {
            CompanyInfoEvent.Goto.Edit -> Unit
            CompanyInfoEvent.Goto.Method -> navController.navigate(CompanyRoute.PaymentMethod)
            CompanyInfoEvent.Goto.Plan -> navController.navigate(CompanyRoute.PaymentPlan)
          }

          else -> Unit
        }
      }
    }
  }
}
