package net.techandgraphics.wastical.ui.screen.client.notification

import net.techandgraphics.wastical.domain.model.NotificationUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel

sealed interface ClientNotificationState {
  data object Loading : ClientNotificationState
  data class Success(
    val company: CompanyUiModel,
    val query: String = "",
    val sort: Boolean = false,
    val notifications: List<NotificationUiModel> = listOf(),
  ) : ClientNotificationState
}
