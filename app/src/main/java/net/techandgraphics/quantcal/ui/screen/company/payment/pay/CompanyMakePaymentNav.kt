@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.payment.pay

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyMakePaymentNav(navController: NavHostController) {
  composable<CompanyRoute.MakePayment> {
    with(hiltViewModel<CompanyMakePaymentViewModel>()) {
      val id = it.toRoute<CompanyRoute.MakePayment>().id
      val state = state.collectAsState().value
      LaunchedEffect(id) { onEvent(CompanyMakePaymentEvent.Load(id)) }
      CompanyMakePaymentScreen(state, channel) { event ->
        when (event) {
          CompanyMakePaymentEvent.GoTo.BackHandler -> navController.navigateUp()
          else -> onEvent(event)
        }
      }
    }
  }
}
