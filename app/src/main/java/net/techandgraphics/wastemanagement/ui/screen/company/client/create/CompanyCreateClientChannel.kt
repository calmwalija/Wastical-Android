package net.techandgraphics.wastemanagement.ui.screen.company.client.create

import net.techandgraphics.wastemanagement.data.remote.ApiResult

sealed interface CompanyCreateClientChannel {
  data class Success(val id: Long) : CompanyCreateClientChannel
  data class Error(val error: ApiResult.Error) : CompanyCreateClientChannel
}
