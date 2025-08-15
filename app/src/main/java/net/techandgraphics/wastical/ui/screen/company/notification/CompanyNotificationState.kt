package net.techandgraphics.wastical.ui.screen.company.notification

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.domain.model.NotificationUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel

sealed interface CompanyNotificationState {
  data object Loading : CompanyNotificationState
  data class Success(
    val company: CompanyUiModel,
    val notifications: Flow<PagingData<NotificationUiModel>> = flow { },
    val query: String = "",
    val sort: Boolean = true,
  ) : CompanyNotificationState
}
