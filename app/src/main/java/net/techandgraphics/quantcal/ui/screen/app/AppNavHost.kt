package net.techandgraphics.quantcal.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.data.remote.account.ACCOUNT_ID
import net.techandgraphics.quantcal.ui.Route
import net.techandgraphics.quantcal.ui.screen.auth.phone.PhoneNavGraphBuilder
import net.techandgraphics.quantcal.ui.screen.client.home.ClientHomeEvent
import net.techandgraphics.quantcal.ui.screen.client.home.ClientHomeViewModel
import net.techandgraphics.quantcal.ui.screen.client.home.HomeScreen
import net.techandgraphics.quantcal.ui.screen.client.invoice.ClientInvoiceEvent
import net.techandgraphics.quantcal.ui.screen.client.invoice.ClientInvoiceScreen
import net.techandgraphics.quantcal.ui.screen.client.invoice.ClientInvoiceViewModel
import net.techandgraphics.quantcal.ui.screen.client.payment.ClientPaymentEvent
import net.techandgraphics.quantcal.ui.screen.client.payment.ClientPaymentResponseScreen
import net.techandgraphics.quantcal.ui.screen.client.payment.ClientPaymentScreen
import net.techandgraphics.quantcal.ui.screen.client.payment.ClientPaymentViewModel
import net.techandgraphics.quantcal.ui.screen.company.CompanyNavGraphBuilder

@Composable
fun AppNavHost(navController: NavHostController) {
  NavHost(
    navController = navController,
    startDestination = Route.Client.Home
  ) {

    PhoneNavGraphBuilder(navController)
    CompanyNavGraphBuilder(navController)

    composable<Route.Client.Payment> {
      with(hiltViewModel<ClientPaymentViewModel>()) {
        val state = state.collectAsState().value
        onEvent(ClientPaymentEvent.Load(ACCOUNT_ID))
        ClientPaymentScreen(state, channel) { event ->
          when (event) {
            is ClientPaymentEvent.Response ->
              navController.navigate(Route.Client.PaymentResponse(event.isSuccess, event.error)) {
                popUpTo(Route.Client.Payment) { inclusive = true }
              }

            ClientPaymentEvent.GoTo.BackHandler -> navController.navigateUp()
            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Client.Home> {
      with(hiltViewModel<ClientHomeViewModel>()) {
        val state = state.collectAsState().value
        onEvent(ClientHomeEvent.Load(ACCOUNT_ID))
        HomeScreen(state) { event ->
          when (event) {
            is ClientHomeEvent.Goto ->
              when (event) {
                ClientHomeEvent.Goto.Invoice -> navController.navigate(Route.Client.Invoice)
              }

            ClientHomeEvent.Button.MakePayment -> navController.navigate(Route.Client.Payment)

            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Client.PaymentResponse> {
      val isSuccess = it.toRoute<Route.Client.PaymentResponse>().isSuccess
      val error = it.toRoute<Route.Client.PaymentResponse>().error
      ClientPaymentResponseScreen(isSuccess = isSuccess, error) { navController.navigateUp() }
    }

    composable<Route.Client.Invoice> {
      with(hiltViewModel<ClientInvoiceViewModel>()) {
        val state = state.collectAsState().value
        ClientInvoiceScreen(state, channel) { event ->
          when (event) {
            is ClientInvoiceEvent.GoTo ->
              when (event) {
                ClientInvoiceEvent.GoTo.BackHandler -> navController.navigateUp()
              }

            else -> onEvent(event)
          }
        }
      }
    }


  }
}
