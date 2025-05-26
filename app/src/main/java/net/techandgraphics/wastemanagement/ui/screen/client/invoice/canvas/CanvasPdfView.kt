package net.techandgraphics.wastemanagement.ui.screen.client.invoice.canvas

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.bold
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.extraBold
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.light
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.mailMan
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.time.ZonedDateTime


@Composable
fun CanvasPdfView(modifier: Modifier = Modifier) {

  val context = LocalContext.current
  val density = LocalDensity.current.density

  val a4WidthDp = 8.27f * density * 160f
  val a4HeightDp = 11.69f * density * 23f

  val textSize72 = Paint().apply { textSize = 72f }
  val textSize42 = Paint().apply { textSize = 42f }
  val textSize32 = Paint().apply { textSize = 32f }

  Canvas(
    modifier = modifier
      .height(a4HeightDp.dp)
      .width(a4WidthDp.dp)
      .background(Color.White)
  ) {

    var theYAxis = 160f
    var theXAxis = 90f

    var holdVerticalAxis = 0f
    var holdHorizontalAxis = 0f


    drawIntoCanvas { canvas ->
      canvas.save()
      canvas.rotate(-65f)
      canvas.nativeCanvas.drawText(
        "paid".uppercase(),
        -1350f,
        1200f,
        Paint().also {
          it.typeface = extraBold(context)
          it.color = "#EFEFEF".toColorInt()
          it.textSize = 520f
        }
      )
      canvas.restore()
    }


    /***************************************************************/
    canvasHeadingView(
      theHeading = "Invoice".uppercase(),
      theXAxis = theXAxis,
      theYAxis = theYAxis,
      paint = textSize72.also {
        it.typeface = bold(context); it.color = android.graphics.Color.GRAY
      },
    ).run { theYAxis = yAxis }
    /***************************************************************/


    /***************************************************************/
    canvasSentence(
      theSentence = "Clear Sight Cleaning Services",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    canvasSentence(
      theSentence = "P.O. Box 40286",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    canvasSentence(
      theSentence = "Kanengo,",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )

    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))


    /***************************************************************/
    canvasSentence(
      theSentence = "Lilongwe 4",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))


    /***************************************************************/
    canvasSentence(
      theSentence = "Phone : +265-992-882-020",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    canvasSentence(
      theSentence = "Email : clearsightinvestiments@gmail.com",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    theYAxis = theYAxis.plus(textSize72.textSize.times(1.7f))

    holdVerticalAxis = theYAxis

    /***************************************************************/
    canvasHeadingView(
      theHeading = "Invoice #",
      theXAxis = theXAxis,
      theYAxis = theYAxis,
      paint = textSize42.also {
        it.typeface = bold(context); it.color = android.graphics.Color.GRAY
      },
    ).run { theYAxis = yAxis.plus(10) }
    /***************************************************************/

    canvasSentence(
      theSentence = System.currentTimeMillis().toString(),
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )

    theYAxis = theYAxis.plus(textSize72.textSize.plus(30))


    /***************************************************************/
    canvasHeadingView(
      theHeading = "Date & Time",
      theXAxis = theXAxis.times(6),
      theYAxis = holdVerticalAxis,
      paint = textSize42.also {
        it.typeface = bold(context); it.color = android.graphics.Color.GRAY
      },
    ).run { theYAxis = yAxis.plus(10) }
    /***************************************************************/

    canvasSentence(
      theSentence = ZonedDateTime.now().defaultDateTime(),
      theYAxis = theYAxis,
      theXAxis = theXAxis.times(6),
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    theYAxis = theYAxis.plus(textSize72.textSize.times(1.7f))

    /***************************************************************/
    canvasHeadingView(
      theHeading = "Received From",
      theXAxis = theXAxis,
      theYAxis = theYAxis,
      paint = textSize42.also {
        it.typeface = bold(context); it.color = android.graphics.Color.GRAY
      },
    ).run { theYAxis = yAxis.plus(10) }
    /***************************************************************/


    /***************************************************************/
    canvasSentence(
      theSentence = "Dr. James Mike Jr",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))


    /***************************************************************/
    canvasSentence(
      theSentence = "Phone : +265-992-882-020",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.times(1.7f))

    /***************************************************************/
    canvasHeadingView(
      theHeading = "Payment Info",
      theXAxis = theXAxis,
      theYAxis = theYAxis,
      paint = textSize42.also {
        it.typeface = bold(context); it.color = android.graphics.Color.GRAY
      },
    ).run { theYAxis = yAxis.plus(10) }
    /***************************************************************/


    /***************************************************************/
    canvasSentence(
      theSentence = "National Bank",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    canvasSentence(
      theSentence = "Account # : 100490012",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.minus(20))

    /***************************************************************/
    canvasSentence(
      theSentence = "Trans Id Ref : ${System.currentTimeMillis()}",
      theYAxis = theYAxis,
      theXAxis = theXAxis,
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.minus(10))

    holdHorizontalAxis = theXAxis.times(2)


    val tableData = listOf(
      listOf("#", "Description", "Qty", "Rate", "Amount"),
      listOf("1", "Row 1, Col 1", "1", "K10,000", "K20,000"),
    )

    val spacer = listOf(30, 250, 70, 150, 150)

    var horizontalSpacing = holdHorizontalAxis
    theYAxis = theYAxis.plus(textSize72.textSize.plus(40))

    tableData.forEachIndexed { index, theData ->
      horizontalSpacing = holdHorizontalAxis
      theData.forEachIndexed { i, str ->
        canvasSentence(
          theSentence = str,
          theYAxis = theYAxis,
          theXAxis = horizontalSpacing,
          paint = textSize32.also {
            it.typeface = if (index == 0) bold(context) else light(context)
          },
        )
        horizontalSpacing = horizontalSpacing.plus(textSize32.textSize.plus(spacer[i]))

      }
      theYAxis = theYAxis.plus(textSize72.textSize.plus(20))

    }

    holdHorizontalAxis = horizontalSpacing.plus(textSize32.textSize.plus(spacer[3]))
    theYAxis = theYAxis.plus(textSize72.textSize.minus(90))


    /***************************************************************/
    canvasSentence(
      theSentence = "Subtotal",
      theYAxis = theYAxis,
      theXAxis = theXAxis.times(7),
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    /***************************************************************/
    canvasSentence(
      theSentence = "K30,000",
      theYAxis = theYAxis,
      theXAxis = theXAxis.times(9),
      paint = textSize32.also { it.typeface = light(context) },
    )
    /***************************************************************/

    theYAxis = theYAxis.plus(textSize72.textSize)


    /***************************************************************/
    canvasSentence(
      theSentence = "Total".uppercase(),
      theYAxis = theYAxis,
      theXAxis = theXAxis.times(7),
      paint = textSize32.also { it.typeface = bold(context) },
    )
    /***************************************************************/


    /***************************************************************/
    canvasSentence(
      theSentence = "K30,000",
      theYAxis = theYAxis,
      theXAxis = theXAxis.times(9),
      paint = textSize32.also { it.typeface = bold(context) },
    )
    /***************************************************************/


    theYAxis = theYAxis.plus(textSize72.textSize.plus(120))


    /***************************************************************/
    canvasSentence(
      theSentence = "With Thanks",
      theYAxis = theYAxis,
      theXAxis = theXAxis.times(6),
      paint = Paint().also { it.typeface = mailMan(context); it.textSize = 120f },
    )
    /***************************************************************/


    /***************************************************************/


    /***************************************************************/


  }


}

@Composable
@Preview(showBackground = true)
fun InvoicePdfViewPreview() {
  WasteManagementTheme {
    CanvasPdfView()
  }
}
