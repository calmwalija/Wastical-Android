@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.payment.verify

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyVerifyPaymentNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentVerify> {
    with(hiltViewModel<CompanyVerifyPaymentViewModel>()) {
      val state = state.collectAsState().value
      val ofType = it.toRoute<CompanyRoute.PaymentVerify>().ofType
      LaunchedEffect(ofType) { onEvent(CompanyVerifyPaymentEvent.Load(ofType)) }
      CompanyVerifyPaymentScreen(state) { event ->
        when (event) {
          CompanyVerifyPaymentEvent.Goto.BackHandler -> navController.navigateUp()
          is CompanyVerifyPaymentEvent.Goto.Profile ->
            navController.navigate(CompanyRoute.ClientProfile(event.id))

          else -> onEvent(event)
        }
      }
    }
  }
}
