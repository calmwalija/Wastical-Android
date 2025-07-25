@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import net.techandgraphics.wastical.ui.screen.company.client.browse.CompanyBrowseClientNav
import net.techandgraphics.wastical.ui.screen.company.client.create.CompanyCreateClientNav
import net.techandgraphics.wastical.ui.screen.company.client.history.CompanyPaymentHistoryNav
import net.techandgraphics.wastical.ui.screen.company.client.info.CompanyClientInfoNav
import net.techandgraphics.wastical.ui.screen.company.client.invoice.CompanyPaymentInvoiceNav
import net.techandgraphics.wastical.ui.screen.company.client.location.CompanyClientLocationNav
import net.techandgraphics.wastical.ui.screen.company.client.pending.CompanyClientPendingPaymentNav
import net.techandgraphics.wastical.ui.screen.company.client.plan.CompanyClientPlanNav
import net.techandgraphics.wastical.ui.screen.company.client.profile.CompanyClientProfileNav
import net.techandgraphics.wastical.ui.screen.company.home.CompanyHomeNav
import net.techandgraphics.wastical.ui.screen.company.info.CompanyInfoNav
import net.techandgraphics.wastical.ui.screen.company.info.method.CompanyInfoMethodNav
import net.techandgraphics.wastical.ui.screen.company.info.plan.CompanyInfoPlanNav
import net.techandgraphics.wastical.ui.screen.company.location.browse.CompanyBrowseLocationNav
import net.techandgraphics.wastical.ui.screen.company.location.overview.CompanyPaymentLocationOverviewNav
import net.techandgraphics.wastical.ui.screen.company.payment.pay.CompanyMakePaymentNav
import net.techandgraphics.wastical.ui.screen.company.payment.timeline.PaymentTimelineNav
import net.techandgraphics.wastical.ui.screen.company.payment.verify.CompanyVerifyPaymentNav
import net.techandgraphics.wastical.ui.screen.company.report.CompanyReportNav

fun NavGraphBuilder.CompanyNavGraphBuilder(navController: NavHostController) {
  CompanyHomeNav(navController)
  CompanyClientInfoNav(navController)
  CompanyClientProfileNav(navController)
  CompanyBrowseLocationNav(navController)
  PaymentTimelineNav(navController)
  CompanyCreateClientNav(navController)
  CompanyReportNav(navController)
  CompanyClientPlanNav(navController)
  CompanyBrowseClientNav(navController)
  CompanyInfoNav(navController)
  CompanyInfoPlanNav(navController)
  CompanyInfoMethodNav(navController)
  CompanyVerifyPaymentNav(navController)
  CompanyMakePaymentNav(navController)
  CompanyClientPendingPaymentNav(navController)
  CompanyPaymentHistoryNav(navController)
  CompanyClientLocationNav(navController)
  CompanyPaymentInvoiceNav(navController)
  CompanyPaymentLocationOverviewNav(navController)
}
