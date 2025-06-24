package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

import net.techandgraphics.wastemanagement.data.remote.ApiResult

sealed interface CompanyClientPlanChannel {
  data object Processing : CompanyClientPlanChannel
  data class Error(val error: ApiResult.Error) : CompanyClientPlanChannel
  data object Success : CompanyClientPlanChannel
}
