package net.techandgraphics.wastemanagement.data.remote

import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.company.CompanyApi
import net.techandgraphics.wastemanagement.keycloak.KeycloakApi

data class AppApi(
  val accountApi: AccountApi,
  val companyApi: CompanyApi,
  val keycloakApi: KeycloakApi,
)
