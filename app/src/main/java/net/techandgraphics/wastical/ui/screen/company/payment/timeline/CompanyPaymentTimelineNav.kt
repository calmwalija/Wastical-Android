@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyPaymentTimelineNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentTimeline> {
    with(hiltViewModel<CompanyPaymentTimelineViewModel>()) {
      LaunchedEffect(Unit) { onEvent(CompanyPaymentTimelineEvent.Load) }
      val state = state.collectAsState().value
      CompanyPaymentTimelineScreen(state) { event ->
        when (event) {
          CompanyPaymentTimelineEvent.Button.BackHandler -> navController.navigateUp()
          is CompanyPaymentTimelineEvent.Goto.Invoice ->
            navController.navigate(CompanyRoute.PaymentReceipt(event.id))

          else -> onEvent(event)
        }
      }
    }
  }
}
