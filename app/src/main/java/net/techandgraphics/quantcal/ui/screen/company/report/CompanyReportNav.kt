@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.report

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyReportNav(navController: NavHostController) {
  composable<CompanyRoute.CompanyReport> {
    with(hiltViewModel<CompanyReportViewModel>()) {
      val state = state.collectAsState().value
      CompanyReportScreen(state) { event ->
        when (event) {
          CompanyReportEvent.Goto.BackHandler -> navController.navigateUp()
          CompanyReportEvent.Load -> Unit
          else -> onEvent(event)
        }
      }
    }
  }
}
