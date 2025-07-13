package net.techandgraphics.quantcal.ui.screen.company

import kotlinx.serialization.Serializable

interface CompanyRoute {

  @Serializable data class ClientInfo(val id: Long) : CompanyRoute

  @Serializable data class ClientProfile(val id: Long) : CompanyRoute

  @Serializable data class PaymentVerify(val ofType: String) : CompanyRoute

  @Serializable data class ClientHistory(val id: Long) : CompanyRoute

  @Serializable data class PaymentInvoice(val id: Long) : CompanyRoute

  @Serializable data class ClientPlan(val id: Long) : CompanyRoute

  @Serializable data class LocationOverview(val id: Long) : CompanyRoute

  @Serializable data class PaymentPending(val id: Long) : CompanyRoute

  @Serializable data class MakePayment(val id: Long) : CompanyRoute

  @Serializable data class ClientLocation(val id: Long) : CompanyRoute

  @Serializable data object PaymentTimeline : CompanyRoute

  @Serializable data object BrowseLocation : CompanyRoute

  @Serializable data object Home : CompanyRoute

  @Serializable data object CompanyReport : CompanyRoute

  @Serializable data object CompanyInfo : CompanyRoute

  @Serializable data object PaymentMethod : CompanyRoute

  @Serializable data object PaymentPlan : CompanyRoute

  @Serializable data object ClientBrowse : CompanyRoute

  @Serializable data object ClientCreate : CompanyRoute
}
