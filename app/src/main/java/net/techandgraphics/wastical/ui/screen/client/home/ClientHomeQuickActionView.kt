package net.techandgraphics.wastical.ui.screen.client.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientHomeQuickActionView(
  homeQuickActionUiModel: ClientHomeActivityItemModel,
  modifier: Modifier = Modifier,
  onEvent: () -> Unit,
) {
  val contentColor = MaterialTheme.colorScheme.onSecondaryContainer
  OutlinedCard(
    modifier = modifier.padding(4.dp),
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent() },
  ) {
    Column(
      modifier = Modifier
        .height(90.dp)
        .padding(8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        painterResource(homeQuickActionUiModel.drawableRes),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(32.dp)
      )
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        BasicText(
          text = homeQuickActionUiModel.activity,
          style = MaterialTheme.typography.bodyLarge,
          color = ColorProducer { contentColor },
          modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
          maxLines = 1,
          autoSize = TextAutoSize.StepBased(
            maxFontSize = MaterialTheme.typography.titleMedium.fontSize
          )
        )
      }
    }
  }

}

@Preview(showBackground = true)
@Composable
private fun ClientHomeQuickActionViewPreview() {
  WasticalTheme {
    ClientHomeQuickActionView(
      onEvent = {},
      homeQuickActionUiModel = homeQuickActionUiModels.first()
    )
  }
}
