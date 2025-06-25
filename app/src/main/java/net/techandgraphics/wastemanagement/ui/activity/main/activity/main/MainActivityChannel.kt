package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

sealed interface MainActivityChannel {
  data object Empty : MainActivityChannel
  data object Load : MainActivityChannel
  sealed interface Import : MainActivityChannel {
    enum class Status { Wait, Invalid, Error, Success }
    data class Data(val status: Status) : Import
    data class Progress(val total: Int, val current: Int) : Import
  }
}
