package net.techandgraphics.wastemanagement.ui.screen.client.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.screen.client.home.model.HomeQuickActionUiModel
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeQuickActionView(
  homeQuickActionUiModel: HomeQuickActionUiModel,
  modifier: Modifier = Modifier,
  onEvent: (HomeEvent) -> Unit
) {

  OutlinedCard(
    modifier = modifier.padding(4.dp),
    onClick = { onEvent(homeQuickActionUiModel.event) },
  ) {
    Column(
      modifier = Modifier
        .height(90.dp)
        .padding(8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        painterResource(homeQuickActionUiModel.drawableRes), null,
        tint = MaterialTheme.colorScheme.primary
      )
      Text(
        text = homeQuickActionUiModel.title,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }

}

@Preview(showBackground = true)
@Composable
private fun HomeQuickActionViewPreview() {
  WasteManagementTheme {

    HomeQuickActionView(
      onEvent = {},
      homeQuickActionUiModel = homeQuickActionUiModels.first()
    )
  }
}
