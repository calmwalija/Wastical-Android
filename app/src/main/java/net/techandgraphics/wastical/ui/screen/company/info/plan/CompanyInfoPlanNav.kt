@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.info.plan

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyInfoPlanNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentPlan> {
    with(hiltViewModel<CompanyInfoPlanViewModel>()) {
      val state = state.collectAsState().value
      CompanyInfoPlanScreen(state) { event ->
        when (event) {
          CompanyInfoPlanEvent.Button.BackHandler -> navController.navigateUp()
          CompanyInfoPlanEvent.Button.Plan -> Unit
        }
      }
    }
  }
}
