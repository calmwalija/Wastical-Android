package net.techandgraphics.quantcal

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.zip
import net.techandgraphics.quantcal.data.remote.account.AccountRequest
import net.techandgraphics.quantcal.data.remote.payment.PaymentRequest
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.quantcal.worker.company.account.CompanyAccountPaymentPlanRequestWorker
import java.io.File
import java.text.DecimalFormat
import java.util.Calendar
import java.util.UUID

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Number.toAmount() = "K${format(this)}"

fun format(number: Number): String = DecimalFormat("#,###").format(number)

fun Context.copyTextToClipboard(text: String) {
  val clipboard = getSystemService(this, ClipboardManager::class.java)
  val clip = ClipData.newPlainText(this.javaClass.simpleName, text)
  clipboard?.setPrimaryClip(clip)
}

fun Bitmap.image2Text(onResult: (Result<String>) -> Unit) {
  onResult(Result.success(""))
}

fun Context.onTextToClipboard(text: String) = copyTextToClipboard(text)

fun Context.getUCropFile(fileId: Number) = File(cacheDir, "$fileId.jpg")

fun Color.toGradient() = Brush.horizontalGradient(
  listOf(this.copy(.7f), this.copy(.8f), this),
)

fun PaymentRequest.asApproved() = copy(status = PaymentStatus.Approved)
fun PaymentRequest.asVerifying() = copy(status = PaymentStatus.Verifying)

val gatewayDrawableRes = listOf(
  R.drawable.im_airtel_money,
  R.drawable.im_eco_bank,
  R.drawable.im_fdh_bank,
  R.drawable.im_first_capital_bank,
  R.drawable.im_inde_bank,
  R.drawable.im_nbs_bank,
  R.drawable.im_national_bank,
  R.drawable.im_standard_bank,
  R.drawable.im_tnm_mpamba,
  R.drawable.im_placeholder,
)

data class Today(val day: Int, val month: Int, val year: Int)

fun getToday(): Today {
  with(Calendar.getInstance()) {
    return Today(
      get(Calendar.DAY_OF_MONTH),
      get(Calendar.MONTH).plus(1),
      get(Calendar.YEAR),
    )
  }
}

fun groupPaymentsByDate(
  payments: List<PaymentWithAccountAndMethodWithGatewayUiModel>,
): Map<String, List<PaymentWithAccountAndMethodWithGatewayUiModel>> {
  return payments
    .sortedByDescending { it.payment.createdAt }
    .groupBy { it.payment.createdAt.toZonedDateTime().defaultDate() }
}

fun Context.openDialer(contact: String) {
  val intent = Intent(Intent.ACTION_DIAL).apply {
    data = "tel:${contact.toPhoneFormat()}".toUri()
  }
  if (intent.resolveActivity(packageManager) != null) {
    startActivity(intent)
  } else {
    toast("No app found to handle making phone calls.")
  }
}

private suspend fun Context.checkIfAccountWorkerExists(
  hasPendingOrRunningWorker: (Boolean) -> Unit,
) {
  with(WorkManager.getInstance(this)) {
    getWorkInfoByIdFlow(
      id = UUID.fromString(AccountRequest::class.java.simpleName),
    ).zip(
      getWorkInfoByIdFlow(
        id = UUID.fromString(CompanyAccountPaymentPlanRequestWorker::class.java.simpleName),
      ),
    ) { accountRequestWorkInfo, accountRequestPlanWorkInfo ->
      Pair(accountRequestWorkInfo, accountRequestPlanWorkInfo)
    }
  }.collectLatest { combinedWorkInfos ->
    val (accountRequestWorkInfo, accountRequestPlanWorkInfo) = combinedWorkInfos

    val hasPendingOrRunningAccountRequestWorker =
      accountRequestWorkInfo?.state == WorkInfo.State.RUNNING ||
        accountRequestWorkInfo?.state == WorkInfo.State.ENQUEUED

    val hasPendingOrRunningAccountPaymentPlanRequestWorker =
      accountRequestPlanWorkInfo?.state == WorkInfo.State.RUNNING ||
        accountRequestPlanWorkInfo?.state == WorkInfo.State.ENQUEUED

    hasPendingOrRunningWorker(
      hasPendingOrRunningAccountRequestWorker ||
        hasPendingOrRunningAccountPaymentPlanRequestWorker,
    )
  }
}
