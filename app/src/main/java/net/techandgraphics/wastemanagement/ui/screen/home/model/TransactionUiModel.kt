package net.techandgraphics.wastemanagement.ui.screen.home.model

import java.time.ZonedDateTime

data class TransactionUiModel(
  val id: Long = System.currentTimeMillis(),
  val paymentMethod: String,
  val numberOfMonths: Int,
  val drawableRes: Int,
  val date: ZonedDateTime = ZonedDateTime.now(),
)
