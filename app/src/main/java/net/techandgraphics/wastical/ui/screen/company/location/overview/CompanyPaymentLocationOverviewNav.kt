@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.location.overview

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute.ClientProfile
import net.techandgraphics.wastical.ui.screen.company.payment.timeline.PaymentTimelineEvent
import net.techandgraphics.wastical.ui.screen.company.payment.timeline.PaymentTimelineScreen
import net.techandgraphics.wastical.ui.screen.company.payment.timeline.PaymentTimelineViewModel

fun NavGraphBuilder.CompanyPaymentLocationOverviewNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentTimeline> {
    with(hiltViewModel<PaymentTimelineViewModel>()) {
      val state = state.collectAsState().value
      PaymentTimelineScreen(state) { event ->
        when (event) {
          PaymentTimelineEvent.Goto.BackHandler -> navController.navigateUp()
          PaymentTimelineEvent.Load -> Unit
          is PaymentTimelineEvent.Goto.Profile ->
            navController.navigate(ClientProfile(event.id))

          is PaymentTimelineEvent.Button.Filter -> onEvent(event)
        }
      }
    }
  }
}
