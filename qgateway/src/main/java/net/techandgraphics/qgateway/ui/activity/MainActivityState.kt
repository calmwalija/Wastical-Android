package net.techandgraphics.qgateway.ui.activity

import net.techandgraphics.qgateway.domain.model.SmsUiModel

sealed interface MainActivityState {
  data object Loading : MainActivityState
  data class Success(
    val messages: List<SmsUiModel> = listOf(),
  ) : MainActivityState
}
