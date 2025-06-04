package net.techandgraphics.wastemanagement.ui.screen.company.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoScreen(
  state: CompanyInfoState,
  onEvent: (CompanyInfoEvent) -> Unit,
) {


  val account = account4Preview
  val company = company4Preview


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
          }
        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    },
  ) {

    Column(
      modifier = Modifier
        .padding(16.dp)
        .padding(it)
    ) {


      Text(
        text = "Company Info",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp, horizontal = 8.dp),
      )


      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        CompanyLetterView(company)

        Text(
          text = company.name,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
        )
        Text(
          text = company.slogan,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = company.address,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = company.email,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = company.slogan,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          style = MaterialTheme.typography.bodyMedium,
        )


        Card(modifier = Modifier.padding(vertical = 24.dp)) {

          LazyColumn {
            items(companyInfoItems) { item ->
              Column {
                Row(modifier = Modifier.padding(24.dp)) {
                  Icon(painterResource(item.drawableRes), null)
                  Text(
                    text = item.title,
                    modifier = Modifier
                      .padding(start = 16.dp)
                      .weight(1f)
                  )
                  Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
              }
            }
          }

        }


      }


    }
  }


}

@Composable private fun CompanyLetterView(company: CompanyUiModel) {
  val brush = Brush.horizontalGradient(
    listOf(
      MaterialTheme.colorScheme.primary.copy(.7f),
      MaterialTheme.colorScheme.primary.copy(.8f),
      MaterialTheme.colorScheme.primary
    )
  )

  Box(contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(98.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.2f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(104.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(92.dp)
        .background(
          brush = brush
        )
    )
    Text(
      text = company.name.first().toString(),
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
    )
  }

}

@Preview
@Composable
private fun CompanyInfoScreenPreview() {
  WasteManagementTheme {
    CompanyInfoScreen(
      state = CompanyInfoState(),
      onEvent = {}
    )
  }
}
