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

    private val sessionManager = SessionManager(application)

    var textFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    private val _saveResult = MutableSharedFlow<SaveResult>()
    val saveResult = _saveResult.asSharedFlow()

    fun updateText(newValue: TextFieldValue) {
        textFieldValue = newValue
    }

    fun save() {
        viewModelScope.launch {
            val result = sessionManager.saveDocument(textFieldValue.text)
            if (result.isSuccess) {
                _saveResult.emit(SaveResult.Success(sessionManager.sessionFileName))
            } else {
                _saveResult.emit(SaveResult.Error(result.exceptionOrNull()?.message ?: "Unknown error"))
            }
        }
    }

    sealed class SaveResult {
        data class Success(val fileName: String) : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}
