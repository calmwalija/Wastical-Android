@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import net.techandgraphics.quantcal.ui.screen.company.client.info.CompanyClientInfoNav
import net.techandgraphics.quantcal.ui.screen.company.client.profile.CompanyClientProfileNav

fun NavGraphBuilder.CompanyNavGraphBuilder(navController: NavHostController) {
  CompanyClientInfoNav(navController)
  CompanyClientProfileNav(navController)
}
