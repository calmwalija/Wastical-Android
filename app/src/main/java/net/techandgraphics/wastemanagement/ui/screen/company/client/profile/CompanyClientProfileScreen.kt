package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toInitials
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientProfileScreen(
  state: CompanyClientProfileState,
  onEvent: (CompanyClientProfileEvent) -> Unit,
) {

  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(
            onClick = { },
          ) {
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
        text = "Client Profile",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
      )


      when (state) {
        CompanyClientProfileState.Loading -> Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }

        is CompanyClientProfileState.Success -> CompanyClientProfileSuccess(state, onEvent)
      }

    }
  }
}


@Composable private fun PaymentPlanView(state: CompanyClientProfileState.Success) {

  Column {

    state.paymentPlans.forEachIndexed { index, paymentPlan ->

      OutlinedCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        colors = if (paymentPlan.belongTo) CardDefaults.outlinedCardColors(
          containerColor = MaterialTheme.colorScheme.primary.copy(.1f)
        ) else {
          CardDefaults.elevatedCardColors()
        }
      ) {
        Row(
          modifier = Modifier
            .clickable { }
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {

          RadioButton(selected = paymentPlan.belongTo, onClick = {})

          Column(
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .weight(1f)
          ) {

            Text(
              text = "Payment Plan ${index.plus(1)}",
              style = MaterialTheme.typography.bodySmall,
              maxLines = 1,
              overflow = TextOverflow.MiddleEllipsis
            )

            Text(
              text = paymentPlan.name,
              style = MaterialTheme.typography.titleMedium
            )

          }

          Box(
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .background(MaterialTheme.colorScheme.secondary.copy(.1f))
              .fillMaxHeight(.05f)
              .width(1.dp)
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = paymentPlan.fee.toAmount(),
              color = MaterialTheme.colorScheme.primary
            )

            Text(
              text = paymentPlan.period.name,
              style = MaterialTheme.typography.bodySmall
            )
          }


        }

      }
    }

    Spacer(modifier = Modifier.height(24.dp))

  }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun CompanyClientProfileSuccess(
  state: CompanyClientProfileState.Success,
  onEvent: (CompanyClientProfileEvent) -> Unit,
) {

  var showPlans by remember { mutableStateOf(false) }

  val account = state.account


  if (showPlans) ModalBottomSheet(onDismissRequest = { showPlans = false }) {
    PaymentPlanView(state)
  }


  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp)
  ) {
    ProfileLetterView(account)

    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 8.dp)
    ) {
      Text(
        text = account.username,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
      Text(
        text = account.toFullName(),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
      )
      Text(
        text = account.createdAt.toZonedDateTime().defaultDateTime(),
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )

    }

    IconButton(onClick = {}) {
      Icon(Icons.Default.Phone, null)
    }

    Spacer(modifier = Modifier.width(8.dp))

  }


  LazyColumn {
    itemsIndexed(profileItems) { index, item ->
      Column(modifier = Modifier.clickable {
        when (item.event) {
          CompanyClientProfileEvent.Option.History -> Unit
          CompanyClientProfileEvent.Option.Payment -> onEvent(item.event)
          CompanyClientProfileEvent.Option.Plan -> showPlans = true
          else -> Unit
        }
      }) {
        Row(modifier = Modifier.padding(32.dp)) {
          Icon(painterResource(item.drawableRes), null)
          Text(
            text = item.title,
            modifier = Modifier
              .padding(start = 16.dp)
              .weight(1f)
          )
          Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
        }

        if (index.plus(1) < profileItems.size)
          HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
      }
    }
  }
}


@Composable private fun ProfileLetterView(account: AccountUiModel) {
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
        .size(78.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.2f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(84.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(72.dp)
        .background(
          brush = brush
        )
    )
    Text(
      text = account.toInitials(),
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
    )
  }

}

@Preview
@Composable
private fun CompanyClientProfileScreenPreview() {
  WasteManagementTheme {
    CompanyClientProfileScreen(
      state = CompanyClientProfileState.Success(account4Preview),
      onEvent = {}
    )
  }
}
