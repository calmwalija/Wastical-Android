package net.techandgraphics.quantcal.ui.screen.client.invoice.pdf

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.scale
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.calculate
import net.techandgraphics.quantcal.capitalize
import net.techandgraphics.quantcal.data.remote.payment.PaymentType
import net.techandgraphics.quantcal.defaultDateTime
import net.techandgraphics.quantcal.domain.model.account.AccountContactUiModel
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyContactUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toPhoneFormat
import net.techandgraphics.quantcal.toZonedDateTime
import net.techandgraphics.quantcal.ui.screen.client.invoice.bold
import net.techandgraphics.quantcal.ui.screen.client.invoice.extraBold
import net.techandgraphics.quantcal.ui.screen.client.invoice.light
import net.techandgraphics.quantcal.ui.theme.Orange
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Month

private fun tableData(
  paymentMonthCovered: List<PaymentMonthCoveredUiModel> = listOf(),
  paymentPlan: PaymentPlanUiModel,
) = paymentMonthCovered.mapIndexed { index, month ->
  listOf(
    index.plus(1).toString(),
    Month.of(month.month).name.capitalize(),
    1,
    paymentPlan.fee.toAmount(),
    paymentPlan.calculate(1).toAmount(),
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
    pdfSentence(
      theSentence = "Paid".uppercase(),
      xAxis = 1230f,
      yAxis = 240f,
      paint = textSize520.also {
        it.typeface = extraBold(context)
        it.color = Orange.toArgb()
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
      theHeading = "Invoice #",
      yAxis = yAxis,
      xAxis = xAxis,
      paint = textSize42.also {
        it.typeface = bold(context)
        it.color = Orange.toArgb()
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

    yAxis = yAxis.plus(textSize72.textSize.times(1.7f))

    /***************************************************************/
    pdfHeadingView(
      theHeading = "Received From",
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
      pdfSentence(
        theSentence = "Account # : ${paymentMethod.account}",
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
      theSentence = paymentPlan.calculate(paymentMonthCovered.size).toAmount(),
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

    holdXAxis = holdXAxis.plus(300)

    /***************************************************************/
    pdfBgSentence(
      theSentence = paymentPlan.calculate(paymentMonthCovered.size).toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = extraBold(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    /***************************************************************/

    /***************************************************************/
    val bitmap = BitmapFactory
      .decodeResource(context.resources, R.drawable.im_signature)
      .scale(500, 200)
    drawBitmap(bitmap, xAxis, yAxis, null)
    /***************************************************************/

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
