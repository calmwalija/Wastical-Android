package net.techandgraphics.wastical.ui.screen.company.report

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation
import androidx.core.text.isDigitsOnly
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.getAccountTitle
import net.techandgraphics.wastical.toShortMonthName
import net.techandgraphics.wastical.ui.screen.client.invoice.bold
import net.techandgraphics.wastical.ui.screen.client.invoice.light
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BaseExportKlass<T>(context: Context) : ContextWrapper(context) {

  companion object {
    const val PDF_TEXT_SIZE = 8f
  }

  private val pdfDocument = PdfDocument()
  private val cellPadding = 5f

  private val bodyPaint = Paint().apply {
    color = Color.BLACK
    textSize = 10f
    typeface = light(this@BaseExportKlass)
  }

  private val cellPaint = Paint().apply {
    textSize = PDF_TEXT_SIZE
    typeface = light(this@BaseExportKlass)
    color = Color.BLACK
  }

  private val borderPaint = Paint().apply {
    color = "#CCCCCC".toColorInt()
    strokeWidth = 1f
    style = Paint.Style.STROKE
  }

  private val headerPaint = Paint().apply {
    color = Color.BLACK
    textSize = PDF_TEXT_SIZE
    typeface = bold(this@BaseExportKlass)
  }

  private val pageTitlePaint = Paint().apply {
    color = Color.BLACK
    textSize = PDF_TEXT_SIZE.plus(3f)
    typeface = bold(this@BaseExportKlass)
    textAlign = Paint.Align.CENTER
  }

  private val lineHeight = 18f
  private val margin = 32f

  fun toPdf(
    company: CompanyUiModel,
    columnHeaders: List<String>,
    columnWidths: List<Float>,
    filename: String,
    pageTitle: String,
    items: List<T>,
    valueExtractor: (T) -> List<String>,
    onEvent: (File?) -> Unit,
  ) {
    require(columnHeaders.size == columnWidths.size) {
      "Column headers and widths must have the same size"
    }
    val pageWidth = 600
    val pageHeight = 920

    val startYHeader = 64
    var currentY = startYHeader + lineHeight

    var pageIndex = 1

    fun calculateTotalPages(): Int {
      var pageCount = 1
      var currentY = startYHeader + lineHeight

      items.forEach { _ ->
        if (currentY > pageHeight - 60) {
          pageCount++
          currentY = startYHeader + 20f // Reset Y for new page (same as your logic)
        }
        currentY += lineHeight
      }
      return pageCount
    }

    val totalPages = calculateTotalPages()

    var canvas: Canvas
    var page =
      pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create())
    canvas = page.canvas

    drawWatermark(canvas, pageWidth, pageHeight, company.name)

    canvas.drawText(pageTitle, pageWidth / 2f, margin, pageTitlePaint)

    canvas.drawText("Page $pageIndex of $totalPages", pageWidth / 2f, pageHeight - 30f, cellPaint)

    fun drawHeader(canvas: Canvas) {
      val headerHeight = lineHeight * 1.2f

      val headerBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
      }

      canvas.drawRect(
        margin,
        startYHeader - lineHeight / 2,
        margin + columnWidths.sum(),
        startYHeader + headerHeight / 2,
        headerBgPaint,
      )

      canvas.drawLine(
        margin,
        startYHeader - lineHeight / 2,
        margin + columnWidths.sum(),
        startYHeader - lineHeight / 2,
        borderPaint,
      )

      var currentX = margin
      columnWidths.forEach { width ->
        canvas.drawLine(
          currentX,
          startYHeader - lineHeight / 2,
          currentX,
          startYHeader + headerHeight / 2,
          borderPaint,
        )
        currentX += width
      }
      canvas.drawLine(
        currentX,
        startYHeader - lineHeight / 2,
        currentX,
        startYHeader + headerHeight / 2,
        borderPaint,
      )

      var textX = margin + cellPadding
      columnHeaders.forEachIndexed { i, header ->
        canvas.drawText(header, textX, startYHeader + cellPadding, headerPaint)
        textX += columnWidths[i]
      }

      canvas.drawLine(
        margin,
        startYHeader + headerHeight / 2,
        margin + columnWidths.sum(),
        startYHeader + headerHeight / 2,
        borderPaint,
      )
    }

    drawHeader(canvas)

    items.forEachIndexed { index, item ->
      if (currentY > pageHeight - 60) {
        pdfDocument.finishPage(page)
        pageIndex++
        page = pdfDocument.startPage(
          PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create(),
        )
        canvas = page.canvas
        drawWatermark(canvas, pageWidth, pageHeight, company.name)
        currentY = startYHeader + 20f

        canvas.drawText(
          "Page $pageIndex of $totalPages",
          pageWidth / 2f,
          pageHeight - 30f,
          cellPaint,
        )

        drawHeader(canvas)
      }

      val values = listOf((index + 1).toString()) + valueExtractor(item)

      var currentX = margin
      columnWidths.forEach { width ->
        canvas.drawLine(
          currentX,
          currentY - lineHeight / 2,
          currentX,
          currentY + lineHeight / 2,
          borderPaint,
        )
        currentX += width
      }
      canvas.drawLine(
        currentX,
        currentY - lineHeight / 2,
        currentX,
        currentY + lineHeight / 2,
        borderPaint,
      )

      var textX = margin + cellPadding
      values.forEachIndexed { i, value ->
        canvas.drawText(value, textX, currentY + cellPadding, cellPaint)
        textX += columnWidths[i]
      }

      canvas.drawLine(
        margin,
        currentY + lineHeight / 2,
        margin + columnWidths.sum(),
        currentY + lineHeight / 2,
        borderPaint,
      )

      currentY += lineHeight
    }

    canvas.drawRect(
      margin,
      startYHeader - lineHeight / 2,
      margin + columnWidths.sum(),
      currentY - lineHeight / 2,
      borderPaint,
    )

    pdfDocument.finishPage(page)
    onEvent(pdfDocument.saveToInternal(filename))
    pdfDocument.close()
  }

  fun toCoveragePdf(
    company: CompanyUiModel,
    pdfHeaders: List<String>,
    pdfWidths: List<Float>,
    filename: String,
    items: List<PaymentCoverageRow>,
    onEvent: (File?) -> Unit,
    months: List<Int>,
  ) {
    val columnHeaders = pdfHeaders + months.map { it.toShortMonthName() }
    val columnWidths = pdfWidths + List(months.size) { 60f }

    require(columnHeaders.size == columnWidths.size) {
      "Column headers and widths must have the same size"
    }

    val document = PdfDocument()

    val pageWidth = if (columnWidths.sumOf { it.toInt() }.plus(80) < 595) 595 else 842
    val pageHeight = if (pageWidth == 595) 842 else 595

    val headerHeight = 35f
    val cellPadding = 5f

    val totalTableWidth = columnWidths.sum()

    var currentY = margin + 40f
    var pageNumber = 1

    var page =
      document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
    var canvas = page.canvas

    drawWatermark(canvas, pageWidth, pageHeight, company.name)

    fun drawHeaders() {
      canvas.drawRect(
        margin,
        currentY - headerHeight / 2,
        margin + totalTableWidth,
        currentY + headerHeight / 2,
        headerPaint,
      )

      var currentX = margin
      columnWidths.forEach { width ->
        canvas.drawLine(
          currentX,
          currentY - headerHeight / 2,
          currentX,
          currentY + headerHeight / 2,
          borderPaint,
        )
        currentX += width
      }
      canvas.drawLine(
        currentX,
        currentY - headerHeight / 2,
        currentX,
        currentY + headerHeight / 2,
        borderPaint,
      )

      var textX = margin + cellPadding
      columnHeaders.forEachIndexed { i, header ->
        canvas.drawText(header, textX, currentY + cellPadding, bodyPaint)
        textX += columnWidths[i]
      }

      canvas.drawLine(
        margin,
        currentY + headerHeight / 2,
        margin + totalTableWidth,
        currentY + headerHeight / 2,
        borderPaint,
      )

      currentY += headerHeight
    }

    drawHeaders()

    items.forEachIndexed { index, row ->
      if (currentY + lineHeight > pageHeight - margin) {
        document.finishPage(page)
        pageNumber++
        page =
          document.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create(),
          )
        canvas = page.canvas

        drawWatermark(canvas, pageWidth, pageHeight, company.name)

        currentY = margin + 40f
        currentY += 40f
        drawHeaders()
      }

      val tick = { paid: Boolean -> if (paid) "✓" else "" }
      val values = listOf(
        (index + 1).toString(),
        row.title.getAccountTitle().plus(row.fullName.trim()),
        if (row.phoneNumber.isDigitsOnly()) row.phoneNumber else "",
      ) + months.map { tick(row.monthStatus[it] == true) }

      var currentX = margin
      columnWidths.forEach { width ->
        canvas.drawLine(
          currentX,
          currentY - lineHeight / 2,
          currentX,
          currentY + lineHeight / 2,
          borderPaint,
        )
        currentX += width
      }
      canvas.drawLine(
        currentX,
        currentY - lineHeight / 2,
        currentX,
        currentY + lineHeight / 2,
        borderPaint,
      )

      var textX = margin + cellPadding
      values.forEachIndexed { i, value ->
        canvas.drawText(value, textX, currentY + cellPadding, cellPaint)
        textX += columnWidths[i]
      }

      canvas.drawLine(
        margin,
        currentY + lineHeight / 2,
        margin + totalTableWidth,
        currentY + lineHeight / 2,
        borderPaint,
      )

      currentY += lineHeight
    }

    canvas.drawRect(
      margin,
      margin + 80f - headerHeight / 2,
      margin + totalTableWidth,
      currentY - lineHeight / 2,
      borderPaint,
    )

    document.finishPage(page)
    onEvent(document.saveToInternal(filename))
    document.close()
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

  private fun Context.drawWatermark(canvas: Canvas, pageWidth: Int, pageHeight: Int, text: String) {
    val watermarkPaint = Paint().apply {
      color = "#97808080".toColorInt()
      textSize = 32f
      typeface = bold(this@drawWatermark)
      textAlign = Paint.Align.CENTER
      alpha = 40
    }

    canvas.withRotation(-45f, pageWidth / 2f, pageHeight / 2f) {
      drawText(
        text,
        pageWidth / 2f,
        pageHeight / 2f,
        watermarkPaint,
      )
    }
  }
}
