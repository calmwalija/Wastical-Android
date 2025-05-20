package net.techandgraphics.wastemanagement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.session.SessionRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val session: SessionRepository,
) : ViewModel() {

  init {
    viewModelScope.launch {
      session.invoke()
    }
  }
}
