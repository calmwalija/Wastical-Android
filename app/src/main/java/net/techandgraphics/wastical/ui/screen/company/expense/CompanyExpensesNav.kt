package net.techandgraphics.wastical.ui.screen.company.expense

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyExpensesNav(navController: NavHostController) {
  composable<CompanyRoute.Expenses> {
    val viewModel: CompanyExpensesViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState()
    CompanyExpensesScreen(state = state, onEvent = viewModel::onEvent)
  }
}
