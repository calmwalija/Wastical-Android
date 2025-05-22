package net.techandgraphics.wastemanagement

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

fun AccountUiModel.toFullName() = "${this.title.title} ${this.firstname} ${this.lastname}"

fun calculateAmount(plan: PaymentPlanUiModel, pay: PaymentUiModel): String {
  return plan.fee.times(pay.numberOfMonths).toAmount()
}
