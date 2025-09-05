@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.client.receipt

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.openDialer
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyPaymentReceiptNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentReceipt> {
    with(hiltViewModel<CompanyPaymentReceiptViewModel>()) {
      val id = it.toRoute<CompanyRoute.PaymentReceipt>().id
      val state = state.collectAsState().value
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(CompanyPaymentReceiptEvent.Load(id)) }
      CompanyPaymentReceiptScreen(state) { event ->
        when (event) {
          is CompanyPaymentReceiptEvent.Button.Phone -> context.openDialer(event.contact)
          CompanyPaymentReceiptEvent.Goto.BackHandler -> navController.navigateUp()
          is CompanyPaymentReceiptEvent.Goto.Location -> {
            navController.navigate(CompanyRoute.LocationOverview(event.id)) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive = false
              }
              launchSingleTop = true
            }
          }

          else -> onEvent(event)
        }
      }
    }
  }
}
