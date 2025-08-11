package net.techandgraphics.wastical.ui.screen.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toInitials
import net.techandgraphics.wastical.toLocation
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@Composable fun AccountInfoView(
  account: AccountUiModel,
  demographic: CompanyLocationWithDemographicUiModel,
  onEvent: (AccountInfoEvent) -> Unit,
) {

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
  ) {

    Box(contentAlignment = Alignment.BottomEnd) {
      ProfileLetterView(account)
      if (account.username.trim().isNotEmpty() && account.username.isDigitsOnly())
        Card(
          shape = CircleShape,
          modifier = Modifier
            .offset(y = -(2).dp)
            .size(48.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color.White
          ),
          elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp
          ),
          onClick = { onEvent(AccountInfoEvent.Phone(account.username)) }
        ) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .size(32.dp)
                .padding(4.dp)
            )
          }
        }

    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = account.username,
      style = MaterialTheme.typography.bodyLarge,
    )
    Text(
      text = account.toFullName(),
      style = MaterialTheme.typography.titleLarge,
      overflow = TextOverflow.MiddleEllipsis,
      modifier = Modifier.padding(vertical = 4.dp)
    )

    TextButton(
      modifier = Modifier.fillMaxWidth(.7f),
      colors = ButtonDefaults.elevatedButtonColors(),
      onClick = { onEvent(AccountInfoEvent.Location(demographic.demographicStreet.id)) }
    ) {
      Text(text = demographic.toLocation())
    }

  }
}


@Composable private fun ProfileLetterView(account: AccountUiModel) {

  val brush = Brush.horizontalGradient(
    listOf(
      MaterialTheme.colorScheme.primary.copy(.4f),
      MaterialTheme.colorScheme.primary.copy(.6f),
      MaterialTheme.colorScheme.primary
    )
  )

  Box(contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(160.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(140.dp)
        .background(brush = brush)
    )
    Text(
      text = account.toInitials(),
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.headlineSmall
    )
  }

}


@Preview(showBackground = true) @Composable fun AccountInfoViewPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      AccountInfoView(
        account = account4Preview,
        demographic = companyLocationWithDemographic4Preview,
        onEvent = {})
    }
  }
}
