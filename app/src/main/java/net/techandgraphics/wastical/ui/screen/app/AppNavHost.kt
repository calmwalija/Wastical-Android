package net.techandgraphics.wastical.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.ui.Route
import net.techandgraphics.wastical.ui.activity.MainActivity
import net.techandgraphics.wastical.ui.screen.auth.phone.PhoneNavGraphBuilder
import net.techandgraphics.wastical.ui.screen.auth.phone.PhoneRoute
import net.techandgraphics.wastical.ui.screen.auth.phone.load.LoadEvent
import net.techandgraphics.wastical.ui.screen.auth.phone.load.LoadScreen
import net.techandgraphics.wastical.ui.screen.auth.phone.load.LoadViewModel
import net.techandgraphics.wastical.ui.screen.client.home.ClientHomeEvent
import net.techandgraphics.wastical.ui.screen.client.home.ClientHomeScreen
import net.techandgraphics.wastical.ui.screen.client.home.ClientHomeState
import net.techandgraphics.wastical.ui.screen.client.home.ClientHomeViewModel
import net.techandgraphics.wastical.ui.screen.client.info.ClientInfoNav
import net.techandgraphics.wastical.ui.screen.client.receipt.ClientReceiptEvent
import net.techandgraphics.wastical.ui.screen.client.receipt.ClientReceiptViewModel
import net.techandgraphics.wastical.ui.screen.client.notification.ClientNotificationNav
import net.techandgraphics.wastical.ui.screen.client.payment.ClientPaymentEvent
import net.techandgraphics.wastical.ui.screen.client.payment.ClientPaymentResponseScreen
import net.techandgraphics.wastical.ui.screen.client.payment.ClientPaymentScreen
import net.techandgraphics.wastical.ui.screen.client.payment.ClientPaymentViewModel
import net.techandgraphics.wastical.ui.screen.client.receipt.ClientReceiptScreen
import net.techandgraphics.wastical.ui.screen.client.settings.ClientSettingsNav
import net.techandgraphics.wastical.ui.screen.company.CompanyNavGraphBuilder
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute
import net.techandgraphics.wastical.worker.client.payment.INTENT_EXTRA_GOTO

@Composable
fun AppNavHost(
  navController: NavHostController,
  activity: MainActivity,
) {
  NavHost(
    navController = navController,
    startDestination = Route.Load(false)
  ) {

    composable<Route.Load> {
      with(hiltViewModel<LoadViewModel>()) {
        val state = state.collectAsState().value
        val shouldLogout = it.toRoute<Route.Load>().shouldLogout
        LaunchedEffect(shouldLogout) {
          if (shouldLogout) {
            onEvent(LoadEvent.Logout)
            navController.navigate(PhoneRoute.Verify) { popUpTo(0) }
          }
        }

        LaunchedEffect(Unit) { onEvent(LoadEvent.Load) }

        LoadScreen(state, channel) { event ->
          when (event) {
            LoadEvent.NoAccount -> navController.navigate(PhoneRoute.Verify) { popUpTo(0) }
            is LoadEvent.NoToken ->
              navController.navigate(PhoneRoute.Opt(event.contact)) { popUpTo(0) }

            is LoadEvent.Success -> {
              when (AccountRole.valueOf(event.account.role)) {
                AccountRole.Client -> navController.navigate(Route.Client.Home) { popUpTo(0) }
                AccountRole.Company -> navController.navigate(CompanyRoute.Home) { popUpTo(0) }
              }
            }

            is LoadEvent.Goto.Otp ->
              navController.navigate(PhoneRoute.Opt(event.contact)) { popUpTo(0) }

            else -> onEvent(event)
          }
        }
      }
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
        LaunchedEffect(state) {
          activity.intent?.getStringExtra(INTENT_EXTRA_GOTO)?.let { route ->
            if (state is ClientHomeState.Success) {
              activity.intent = null
              navController.navigate(Route.Client.Notification(state.account.id))
            }
          }
        }
        ClientHomeScreen(state, channel) { event ->
          when (event) {
            is ClientHomeEvent.Goto ->
              when (event) {
                is ClientHomeEvent.Goto.Invoice -> navController.navigate(Route.Client.Receipt(event.id))
                is ClientHomeEvent.Goto.Settings ->
                  navController.navigate(Route.Client.Settings(event.id))

                ClientHomeEvent.Goto.Login -> navController.navigate(Route.Load(true)) { popUpTo(0) }
                ClientHomeEvent.Goto.Reload -> navController.navigate(Route.Load(false)) { popUpTo(0) }

                is ClientHomeEvent.Goto.Notification ->
                  navController.navigate(Route.Client.Notification(event.id))
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

    composable<Route.Client.Receipt> {
      with(hiltViewModel<ClientReceiptViewModel>()) {
        val state = state.collectAsState().value
        val id = it.toRoute<Route.Client.Receipt>().id
        LaunchedEffect(id) { onEvent(ClientReceiptEvent.Load(id)) }
        ClientReceiptScreen(state, channel) { event ->
          when (event) {
            is ClientReceiptEvent.GoTo ->
              when (event) {
                ClientReceiptEvent.GoTo.BackHandler -> navController.navigateUp()
              }

            else -> onEvent(event)
          }
        }
      }
    }

    ClientSettingsNav(navController)
    ClientInfoNav(navController)
    ClientNotificationNav(navController)

  }
}
