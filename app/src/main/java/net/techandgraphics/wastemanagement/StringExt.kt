package net.techandgraphics.wastemanagement

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

fun AccountUiModel.toFullName() = "${this.title.title} ${this.firstname} ${this.lastname}"

fun calculateToTextAmount(plan: PaymentPlanUiModel, pay: PaymentUiModel): String {
  return calculate(plan, pay).toAmount()
}

fun calculate(plan: PaymentPlanUiModel, pay: PaymentUiModel) = plan.fee.times(pay.numberOfMonths)

fun imageGatewayUrl(pmId: Long) = AppUrl.FILE_URL.plus("gateway/").plus(pmId)

fun String.capitalize(): String =
  this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun AccountUiModel.toInitials(): String {
  return firstname.first().uppercase()
    .plus(lastname.first().lowercase())
}
