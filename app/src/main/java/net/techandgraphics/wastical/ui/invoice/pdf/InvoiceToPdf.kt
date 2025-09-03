package net.techandgraphics.wastical.ui.invoice.pdf

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation
import androidx.core.text.isDigitsOnly
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.defaultDateTime
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
import net.techandgraphics.wastical.toEnglishWords
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

private fun Context.drawWatermark(canvas: Canvas, pageWidth: Int, pageHeight: Int, text: String) {
  val watermarkPaint = Paint().apply {
    color = "#97808080".toColorInt()
    textSize = 532f
    typeface = extraBold(this@drawWatermark)
    textAlign = Paint.Align.CENTER
    alpha = 24
  }

  canvas.withRotation(-45f, pageWidth / 2f, pageHeight / 2f) {
    drawText(
      text,
      pageWidth / 2f,
      pageHeight / 1.6f,
      watermarkPaint,
    )
  }
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
  val heightInches = 7.1f
  val tableDataHeight = (53 * tableData.size.minus(1)).toFloat()
  val pdfWidthPx = (widthInches * targetDpi).toInt()
  val pdfHeightPx = (heightInches * targetDpi).plus(tableDataHeight).toInt()

  val textSize72 = Paint().apply { textSize = 72f }
  val textSize42 = Paint().apply { textSize = 42f }
  val textSize32 = Paint().apply { textSize = 32f }

  val pdfPageInfo = PdfDocument.PageInfo.Builder(pdfWidthPx, pdfHeightPx, 1).create()
  val pdfPage = pdfDocument.startPage(pdfPageInfo)

  with(pdfPage.canvas) {
    var xAxis = 90f // Vertical
    var yAxis = 160f // Horizontal

    var holdXAxis = 0f
    var holdYAxis = 0f

    context.drawWatermark(this, pdfWidthPx, pdfHeightPx, "PAID")

    ContextCompat.getDrawable(context, R.drawable.im_cs_logo)?.let { logo ->
      val logoWidth = 240
      val logoHeight = 240
      val left = pdfWidthPx - 90 - logoWidth
      val top = 80
      logo.setBounds(left, top, left + logoWidth, top + logoHeight)
      logo.draw(this)
    }

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
      theSentence = "INV-${account.id.times(5983)}-${payment.createdAt}",
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

    /***************************************************************/
    accountContact.contact.takeIf { it.isDigitsOnly() }
      ?.let { contact ->
        yAxis = yAxis.plus(textSize72.textSize.minus(20))
        pdfSentence(
          theSentence = "Phone : ${contact.toPhoneFormat()}",
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
      theSentence = "Total".uppercase(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = bold(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    /***************************************************************/

    holdXAxis = holdXAxis.plus(300)

    /***************************************************************/
    pdfBgSentence(
      theSentence = paymentPlan.fee.times(paymentMonthCovered.size).toAmount(),
      yAxis = yAxis,
      xAxis = holdXAxis,
      paint = textSize32.also { it.typeface = bold(context) },
      withBg = tableData.flatten().size.mod(2) == 1,
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(10))
    val totalAmount = paymentPlan.fee.times(paymentMonthCovered.size)

    pdfSentence(
      theSentence = "Amount in words: ${(totalAmount).toEnglishWords()} only",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )

    yAxis = yAxis.plus(textSize72.textSize.plus(60))

    pdfSentence(
      theSentence = "Received with thanks",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )

    yAxis = yAxis.plus(textSize72.textSize.minus(50))

    val bitmap = BitmapFactory
      .decodeResource(context.resources, R.drawable.im_signature)
      .scale(400, 100)
    drawBitmap(bitmap, xAxis, yAxis, null)
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.plus(80))

    pdfSentence(
      theSentence = "Received by: ${company.name}",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )

    /***************************************************************/
    val footerY = pdfHeightPx - 80f
    pdfSentence(
      theSentence = "Contact: ${companyContact.contact.toPhoneFormat()} | Email: ${companyContact.email ?: company.email}",
      xAxis = xAxis,
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
  val pdfFile = File(context.filesDir, "INV-${account.id.times(5983)}-${payment.createdAt}.pdf")
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
