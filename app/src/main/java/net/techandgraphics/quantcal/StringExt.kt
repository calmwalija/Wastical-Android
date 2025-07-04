package net.techandgraphics.quantcal

import net.techandgraphics.quantcal.data.local.database.account.AccountTitle
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

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

fun PaymentUiModel.calculate() = 10_000.times(3).toAmount() // TODO

fun PaymentPlanUiModel.calculate(size: Int) = fee.times(size)

fun calculate(plan: PaymentPlanUiModel, pay: PaymentUiModel) = plan.fee.times(pay.id) // TODO

fun imageGatewayUrl(pmId: Long) = AppUrl.FILE_URL.plus("gateway/").plus(pmId)

fun PaymentUiModel.imageScreenshotUrl() =
  AppUrl.FILE_URL.plus("screenshot/$accountId").plus(createdAt)

fun String.capitalize(): String =
  this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun AccountUiModel.toInitials(): String {
  return lastname.first().uppercase()
    .plus(lastname.last().lowercase())
}

fun String.toInitials() = firstOrNull()?.uppercase().plus(lastOrNull()?.lowercase())

fun String.toPhoneFormat() = replace(Regex("(\\d{3})(\\d{3})(\\d{3})"), "+265-$1-$2-$3")

fun AccountUiModel.toInvoice(payment: PaymentUiModel) = "${id.times(5983)}-${payment.createdAt}"

fun Number.toWords() = toEnglishWords()

fun String.toKwacha() = this.plus(" Kwacha")

fun Int.toMonthName() = Month.of(this)
  .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).capitalize()

fun CompanyLocationWithDemographicUiModel.toLocation() =
  demographicArea.name
    .plus(", ")
    .plus(demographicStreet.name)
