package net.techandgraphics.wastemanagement.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun DottedBorderBox(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = modifier
      .borderDotted(MaterialTheme.colorScheme.primary.copy(.4f), 3f)
      .padding(16.dp)
      .wrapContentSize(Alignment.Center)
  ) {
    content()
  }
}


fun Modifier.borderDotted(color: Color, dotRadius: Float): Modifier = this.then(
  Modifier.drawBehind {

    val width = size.width
    val height = size.height
    val step = 10f

    for (x in 0 until width.toInt() step step.toInt()) {
      drawCircle(
        color = color,
        radius = dotRadius,
        center = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f)
      )
    }

    for (x in 0 until width.toInt() step step.toInt()) {
      drawCircle(
        color = color,
        radius = dotRadius,
        center = androidx.compose.ui.geometry.Offset(x.toFloat(), height)
      )
    }

    for (y in 0 until height.toInt() step step.toInt()) {
      drawCircle(
        color = color,
        radius = dotRadius,
        center = androidx.compose.ui.geometry.Offset(0f, y.toFloat())
      )
    }

    for (y in 0 until height.toInt() step step.toInt()) {
      drawCircle(
        color = color,
        radius = dotRadius,
        center = androidx.compose.ui.geometry.Offset(width, y.toFloat())
      )
    }
  }
)

@Composable
@Preview
fun DottedBorderCardPreview() {
  DottedBorderBox {
    Text(
      text = "Dotted Border Card",
      modifier = Modifier.padding(16.dp)
    )
  }
}
