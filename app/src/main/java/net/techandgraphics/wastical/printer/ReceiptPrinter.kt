package net.techandgraphics.wastical.printer

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.defaultTimeMedium
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toPhone265
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.company.payment.pay.CompanyMakePaymentState

object ReceiptPrinter {

  fun printCompanyPaymentReceipt(
    context: Context,
    payment: PaymentRequestEntity,
    state: CompanyMakePaymentState.Success,
  ) {
    fun transId() =
      "TXN-${payment.accountId.times(59_83)}-${payment.createdAt}"

    try {
      val connection = BluetoothPrintersConnections.selectFirstPaired()
      if (connection == null) return

      val printer = EscPosPrinter(connection, 203, 48f, 32)

      val addressLines = state.company.address
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

      val header = buildList {
        add("[C]<b>${state.company.name.uppercase()}</b>")
        addressLines.forEach { add("[C]$it") }
        add("\n")
        add("[C]Receipt of Payment")
        add("[L]-------------------------------")
      }.joinToString(separator = "\n")

      val details = """
        [L]Client  : ${state.account.toFullName()}
        ${if (state.account.username.isDigitsOnly()) "[L]Contact : ${state.account.username.toPhone265()}" else ""}
        [L]Paid In : Cash
        [L]Ref     : ${transId()}
        [L]Date    : ${payment.createdAt.toZonedDateTime().defaultDate()}
        [L]Time    : ${payment.createdAt.toZonedDateTime().defaultTimeMedium()}

      """.trimIndent()

      val body = """
        [L]Months: ${payment.months} x ${state.paymentPlan.fee.toAmount()}
        [L]-------------------------------
        [R]<b>Total: ${payment.months.times(state.paymentPlan.fee).toAmount()}</b>

      """.trimIndent()

      val footer = """
        [C]Thank you for your payment!

      """.trimIndent()

      val barcode = """
        [C]<barcode>${System.currentTimeMillis()}</barcode>
      """.trimIndent()

      printer.printFormattedText(
        listOf(header, details, body, footer, barcode).joinToString(separator = "\n"),
      )

      printer.disconnectPrinter()
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }
}
