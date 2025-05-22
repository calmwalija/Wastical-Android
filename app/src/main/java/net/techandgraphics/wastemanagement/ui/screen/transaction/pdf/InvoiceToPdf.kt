package net.techandgraphics.wastemanagement.ui.screen.transaction.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import net.techandgraphics.wastemanagement.calculateAmount
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.screen.payment.paymentMethod
import net.techandgraphics.wastemanagement.ui.screen.transaction.bold
import net.techandgraphics.wastemanagement.ui.screen.transaction.extraBold
import net.techandgraphics.wastemanagement.ui.screen.transaction.light
import net.techandgraphics.wastemanagement.ui.screen.transaction.mailMan
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Month

private fun tableData(
  payment: PaymentUiModel,
  paymentPlan: PaymentPlanUiModel,
) = Month.entries.take(1).mapIndexed { index, month ->
  listOf(
    "1",
    "${paymentPlan.period.name} Subscription",
    payment.numberOfMonths,
    paymentPlan.fee.toAmount(),
    calculateAmount(paymentPlan, payment),
  )
}

fun invoiceToPdf(
  context: Context,
  company: CompanyUiModel,
  companyContact: CompanyContactUiModel,
  account: AccountUiModel,
  accountContact: AccountContactUiModel,
  payment: PaymentUiModel,
  paymentPlan: PaymentPlanUiModel,
) {
  val pdfDocument = PdfDocument()

  val tableData = tableData(payment, paymentPlan)
  val targetDpi = 300f
  val widthInches = 5.27f
  val heightInches = 6.8f
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
    pdfSentence(
      theSentence = "Paid".uppercase(),
      xAxis = 1230f,
      yAxis = 240f,
      paint = textSize520.also {
        it.typeface = extraBold(context)
        it.color = Color.LTGRAY
      },
    )
    /***************************************************************/

    pdfHeadingView(
      theHeading = "Invoice".uppercase(),
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize72.also {
        it.typeface = bold(context)
        it.color = Color.GRAY
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
      theSentence = "Phone : ${companyContact.contact}",
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
      theHeading = "Invoice #",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = android.graphics.Color.GRAY
      },
    ).run { yAxis = this.yAxis.plus(10) }
    /***************************************************************/

    pdfSentence(
      theSentence = "${account.id.times(5983)}-${payment.createdAt}",
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
        it.color = android.graphics.Color.GRAY
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

    yAxis = yAxis.plus(textSize72.textSize.times(1.7f))

    /***************************************************************/
    pdfHeadingView(
      theHeading = "Received From",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = android.graphics.Color.GRAY
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
      theSentence = "Phone : ${accountContact.contact}",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.times(1.7f))

    /***************************************************************/
    pdfHeadingView(
      theHeading = "Payment Info",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = android.graphics.Color.GRAY
      },
    ).run { yAxis = this.yAxis.plus(10) }
    /***************************************************************/

    /***************************************************************/
    pdfSentence(
      theSentence = paymentMethod.name,
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Account # : ${paymentMethod.account}",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

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

    holdXAxis = holdXAxis.plus(310)

    /***************************************************************/
    pdfBgSentence(
      theSentence = calculateAmount(paymentPlan, payment),
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
      theSentence = "Total".uppercase(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = extraBold(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    /***************************************************************/

    holdXAxis = holdXAxis.plus(310)

    /***************************************************************/
    pdfBgSentence(
      theSentence = calculateAmount(paymentPlan, payment),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = extraBold(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.plus(120))

    /***************************************************************/
    pdfSentence(
      theSentence = "With Thanks",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = Paint().also {
        it.typeface = mailMan(context)
        it.textSize = 120f
      },
    )
    /***************************************************************/

    pdfDocument.finishPage(pdfPage)
    pdfDocument.saveToInternal(context)

    pdfDocument.close()
  }
}

private fun PdfDocument.saveToInternal(context: Context): File? {
  val pdfFile = File(context.filesDir, "129048242453.pdf")
  return try {
    val fileOutputStream = FileOutputStream(pdfFile)
    writeTo(fileOutputStream)
    fileOutputStream.close()
    context.toast("Okay")
    pdfFile
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
}
