package net.techandgraphics.wastemanagement.ui.screen.transaction.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.screen.transaction.bold
import net.techandgraphics.wastemanagement.ui.screen.transaction.extraBold
import net.techandgraphics.wastemanagement.ui.screen.transaction.light
import net.techandgraphics.wastemanagement.ui.screen.transaction.mailMan
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Month
import java.time.ZonedDateTime
import java.util.Locale

private val tableData = Month.entries.mapIndexed { index, month ->
  listOf(
    "${index.plus(1)}",
    month.name.lowercase().capitalize(Locale.ROOT),
    "1",
    "K10,000",
    "K10,000",
  )
}

fun invoiceToPdf(context: Context) {
  val pdfDocument = PdfDocument()

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
      theSentence = "Clear Sight Cleaning Services",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "P.O. Box 40286",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Kanengo,",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )

    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Lilongwe 4",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Phone : +265-992-882-020",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Email : clearsightinvestiments@gmail.com",
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
      theSentence = System.currentTimeMillis().toString(),
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
      theSentence = ZonedDateTime.now().defaultDateTime(),
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
      theSentence = "Dr. James Mike Jr",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Phone : +265-992-882-020",
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
      theSentence = "National Bank",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Account # : 100490012",
      xAxis = xAxis,
      yAxis = yAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    yAxis = yAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    pdfSentence(
      theSentence = "Trans Id Ref : ${System.currentTimeMillis()}",
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
          theSentence = theSentence,
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
      theSentence = 10_000.times(tableData.size).toAmount(),
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
      theSentence = 10_000.times(tableData.size).toAmount(),
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
