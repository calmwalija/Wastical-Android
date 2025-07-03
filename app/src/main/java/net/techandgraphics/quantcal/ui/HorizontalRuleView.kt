package net.techandgraphics.quantcal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable fun HorizontalRuleView(content: @Composable () -> Unit, modifier: Modifier = Modifier) {

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .background(
          brush = Brush.horizontalGradient(
            listOf(
              Color.Transparent,
              MaterialTheme.colorScheme.secondary,
              MaterialTheme.colorScheme.secondary,
            )
          )
        )
        .height(.5.dp)
        .weight(1f)
    )
    content()
    Box(
      modifier = Modifier
        .background(
          brush = Brush.horizontalGradient(
            listOf(
              MaterialTheme.colorScheme.secondary,
              MaterialTheme.colorScheme.secondary,
              Color.Transparent,
            )
          )
        )
        .height(.5.dp)
        .weight(1f)
    )
  }
}
