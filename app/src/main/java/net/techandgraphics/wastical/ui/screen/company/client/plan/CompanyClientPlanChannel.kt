package net.techandgraphics.wastical.ui.screen.company.client.plan

import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface CompanyClientPlanChannel {
  data object Processing : CompanyClientPlanChannel
  data class Error(val error: ApiResult.Error) : CompanyClientPlanChannel
  data object Success : CompanyClientPlanChannel
}
