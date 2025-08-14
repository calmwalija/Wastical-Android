package net.techandgraphics.wastical.ui.screen.company.notification

import net.techandgraphics.wastical.domain.model.NotificationUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel

sealed interface CompanyNotificationState {
  data object Loading : CompanyNotificationState
  data class Success(
    val company: CompanyUiModel,
    val notifications: List<NotificationUiModel>,
    val query: String = "",
    val sortDesc: Boolean = true,
  ) : CompanyNotificationState
}
