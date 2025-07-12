package net.techandgraphics.quantcal.ui.screen.company

import kotlinx.serialization.Serializable

interface CompanyRoute {

  @Serializable data class ClientInfo(val id: Long) : CompanyRoute

  @Serializable data class ClientProfile(val id: Long) : CompanyRoute

  @Serializable data class ClientCreate(val id: Long) : CompanyRoute

  @Serializable data class PaymentVerify(val id: Long) : CompanyRoute

  @Serializable data class ClientBrowse(val id: Long) : CompanyRoute

  @Serializable data class ClientPayment(val id: Long) : CompanyRoute

  @Serializable data class ClientHistory(val id: Long) : CompanyRoute

  @Serializable data class ClientInvoice(val id: Long) : CompanyRoute

  @Serializable data class ClientPlan(val id: Long) : CompanyRoute

  @Serializable data class PaymentPlan(val id: Long) : CompanyRoute

  @Serializable data class PaymentMethod(val id: Long) : CompanyRoute

  @Serializable data class BrowseLocation(val id: Long) : CompanyRoute

  @Serializable data class LocationOverview(val id: Long) : CompanyRoute

  @Serializable data class PaymentPending(val id: Long) : CompanyRoute

  @Serializable data class ClientLocation(val id: Long) : CompanyRoute

  @Serializable data class PaymentTimeline(val id: Long) : CompanyRoute

  @Serializable data object Home : CompanyRoute

  @Serializable data object Report : CompanyRoute
}
