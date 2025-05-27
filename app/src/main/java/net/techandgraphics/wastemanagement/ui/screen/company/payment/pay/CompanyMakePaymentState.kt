package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay

import android.net.Uri
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class CompanyMakePaymentState(
  val numberOfMonths: Int = 1,
  val showCropView: Boolean = false,
  val screenshotAttached: Boolean = false,
  val imageUri: Uri? = null,
  val lastPaymentId: Long = 1L,
  val screenshotText: String = "",
  val state: MainActivityState = MainActivityState(),
)
