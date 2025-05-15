package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.screen.home.model.HomeActivityUiModel
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeActivityView(
  state: HomeState,
  homeActivityUiModel: HomeActivityUiModel,
  modifier: Modifier = Modifier,
  onEvent: (HomeEvent) -> Unit
) {

  val brush = Brush.horizontalGradient(
    listOf(
      homeActivityUiModel.iconBackground.copy(.7f),
      homeActivityUiModel.iconBackground.copy(.8f),
      homeActivityUiModel.iconBackground
    )
  )

  Surface(
    tonalElevation = 3.dp,
    shape = RoundedCornerShape(8),
    color = homeActivityUiModel.containerColor,
    modifier = modifier.padding(4.dp),
    onClick = {},
    enabled = homeActivityUiModel.clickable
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Icon(
        painterResource(homeActivityUiModel.drawableRes), null,
        modifier = Modifier
          .padding(bottom = 8.dp)
          .clip(CircleShape)
          .background(brush = brush)
          .size(42.dp)
          .padding(8.dp),
        tint = homeActivityUiModel.iconTint
      )
      Text(
        text = homeActivityUiModel.activity,
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = homeActivityUiModel.date.toString(),
        fontWeight = FontWeight.Bold,
      )

    }
  }

}

@Preview(showBackground = true)
@Composable
private fun HomeActivityViewPreview() {
  WasteManagementTheme {
    HomeActivityView(
      state = HomeState(),
      homeActivityUiModel = homeActivityUiModels.first(),
      onEvent = {}
    )
  }
}
