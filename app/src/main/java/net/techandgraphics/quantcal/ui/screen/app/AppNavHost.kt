package net.techandgraphics.quantcal.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.data.local.database.AccountRole
import net.techandgraphics.quantcal.ui.Route
import net.techandgraphics.quantcal.ui.activity.MainActivityEvent
import net.techandgraphics.quantcal.ui.activity.MainViewModel
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.auth.phone.PhoneNavGraphBuilder
import net.techandgraphics.quantcal.ui.screen.auth.phone.PhoneRoute
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
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute

@Composable
fun AppNavHost(
  navController: NavHostController,
  viewModel: MainViewModel,
) {
  NavHost(
    navController = navController,
    startDestination = Route.Load(false)
  ) {

    composable<Route.Load> {
      val state = viewModel.state.collectAsState().value
      val logout = it.toRoute<Route.Load>().logout
      LaunchedEffect(Unit) {
        viewModel.onEvent(MainActivityEvent.Nullify(logout))
        viewModel.onEvent(MainActivityEvent.Load)
      }
      LaunchedEffect(state.account) {
        if (state.holding) return@LaunchedEffect
        if (state.account == null)
          navController.navigate(PhoneRoute.Verify) { popUpTo(0) } else {
          when (AccountRole.valueOf(state.account.role)) {
            AccountRole.Client -> navController.navigate(Route.Client.Home) { popUpTo(0) }
            AccountRole.Company -> navController.navigate(CompanyRoute.Home) { popUpTo(0) }
          }
        }
      }
      LoadingIndicatorView()
    }

    PhoneNavGraphBuilder(navController)
    CompanyNavGraphBuilder(navController)

    composable<Route.Client.Payment> {
      with(hiltViewModel<ClientPaymentViewModel>()) {
        val state = state.collectAsState().value
        val id = it.toRoute<Route.Client.Payment>().id
        LaunchedEffect(id) { onEvent(ClientPaymentEvent.Load(id)) }
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
        HomeScreen(state, channel) { event ->
          when (event) {
            is ClientHomeEvent.Goto ->
              when (event) {
                is ClientHomeEvent.Goto.Invoice -> navController.navigate(Route.Client.Invoice(event.id))
                ClientHomeEvent.Goto.Login -> navController.navigate(Route.Load(true)) { popUpTo(0) }
              }

            is ClientHomeEvent.Button.MakePayment ->
              navController.navigate(Route.Client.Payment(event.id))

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
        val id = it.toRoute<Route.Client.Invoice>().id
        LaunchedEffect(id) { onEvent(ClientInvoiceEvent.Load(id)) }
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
