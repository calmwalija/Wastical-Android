package net.techandgraphics.wastemanagement

import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

fun AccountUiModel.toFullName() =
  "${if (this.title == AccountTitle.Na) "" else this.title} ${this.firstname} ${this.lastname}"
    .trim()

fun toFullName(title: String, firstname: String, lastname: String): String {
  val accountTitle = AccountTitle.valueOf(title)
  return "${if (accountTitle == AccountTitle.Na) "" else accountTitle.title} $firstname $lastname"
    .trim()
}

fun calculateToTextAmount(plan: PaymentPlanUiModel, pay: PaymentUiModel): String {
  return calculate(plan, pay).toAmount()
}

fun PaymentUiModel.calculate() = paymentPlanFee.times(numberOfMonths).toAmount()

fun calculate(plan: PaymentPlanUiModel, pay: PaymentUiModel) = plan.fee.times(pay.numberOfMonths)

fun imageGatewayUrl(pmId: Long) = AppUrl.FILE_URL.plus("gateway/").plus(pmId)

fun PaymentUiModel.imageScreenshotUrl() =
  AppUrl.FILE_URL.plus("screenshot/$accountId").plus(createdAt)

fun String.capitalize(): String =
  this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun AccountUiModel.toInitials(): String {
  return lastname.first().uppercase()
    .plus(lastname.last().lowercase())
}

fun String.toInitials() = first().uppercase().plus(last().lowercase())

fun String.toPhoneFormat() = replace(Regex("(\\d{3})(\\d{3})(\\d{3})"), "+265-$1-$2-$3")
