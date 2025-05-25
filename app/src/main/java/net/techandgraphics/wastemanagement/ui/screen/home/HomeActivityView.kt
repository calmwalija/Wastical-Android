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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.home.model.HomeActivityUiModel
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeActivityView(
  homeActivity: HomeActivityUiModel,
  modifier: Modifier = Modifier,
  onEvent: (HomeEvent) -> Unit
) {

  val brush = Brush.horizontalGradient(
    listOf(
      homeActivity.iconBackground.copy(.7f),
      homeActivity.iconBackground.copy(.8f),
      homeActivity.iconBackground
    )
  )

  Surface(
    tonalElevation = 3.dp,
    shape = RoundedCornerShape(8),
    color = homeActivity.containerColor,
    modifier = modifier.padding(4.dp),
    onClick = { onEvent(homeActivity.event) },
    enabled = homeActivity.clickable
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Icon(
        painterResource(homeActivity.drawableRes), null,
        modifier = Modifier
          .padding(bottom = 8.dp)
          .clip(CircleShape)
          .background(brush = brush)
          .size(42.dp)
          .padding(8.dp),
        tint = homeActivity.iconTint
      )
      Text(
        text = homeActivity.activity,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )

      Text(
        text = homeActivity.epochSecond.toZonedDateTime().defaultDate(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )

    }
  }

}

@Preview(showBackground = true)
@Composable
private fun HomeActivityViewPreview() {
  WasteManagementTheme {
    HomeActivityView(
      homeActivity = homeActivityUiModels.first(),
      onEvent = {}
    )
  }
}
