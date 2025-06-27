package net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.metadata

import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.google.gson.Gson
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.remote.ServerResponse
import net.techandgraphics.wastemanagement.getFile
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityChannel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityEvent
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainViewModel

class ImportMetadataExtension(private val viewModel: MainViewModel) {

  fun onImport(event: MainActivityEvent.Import) = with(viewModel) {
    viewModel.viewModelScope.launch {
      runCatching {
        val jsonString = application.getFile(event.uri).bufferedReader().use { it.readText() }
        Gson().fromJson(jsonString, ServerResponse::class.java)
      }.onSuccess { metadata ->

        if (metadata.accounts == null) {
          channelFlow.send(MainActivityChannel.Import.Data(MainActivityChannel.Import.Status.Invalid))
          return@launch
        }

        var current = 0
        runCatching {
          database.withTransaction {
            accountSession.purseData(metadata) { total, done ->
              current += done
              channelFlow.send(MainActivityChannel.Import.Progress(total, current))
            }
          }
        }
          .onSuccess { channelFlow.send(MainActivityChannel.Load) }
          .onFailure { channelFlow.send(MainActivityChannel.Import.Data(MainActivityChannel.Import.Status.Error)) }
      }
        .onFailure { channelFlow.send(MainActivityChannel.Import.Data(MainActivityChannel.Import.Status.Error)) }
    }
  }
}
