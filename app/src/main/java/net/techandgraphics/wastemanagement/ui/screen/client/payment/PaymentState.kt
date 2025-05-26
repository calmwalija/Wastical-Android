package net.techandgraphics.wastemanagement.ui.screen.client.payment

import android.net.Uri
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class PaymentState(
  val numberOfMonths: Int = 1,
  val showCropView: Boolean = false,
  val screenshotAttached: Boolean = false,
  val imageUri: Uri? = null,
  val lastPaymentId: Long = 1L,
  val screenshotText: String = "",
  val state: MainActivityState = MainActivityState(),
)
