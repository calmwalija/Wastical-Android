package net.techandgraphics.wastical.ui.screen.company.home

import android.graphics.Paint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toShortMonthName
import net.techandgraphics.wastical.ui.HorizontalRuleView


enum class ChartStyle { Bar, Line }


@Composable fun CompanyHomeMonthlyPaymentChart(
  data: List<MonthYearPayment4Month>,
  showYAxis: Boolean = false,
  showBarLabels: Boolean = false,
  style: ChartStyle = ChartStyle.Bar,
  onStyleChange: ((ChartStyle) -> Unit)? = null,
) {
  val maxValue =
    (data.maxOfOrNull { it.payment4CurrentMonth.totalPaidAmount } ?: 0).coerceAtLeast(1)
  val barBrush = Brush.verticalGradient(
    listOf(
      MaterialTheme.colorScheme.primary.copy(alpha = .6f),
      MaterialTheme.colorScheme.primary
    )
  )

  Card(colors = CardDefaults.elevatedCardColors()) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Monthly collections",
            style = MaterialTheme.typography.titleMedium,
          )
          Text(
            text = "Tap a bar to see amount",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        SegmentedStyleSwitcher(style = style, onStyleChange = onStyleChange)
      }

      Spacer(modifier = Modifier.height(16.dp))

      var tooltipIndex by remember { mutableIntStateOf(-1) }
      val tooltipOverlayColor = MaterialTheme.colorScheme.secondary.copy(alpha = .2f)
      val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .25f)
      val primaryColorDraw = Color(MaterialTheme.colorScheme.primary.toArgb())
      val lineAreaBrush = Brush.verticalGradient(
        0.0f to MaterialTheme.colorScheme.primary.copy(alpha = .2f),
        1.0f to Color.Transparent,
      )
      val scrollState = rememberScrollState()
      val barWidthDp = 52.dp
      val gapWidthDp = 32.dp
      val slotWidthDp = barWidthDp + gapWidthDp
      val contentWidthDp = slotWidthDp * data.size
      var playAnim by remember(style, data.size) { mutableStateOf(false) }
      val progress by animateFloatAsState(
        targetValue = if (playAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "chartProgress"
      )
      LaunchedEffect(style, data.size) { playAnim = true }

      val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()
      val surfaceArgb = MaterialTheme.colorScheme.surface.copy(alpha = .95f).toArgb()

      Row(modifier = Modifier.horizontalScroll(scrollState)) {
        Box(
          modifier = Modifier
            .width(contentWidthDp)
            .height(180.dp)
        ) {
          Canvas(
            modifier = Modifier
              .matchParentSize()
              .pointerInput(data) {
                detectTapGestures { offset ->
                  val barWidth = barWidthDp.toPx()
                  val gap = gapWidthDp.toPx()
                  val slot = barWidth + gap
                  val index = (offset.x / slot).toInt()
                  tooltipIndex = if (index in data.indices) index else -1
                }
              }
          ) {
            val barWidth = barWidthDp.toPx()
            val gap = gapWidthDp.toPx()
            val gridLevels = if (showYAxis) listOf(0.25f, 0.5f, 0.75f) else emptyList()
            gridLevels.forEach { level ->
              val y = size.height * (1f - level)
              drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(size.width, y),
                strokeWidth = 1f
              )
            }

            var x = 0f
            if (style == ChartStyle.Bar) {
              data.forEachIndexed { i, entry ->
                val value = entry.payment4CurrentMonth.totalPaidAmount
                val ratio = value.toFloat() / maxValue.toFloat()
                val barHeight = size.height * (ratio * progress)
                val left = x + (gap / 2f)
                drawRoundRect(
                  brush = barBrush,
                  topLeft = androidx.compose.ui.geometry.Offset(left, size.height - barHeight),
                  size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                  cornerRadius = CornerRadius(12f, 12f)
                )
                if (i == tooltipIndex) {
                  drawRoundRect(
                    color = tooltipOverlayColor,
                    topLeft = androidx.compose.ui.geometry.Offset(
                      left - 4f,
                      size.height - barHeight - 6f
                    ),
                    size = androidx.compose.ui.geometry.Size(barWidth + 8f, barHeight + 12f),
                    cornerRadius = CornerRadius(12f, 12f)
                  )

                  val year = entry.monthYear.year.toString().takeLast(2)
                  val label =
                    "${entry.monthYear.month.toShortMonthName()} $year  ${value.toAmount()}"
                  val padding = 8.dp.toPx()
                  val textSizePx = 28f
                  val paint = Paint().apply {
                    isAntiAlias = true
                    color = onSurfaceArgb
                    textSize = textSizePx
                  }
                  val textWidth = paint.measureText(label)
                  val bubbleWidth = textWidth + (padding * 2)
                  val bubbleHeight = textSizePx + (padding * 2)
                  val centerX = left + (barWidth / 2f)
                  var bubbleX = centerX - (bubbleWidth / 2f)
                  bubbleX = bubbleX.coerceIn(0f, size.width - bubbleWidth)
                  var bubbleY = (size.height - barHeight) - bubbleHeight - 8.dp.toPx()
                  if (bubbleY < 0f) bubbleY = 0f
                  drawRoundRect(
                    color = Color(surfaceArgb),
                    topLeft = androidx.compose.ui.geometry.Offset(bubbleX, bubbleY),
                    size = androidx.compose.ui.geometry.Size(bubbleWidth, bubbleHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                  )
                  val textX = bubbleX + padding
                  val textY = bubbleY + padding + textSizePx * .78f
                  drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                      label,
                      textX,
                      textY,
                      paint
                    )
                  }
                }
                x += (barWidth + gap)
              }
            } else {
              val points = mutableListOf<androidx.compose.ui.geometry.Offset>()
              x = 0f
              data.forEach { entry ->
                val value = entry.payment4CurrentMonth.totalPaidAmount
                val ratio = value.toFloat() / maxValue.toFloat()
                val y = size.height * (1f - (ratio * progress))
                val cx = x + (gap / 2f) + (barWidth / 2f)
                points += androidx.compose.ui.geometry.Offset(cx, y)
                x += (barWidth + gap)
              }

              if (points.size >= 2) {
                val areaPath = Path().apply {
                  moveTo(points.first().x, size.height)
                  points.forEach { p -> lineTo(p.x, p.y) }
                  lineTo(points.last().x, size.height)
                  close()
                }
                drawPath(path = areaPath, brush = lineAreaBrush)
              }

              var prev = points.firstOrNull()
              points.forEachIndexed { i, p ->
                drawCircle(color = primaryColorDraw, radius = 6f, center = p)
                prev?.let { pr ->
                  if (i > 0) drawLine(
                    color = primaryColorDraw,
                    start = pr,
                    end = p,
                    strokeWidth = 4f
                  )
                }
                prev = p
              }

              val idx = tooltipIndex
              if (idx in data.indices && points.isNotEmpty()) {
                val entry = data[idx]
                val p = points.getOrNull(idx)
                if (p != null) {
                  val value = entry.payment4CurrentMonth.totalPaidAmount
                  val year = entry.monthYear.year.toString().takeLast(2)
                  val label =
                    "${entry.monthYear.month.toShortMonthName()} $year  ${value.toAmount()}"
                  val padding = 8.dp.toPx()
                  val textSizePx = 28f
                  val paint = Paint().apply {
                    isAntiAlias = true
                    color = onSurfaceArgb
                    textSize = textSizePx
                  }
                  val textWidth = paint.measureText(label)
                  val bubbleWidth = textWidth + (padding * 2)
                  val bubbleHeight = textSizePx + (padding * 2)
                  var bubbleX = p.x - (bubbleWidth / 2f)
                  bubbleX = bubbleX.coerceIn(0f, size.width - bubbleWidth)
                  var bubbleY = p.y - bubbleHeight - 8.dp.toPx()
                  if (bubbleY < 0f) bubbleY = 0f
                  drawRoundRect(
                    color = Color(surfaceArgb),
                    topLeft = androidx.compose.ui.geometry.Offset(bubbleX, bubbleY),
                    size = androidx.compose.ui.geometry.Size(bubbleWidth, bubbleHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                  )
                  val textX = bubbleX + padding
                  val textY = bubbleY + padding + textSizePx * .78f
                  drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                      label,
                      textX,
                      textY,
                      paint
                    )
                  }
                }
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier
          .horizontalScroll(scrollState)
          .width(contentWidthDp)
      ) {
        data.forEach { entry ->
          val year = entry.monthYear.year.toString().takeLast(2)
          val label = "${entry.monthYear.month.toShortMonthName()} $year"
          Box(modifier = Modifier.width(slotWidthDp), contentAlignment = Alignment.Center) {
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      if (data.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalRuleView({})
        val active = data.getOrNull(tooltipIndex) ?: data.first()
        val year = active.monthYear.year.toString()
        val label = "${active.monthYear.month.toShortMonthName()} $year"
        val selText = active.payment4CurrentMonth.totalPaidAmount.toAmount()

        Spacer(modifier = Modifier.height(8.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Box(
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .size(4.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.onSurfaceVariant)
          )
          Text(
            text = selText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

      }
    }
  }
}


@Composable private fun SegmentedStyleSwitcher(
  style: ChartStyle,
  onStyleChange: ((ChartStyle) -> Unit)?,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    val onSurface = MaterialTheme.colorScheme.primary.copy(.2f)
    val selected = MaterialTheme.colorScheme.onSecondary.copy(.2f)
    Row(modifier = Modifier) {
      IconButton(
        onClick = { onStyleChange?.invoke(ChartStyle.Bar) },
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = if (style == ChartStyle.Line) selected else onSurface,
        )
      ) {
        Icon(
          painterResource(R.drawable.ic_bar_chart),
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
      }
      IconButton(
        onClick = { onStyleChange?.invoke(ChartStyle.Line) },
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = if (style == ChartStyle.Bar) selected else onSurface,
        )
      ) {
        Icon(
          painterResource(R.drawable.ic_trend),
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
      }
    }

  }
}
