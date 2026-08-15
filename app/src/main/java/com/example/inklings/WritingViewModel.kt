package com.example.inklings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WritingViewModel(application: Application) : AndroidViewModel(application) {

    private var sessionManager = SessionManager(application)

    var textFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateText(newValue: TextFieldValue) {
        textFieldValue = newValue
    }

    fun save(onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val result = sessionManager.saveDocument(textFieldValue.text)
            if (result.isSuccess) {
                _uiEvent.emit(UiEvent.ShowToast("Saved: ${sessionManager.sessionFileName}"))
                onSuccess?.invoke()
            } else {
                _uiEvent.emit(UiEvent.ShowError("Save Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    fun newSession() {
        save(onSuccess = {
            sessionManager = SessionManager(getApplication())
            textFieldValue = TextFieldValue("")
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("New session started"))
            }
        })
    }

    fun closeSession() {
        save(onSuccess = {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.CloseApp)
            }
        })
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object CloseApp : UiEvent()
    }
}
