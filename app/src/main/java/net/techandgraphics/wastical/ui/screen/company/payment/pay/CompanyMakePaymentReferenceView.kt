package net.techandgraphics.wastical.ui.screen.company.payment.pay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyMakePaymentReferenceView(
  state: CompanyMakePaymentState.Success,
  onEvent: (CompanyMakePaymentEvent) -> Unit,
) {


  Column {
    Text(
      text = "Payment Reference",
      modifier = Modifier.padding(8.dp)
    )
    Card(colors = CardDefaults.elevatedCardColors()) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth()
      ) {
        if (state.screenshotAttached) ScreenshotSuccess() else {
          Icon(
            painterResource(R.drawable.ic_add_photo), null,
            modifier = Modifier.size(32.dp)
          )
        }
        Text(
          modifier = Modifier.padding(4.dp),
          text = if (state.screenshotAttached) "Payment Screenshot Attached" else "Attach Payment Screenshot",
          color = if (state.screenshotAttached) MaterialTheme.colorScheme.primary else {
            CardDefaults.elevatedCardColors().contentColor
          }
        )
      }
    }
  }
}

@Composable fun ScreenshotSuccess() {
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
        .size(30.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.2f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(32.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(24.dp)
        .background(
          brush = brush
        )
    )
    Icon(Icons.Default.Check, null, tint = Color.White)
  }

}

@Preview(showBackground = true)
@PreviewLightDark
@Composable
private fun CompanyMakePaymentReferenceViewPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      CompanyMakePaymentReferenceView(
        state = companySuccessState(LocalContext.current),
        onEvent = {}
      )
    }
  }
}
