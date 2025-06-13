package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.Green
import net.techandgraphics.wastemanagement.ui.theme.Orange
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


private val leftCompanyHomeItems = listOf(

  CompanyHomeItemModel(
    title = "Number of Clients",
    drawableRes = R.drawable.ic_supervisor_account,
    containerColor = Green,
    event = CompanyHomeEvent.Goto.Clients
  ),

  CompanyHomeItemModel(
    title = "Number of Streets",
    drawableRes = R.drawable.ic_house,
    containerColor = Orange,
    event = CompanyHomeEvent.Goto.Company
  ),

  )


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeSectionsView(
  onEvent: (CompanyHomeEvent) -> Unit,
) {
  FlowRow(
    maxItemsInEachRow = 2,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
  ) {
    leftCompanyHomeItems.forEach { item ->
      ElevatedCard(
        onClick = { onEvent(item.event) },
        modifier = Modifier
          .weight(1f)
          .padding(8.dp),
        shape = RoundedCornerShape(4),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.elevatedCardElevation(
          defaultElevation = 2.dp
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .padding(bottom = 4.dp)
              .clip(CircleShape)
              .background(item.containerColor.copy(.2f))
          ) {
            Icon(
              painterResource(item.drawableRes),
              contentDescription = null,
              modifier = Modifier.padding(8.dp),
              tint = item.containerColor
            )
          }
          Text(text = 100.toString())
          Text(
            text = item.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            color = item.containerColor
          )
        }
      }
    }
  }
}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeSectionsViewPreview() {
  WasteManagementTheme {
    CompanyHomeSectionsView(
      onEvent = {}
    )
  }
}
