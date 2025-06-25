package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

import android.net.Uri

sealed interface MainActivityEvent {
  data class Import(val uri: Uri) : MainActivityEvent
}
