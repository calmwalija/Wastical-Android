package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.capitalize
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.theme.Green
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomePaymentMonthlyView(
  state: CompanyHomeState.Success,
  onEvent: (CompanyHomeEvent) -> Unit,
) {

  var targetValue by remember { mutableFloatStateOf(0f) }
  val monthName = Month.of(state.monthYear.month).name.capitalize().plus(" ${state.monthYear.year}")
  var showMenuItems by remember { mutableStateOf(false) }

  val animateAsFloat by animateFloatAsState(
    targetValue = targetValue,
    animationSpec = tween(durationMillis = 7_000)
  )

  LaunchedEffect(state.payment4CurrentMonth.totalPaidAccounts) {
    showMenuItems = false
    targetValue = state.payment4CurrentMonth.totalPaidAccounts
      .toFloat()
      .div(state.accountsSize)
      .coerceIn(0f, 1f)
  }


  Card(
    modifier = Modifier
      .padding(8.dp)
      .fillMaxWidth(),
    colors = CardDefaults.elevatedCardColors()
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = monthName,
            style = MaterialTheme.typography.titleSmall,
          )
          AnimatedNumberCounter(
            theNumber = state.payment4CurrentMonth.totalPaidAmount,
            theStyle = MaterialTheme.typography.headlineMedium,
            theColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(onClick = { showMenuItems = true }) {
          Icon(
            Icons.Default.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
          DropdownMenu(
            expanded = showMenuItems,
            onDismissRequest = { showMenuItems = false }) {
            state.allMonthsPayments.forEach { month ->
              DropdownMenuItem(
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(onClick = {
                      onEvent(CompanyHomeEvent.Button.WorkingMonth(month.monthYear))
                    }, selected = month.monthYear == state.monthYear)
                    Text(
                      text = Month.of(month.monthYear.month)
                        .getDisplayName(
                          TextStyle.SHORT,
                          Locale.getDefault()
                        ).capitalize()
                        .plus(" ${month.monthYear.year}")
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                  }
                },
                enabled = month.monthYear != state.monthYear,
                colors = MenuDefaults.itemColors(
                  disabledTextColor = MaterialTheme.colorScheme.primary
                ),
                onClick = { onEvent(CompanyHomeEvent.Button.WorkingMonth(month.monthYear)) }
              )
            }
          }
        }
      }


      LinearProgressIndicator(
        progress = { animateAsFloat },
        modifier = Modifier
          .padding(vertical = 8.dp)
          .fillMaxWidth()
          .height(8.dp)
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${(animateAsFloat * 100).toInt()}% of expected",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.End)
      )

      Spacer(modifier = Modifier.height(20.dp))

      Column {
        CompanyHomePaymentMonthlyItem(
          label = "This Month",
          value = state.currentMonthCollected,
          drawableRes = R.drawable.ic_check_circle
        )
        Spacer(modifier = Modifier.height(12.dp))
        CompanyHomePaymentMonthlyItem(
          label = "Upfront",
          value = state.payment4CurrentMonth.totalPaidAmount,
          drawableRes = R.drawable.ic_history
        )

        Spacer(modifier = Modifier.height(12.dp))

        CompanyHomePaymentMonthlyItem(
          label = "Clients Paid",
          value = state.payment4CurrentMonth.totalPaidAccounts.toLong(),
          drawableRes = R.drawable.ic_account
        )
        Spacer(modifier = Modifier.height(12.dp))
        CompanyHomePaymentMonthlyItem(
          label = "Expected",
          value = state.expectedAmountToCollect,
          drawableRes = R.drawable.ic_payment
        )
      }
    }
  }

}


@Composable private fun CompanyHomePaymentMonthlyItem(
  drawableRes: Int = R.drawable.ic_method,
  label: String,
  value: Any,
) {
  OutlinedCard {
    Row(
      modifier = Modifier
        .padding(12.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        painterResource(drawableRes),
        contentDescription = null,
        modifier = Modifier
          .padding(end = 8.dp)
          .size(24.dp),
      )
      Column(
        verticalArrangement = Arrangement.Center
      ) {

        if (value is Int) {
          AnimatedNumberCounter(value)
        } else
          Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(.9f)
          )
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedNumberCounter(
  theNumber: Int,
  theStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
  theColor: Color = MaterialTheme.colorScheme.primary.copy(.9f),
) {
  AnimatedContent(
    targetState = theNumber,
    transitionSpec = {
      if (targetState > initialState) {
        (slideInVertically { height -> height } + fadeIn())
          .togetherWith(slideOutVertically { height -> -height } + fadeOut())
      } else {
        (slideInVertically { height -> -height } + fadeIn())
          .togetherWith(slideOutVertically { height -> height } + fadeOut())
      }
        .using(SizeTransform(clip = false))
    },
  ) { targetCount ->
    Text(
      text = targetCount.toAmount(),
      style = theStyle,
      color = theColor
    )
  }
}


@Preview(showBackground = true)
@Composable
private fun CompanyHomePaymentMonthlyViewPreview() {
  WasteManagementTheme {
    CompanyHomePaymentMonthlyView(
      state = companyHomeStateSuccess(),
      onEvent = {}
    )
  }
}
