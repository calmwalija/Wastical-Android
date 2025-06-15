package net.techandgraphics.wastemanagement.ui.screen.company

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel

@Composable
fun CompanyInfoTopAppBarView(company: CompanyUiModel) {
  Row(
    modifier = Modifier
      .padding(end = 16.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      painterResource(R.drawable.im_placeholder),
      contentDescription = null,
      modifier = Modifier
        .padding(end = 8.dp)
        .clip(CircleShape)
        .size(32.dp)
    )
    Text(
      text = company.name,
      maxLines = 1,
      style = MaterialTheme.typography.titleMedium,
      overflow = TextOverflow.Ellipsis,
    )
  }
}
