package net.techandgraphics.wastical.ui.invoice.pdf

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.hash
import net.techandgraphics.wastical.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toMonthName
import net.techandgraphics.wastical.toPhoneFormat
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.client.invoice.bold
import net.techandgraphics.wastical.ui.screen.client.invoice.extraBold
import net.techandgraphics.wastical.ui.screen.client.invoice.light
import net.techandgraphics.wastical.ui.theme.Orange
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private fun tableData(
  paymentMonthCovered: List<PaymentMonthCoveredUiModel> = listOf(),
  paymentPlan: PaymentPlanUiModel,
) = paymentMonthCovered.mapIndexed { index, month ->
  listOf(
    index.plus(1).toString(),
    month.month.toMonthName().plus(" ${month.year}"),
    1,
    paymentPlan.fee.toAmount(),
    paymentPlan.fee.toAmount(),
  )
}

private fun amountInWords(amount: Int): String {
  if (amount == 0) return "zero"

  val units = arrayOf(
    "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
    "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
    "seventeen", "eighteen", "nineteen",
  )
  val tens = arrayOf(
    "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
  )

  fun toWords(n: Int): String {
    return when {
      n < 20 -> units[n]
      n < 100 -> tens[n / 10] + if (n % 10 != 0) "-" + units[n % 10] else ""
      n < 1000 -> units[n / 100] + " hundred" + if (n % 100 != 0) " and " + toWords(n % 100) else ""
      n < 1_000_000 -> toWords(n / 1000) + " thousand" + if (n % 1000 != 0) " " + toWords(n % 1000) else ""
      n < 1_000_000_000 -> toWords(n / 1_000_000) + " million" + if (n % 1_000_000 != 0) " " + toWords(n % 1_000_000) else ""
      else -> n.toString()
    }
  }

  return toWords(amount)
}

fun invoiceToPdf(
  context: Context,
  company: CompanyUiModel,
  companyContact: CompanyContactUiModel,
  account: AccountUiModel,
  accountContact: AccountContactUiModel,
  payment: PaymentUiModel,
  paymentPlan: PaymentPlanUiModel,
  paymentMethod: PaymentMethodUiModel,
  paymentGateway: PaymentGatewayUiModel,
  paymentMonthCovered: List<PaymentMonthCoveredUiModel> = listOf(),
  onEvent: (File?) -> Unit,
) {
  val pdfDocument = PdfDocument()

  val tableData = tableData(paymentMonthCovered, paymentPlan)
  val targetDpi = 300f
  val widthInches = 5.27f
  val heightInches = 6.1f
  val tableDataHeight = (53 * tableData.size.minus(1)).toFloat()
  val pdfWidthPx = (widthInches * targetDpi).toInt()
  val pdfHeightPx = (heightInches * targetDpi).plus(tableDataHeight).toInt()

  val textSize72 = Paint().apply { textSize = 72f }
  val textSize42 = Paint().apply { textSize = 42f }
  val textSize32 = Paint().apply { textSize = 32f }
  val textSize520 = Paint().apply { textSize = 100f }

  val pdfPageInfo = PdfDocument.PageInfo.Builder(pdfWidthPx, pdfHeightPx, 1).create()
  val pdfPage = pdfDocument.startPage(pdfPageInfo)

  with(pdfPage.canvas) {
    var xAxis = 90f // Vertical
    var yAxis = 160f // Horizontal

    var holdXAxis = 0f
    var holdYAxis = 0f

    pdfRectangle()

    /***************************************************************/
    // Company logo (top-right)
    ContextCompat.getDrawable(context, R.drawable.ic_logo)?.let { logo ->
      val logoWidth = 250
      val logoHeight = 250
      val left = pdfWidthPx - 90 - logoWidth
      val top = 80
      logo.setBounds(left, top, left + logoWidth, top + logoHeight)
      logo.draw(this)
    }
    /***************************************************************/

    /***************************************************************/
    val isApproved = payment.status == PaymentStatus.Approved
    val watermark = if (isApproved) "PAID" else "DUE"
    pdfSentence(
      theSentence = watermark,
      xAxis = 1230f,
      yAxis = 240f,
      paint = textSize520.also {
        it.typeface = extraBold(context)
        it.color = if (isApproved) Orange.toArgb() else Color.LTGRAY
      },
    )
    /***************************************************************/

    pdfHeadingView(
      theHeading = "Invoice".uppercase(),
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize72.also {
        it.typeface = bold(context)
        it.color = Orange.toArgb()
      },
    ).run { yAxis = this.yAxis }

    /***************************************************************/
    pdfSentence(
      theSentence = company.name,
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = company.address,
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Phone : ${companyContact.contact.toPhoneFormat()}",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Email : ${companyContact.email}",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.times(1.7f))

    holdYAxis = yAxis

    /***************************************************************/
    pdfHeadingView(
      theHeading = "Invoice No.",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = Orange.toArgb()
      },
    ).run { yAxis = this.yAxis.plus(10) }
    /***************************************************************/

    pdfSentence(
      theSentence = "INV-" + payment.id.toString().padStart(6, '0'),
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )

    yAxis = yAxis.plus(textSize72.textSize.plus(30))

    /***************************************************************/
    pdfHeadingView(
      theHeading = "Date & Time",
      yAxis = holdYAxis,
      xAxis = xAxis.times(8),
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = Orange.toArgb()
      },
    ).run { yAxis = this.yAxis.plus(10) }
    /***************************************************************/

    pdfSentence(
      theSentence = payment.createdAt.toZonedDateTime().defaultDateTime(),
      yAxis = yAxis,
      xAxis = xAxis.times(8),
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    // Verification URL placeholder
    val verifyHash = payment.id.hash("invoice").take(10)
    val verifyUrl = "https://wastical.app/invoice/" + payment.id + "?v=" + verifyHash
    yAxis = yAxis.plus(textSize72.textSize.minus(20))
    pdfSentence(
      theSentence = "Verify: $verifyUrl",
      yAxis = yAxis,
      xAxis = xAxis.times(8),
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.times(1.7f))

    /***************************************************************/
    pdfHeadingView(
      theHeading = "Bill To",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = Orange.toArgb()
      },
    ).run { yAxis = this.yAxis.plus(10) }
    /***************************************************************/

    /***************************************************************/
    pdfSentence(
      theSentence = account.toFullName(),
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Phone : ${accountContact.contact.toPhoneFormat()}",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    if (!account.email.isNullOrEmpty()) {
      pdfSentence(
        theSentence = "Email : ${account.email}",
        xAxis = xAxis,
        yAxis = yAxis,
        paint = textSize32.also { it.typeface = light(context) },
      )
    }
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.times(1.7f))

    /***************************************************************/
    pdfHeadingView(
      theHeading = "Payment Info",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = Orange.toArgb()
      },
    ).run { yAxis = this.yAxis.plus(10) }
    /***************************************************************/

    /***************************************************************/
    pdfSentence(
      theSentence = paymentGateway.name,
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    if (paymentGateway.type != PaymentType.Cash.name) {
      val acc = paymentMethod.account
      val masked = if (acc.length > 4) "".padEnd(acc.length - 4, '*') + acc.takeLast(4) else acc
      pdfSentence(
        theSentence = "Account # : $masked",
        xAxis = xAxis,
        yAxis = yAxis,
        paint = textSize32.also { it.typeface = light(context) },
      )
      yAxis = yAxis.plus(textSize72.textSize.minus(20))
    }
    /***************************************************************/

    /***************************************************************/
    pdfSentence(
      theSentence = "Trans Id Ref : ${payment.transactionId}",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.plus(40))

    holdXAxis = xAxis
    holdYAxis = yAxis

    val tableHead = listOf("#", "Description", "Qty", "Rate", "Amount")

    val spacer = listOf(200, 500, 200, 300, 300)

    tableHead.forEachIndexed { index, heading ->
      pdfSentence(
        theSentence = heading,
        xAxis = holdXAxis,
        yAxis = yAxis,
        paint = textSize32.also { it.typeface = light(context) },
      )
      holdXAxis = holdXAxis.plus(spacer[index])
    }

    holdYAxis = yAxis.plus(textSize72.textSize.minus(10))
    holdXAxis = xAxis

    tableData.forEachIndexed { i, theData ->

      theData.forEachIndexed { index, theSentence ->
        pdfBgSentence(
          theSentence = theSentence.toString(),
          xAxis = holdXAxis,
          yAxis = holdYAxis,
          paint = textSize32.also { it.typeface = light(context) },
          withBg = i.mod(2) == 0,
        )
        holdXAxis = holdXAxis.plus(spacer[index])
      }

      holdYAxis = holdYAxis.plus(60)
      holdXAxis = xAxis
    }

    yAxis = holdYAxis
    holdXAxis = xAxis
    holdXAxis = holdXAxis.times(11f)

    /***************************************************************/
    pdfBgSentence(
      theSentence = "Subtotal",
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 0,
    )
    /***************************************************************/

    holdXAxis = holdXAxis.plus(300)

    /***************************************************************/
    pdfBgSentence(
      theSentence = paymentPlan.fee.times(paymentMonthCovered.size).toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 0,
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(10))

    holdXAxis = xAxis
    holdXAxis = holdXAxis.times(11f)

    /***************************************************************/
    pdfBgSentence(
      theSentence = "Grand Total".uppercase(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = extraBold(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    /***************************************************************/

    holdXAxis = holdXAxis.plus(300)

    /***************************************************************/
    pdfBgSentence(
      theSentence = paymentPlan.fee.times(paymentMonthCovered.size).toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = extraBold(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )

    val totalAmount = paymentPlan.fee.times(paymentMonthCovered.size)
    yAxis = yAxis.plus(textSize72.textSize.minus(10))

    // Discount (0.00)
    holdXAxis = xAxis
    holdXAxis = holdXAxis.times(11f)
    pdfBgSentence(
      theSentence = "Discount",
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 0,
    )
    holdXAxis = holdXAxis.plus(300)
    pdfBgSentence(
      theSentence = 0.toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 0,
    )

    yAxis = yAxis.plus(textSize72.textSize.minus(10))

    // Tax (0.00)
    holdXAxis = xAxis
    holdXAxis = holdXAxis.times(11f)
    pdfBgSentence(
      theSentence = "Tax",
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    holdXAxis = holdXAxis.plus(300)
    pdfBgSentence(
      theSentence = 0.toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )

    yAxis = yAxis.plus(textSize72.textSize.minus(10))

    // Fees (0.00)
    holdXAxis = xAxis
    holdXAxis = holdXAxis.times(11f)
    pdfBgSentence(
      theSentence = "Fees",
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 0,
    )
    holdXAxis = holdXAxis.plus(300)
    pdfBgSentence(
      theSentence = 0.toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 0,
    )

    yAxis = yAxis.plus(textSize72.textSize.minus(10))

    // Outstanding Balance
    holdXAxis = xAxis
    holdXAxis = holdXAxis.times(11f)
    pdfBgSentence(
      theSentence = "Outstanding Balance",
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    holdXAxis = holdXAxis.plus(300)
    val outstanding = if (isApproved) 0 else totalAmount
    pdfBgSentence(
      theSentence = outstanding.toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = light(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )

    yAxis = yAxis.plus(textSize72.textSize.minus(10))

    // Amount in words
    pdfSentence(
      theSentence = "Amount in words: ${amountInWords(totalAmount).replaceFirstChar { it.uppercase() }} only",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    /***************************************************************/
    // Acknowledgement and signature (approved only)
    if (isApproved) {
      pdfSentence(
        theSentence = "Received with thanks",
        xAxis = xAxis,
        yAxis = yAxis,
        paint = textSize42.also {
          it.typeface = bold(context)
          it.color = Orange.toArgb()
        },
      )

      yAxis = yAxis.plus(textSize72.textSize.minus(10))

      val bitmap = BitmapFactory
        .decodeResource(context.resources, R.drawable.im_signature)
        .scale(500, 200)
      drawBitmap(bitmap, xAxis, yAxis, null)

      yAxis = yAxis.plus(220f)

      pdfSentence(
        theSentence = "Received by: ${company.name}",
        xAxis = xAxis,
        yAxis = yAxis,
        paint = textSize32.also { it.typeface = light(context) },
      )

      yAxis = yAxis.plus(textSize72.textSize.minus(20))

      pdfSentence(
        theSentence = "Title: Cashier",
        xAxis = xAxis,
        yAxis = yAxis,
        paint = textSize32.also { it.typeface = light(context) },
      )

      yAxis = yAxis.plus(textSize72.textSize.minus(20))

      pdfSentence(
        theSentence = "Date: ${payment.createdAt.toZonedDateTime().defaultDateTime()}",
        xAxis = xAxis,
        yAxis = yAxis,
        paint = textSize32.also { it.typeface = light(context) },
      )
    }
    /***************************************************************/

    // Footer
    val footerY = pdfHeightPx - 60f
    pdfSentence(
      theSentence = "Contact: ${companyContact.contact.toPhoneFormat()} | Email: ${companyContact.email ?: company.email}",
      xAxis = xAxis,
      yAxis = footerY,
      paint = textSize32.also { it.typeface = light(context) },
    )
    pdfSentence(
      theSentence = "Page 1 of 1",
      xAxis = pdfWidthPx - 400f,
      yAxis = footerY,
      paint = textSize32.also { it.typeface = light(context) },
    )

    pdfDocument.finishPage(pdfPage)
    onEvent.invoke(pdfDocument.saveToInternal(context, payment, account))
    pdfDocument.close()
  }
}

private fun PdfDocument.saveToInternal(
  context: Context,
  payment: PaymentUiModel,
  account: AccountUiModel,
): File? {
  val pdfFile = File(context.filesDir, "Invoice-${account.id.times(5983)}-${payment.createdAt}.pdf")
  return try {
    val fileOutputStream = FileOutputStream(pdfFile)
    writeTo(fileOutputStream)
    fileOutputStream.close()
    pdfFile
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
}
