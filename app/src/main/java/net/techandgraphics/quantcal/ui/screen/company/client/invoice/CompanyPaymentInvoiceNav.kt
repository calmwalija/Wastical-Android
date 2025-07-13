@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.client.invoice

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.openDialer
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyPaymentInvoiceNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentInvoice> {
    with(hiltViewModel<CompanyPaymentInvoiceViewModel>()) {
      val id = it.toRoute<CompanyRoute.PaymentInvoice>().id
      val state = state.collectAsState().value
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(CompanyPaymentInvoiceEvent.Load(id)) }
      CompanyPaymentInvoiceScreen(state) { event ->
        when (event) {
          is CompanyPaymentInvoiceEvent.Button.Phone -> context.openDialer(event.contact)
          CompanyPaymentInvoiceEvent.Goto.BackHandler -> navController.navigateUp()
          is CompanyPaymentInvoiceEvent.Goto.Location -> {
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
