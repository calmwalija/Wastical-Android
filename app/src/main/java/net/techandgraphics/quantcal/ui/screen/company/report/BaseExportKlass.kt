package net.techandgraphics.quantcal.ui.screen.company.report

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.text.isDigitsOnly
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.getAccountTitle
import net.techandgraphics.quantcal.toShortMonthName
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BaseExportKlass<T>(context: Context) : ContextWrapper(context) {

  private val pdfDocument = PdfDocument()

  operator fun invoke(
    company: CompanyUiModel,
    pageTitle: String,
    columnHeaders: List<String>,
    columnWidths: List<Float>,
    filename: String,
    items: List<T>,
    valueExtractor: (T) -> List<String>,
    onEvent: (File?) -> Unit,
  ) {
    require(columnHeaders.size == columnWidths.size) {
      "Column headers and widths must have the same size"
    }
    val pageWidth = 595
    val pageHeight = 842
    val margin = 20f
    val lineHeight = 25f
    val titleHeight = 40f
    val startYHeader = 80f

    var currentY = startYHeader + 30f
    var pageIndex = 1
    var canvas: Canvas
    var page =
      pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create())
    canvas = page.canvas

    val paint = Paint().apply {
      color = Color.BLACK
      textSize = 13f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    val titlePaint = Paint(paint).apply {
      textSize = 18f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    fun drawHeader(canvas: Canvas) {
      canvas.drawText(pageTitle, margin, titleHeight, titlePaint)
      var x = margin
      columnHeaders.forEachIndexed { i, header ->
        canvas.drawText(header, x, startYHeader, paint)
        x += columnWidths[i]
      }
    }

    drawHeader(canvas)

    items.forEachIndexed { index, item ->
      if (currentY > pageHeight - 60) {
        pdfDocument.finishPage(page)
        pageIndex++
        page =
          pdfDocument.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create(),
          )
        canvas = page.canvas
        currentY = startYHeader + 30f
        drawHeader(canvas)
      }

      var x = margin
      val values = listOf(index.plus(1).toString()) + valueExtractor(item)

      values.forEachIndexed { i, value ->
        canvas.drawText(value, x, currentY, paint)
        x += columnWidths[i]
      }

      currentY += lineHeight
    }

    pdfDocument.finishPage(page)
    onEvent(pdfDocument.saveToInternal(filename))
    pdfDocument.close()
  }

  private fun PdfDocument.saveToInternal(filename: String): File? {
    val pdfFile = File(filesDir, "$filename.pdf")
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
}

fun exportCoverageToPdf(
  context: Context,
  coverageList: List<PaymentCoverageRow>,
  months: List<Int>,
  onEvent: (File?) -> Unit,
) {
  val document = PdfDocument()

  val pageWidth = 842
  val pageHeight = 595
  val margin = 20f
  val lineHeight = 30f

  val headers = listOf("", "Full Name", "Phone") + months.map { it.toShortMonthName() }
  val columnWidths = listOf(60f, 180f, 100f) + List(months.size) { 60f }

  var currentY = margin + 40f
  var pageNumber = 1
  var page =
    document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
  var canvas = page.canvas

  val titlePaint = Paint().apply {
    textSize = 18f
    typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
    color = Color.BLACK
  }

  val paint = Paint().apply {
    textSize = 14f
    color = Color.BLACK
  }

  fun drawHeaders() {
    var x = margin
    headers.forEachIndexed { i, header ->
      canvas.drawText(header, x, currentY, paint)
      x += columnWidths[i]
    }
    currentY += lineHeight
  }

  canvas.drawText("Payment Coverage Report", pageWidth / 2f - 120f, margin + 20f, titlePaint)
  drawHeaders()

  coverageList.forEachIndexed { index, row ->
    if (currentY + lineHeight > pageHeight - margin) {
      document.finishPage(page)
      pageNumber++
      page =
        document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
      canvas = page.canvas
      currentY = margin + 40f
      drawHeaders()
    }

    var x = margin
    val tick = { paid: Boolean -> if (paid) "✓" else "" }

    val values = listOf(
      index.plus(1).toString(),
      row.title.getAccountTitle().plus(row.fullName.trim()),
      if (row.phoneNumber.isDigitsOnly()) row.phoneNumber else "",
    ) +
      months.map { tick(row.monthStatus[it] == true) }

    values.forEachIndexed { i, value ->
      canvas.drawText(value, x, currentY, paint)
      x += columnWidths[i]
    }

    currentY += lineHeight
  }

  document.finishPage(page)
  onEvent(document.saveToInternal(context))
  document.close()
}

private fun PdfDocument.saveToInternal(context: Context): File? {
  val pdfFile = File(context.filesDir, "filename.pdf")
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
