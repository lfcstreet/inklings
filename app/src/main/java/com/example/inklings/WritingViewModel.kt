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

    // Timing state
    private var totalAccumulatedMillis = 0L
    private var currentPeriodStartMillis: Long? = null
    private var lastActivityMillis: Long? = null
    private val IDLE_TIMEOUT_MILLIS = 10 * 60 * 1000L

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateText(newValue: TextFieldValue) {
        val processedValue = handleDoubleSpace(newValue, textFieldValue)
        if (processedValue.text != textFieldValue.text) {
            trackActivity()
        }
        textFieldValue = processedValue
    }

    private fun handleDoubleSpace(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
        // Only trigger on single character insertion
        if (new.text.length != old.text.length + 1) return new
        
        val selection = new.selection
        if (!selection.collapsed) return new
        
        val cursor = selection.start
        if (cursor < 2) return new

        val text = new.text
        // Check if the last two characters are spaces
        if (text[cursor - 1] == ' ' && text[cursor - 2] == ' ') {
            // Check what's before the two spaces
            val charBeforeSpaces = if (cursor > 2) text[cursor - 3] else null
            
            // Don't trigger if it's the start of the doc (handled by cursor < 2)
            // or if there's already a period or another space
            if (charBeforeSpaces != null && charBeforeSpaces != '.' && charBeforeSpaces != ' ') {
                val newText = text.substring(0, cursor - 2) + ". " + text.substring(cursor)
                return new.copy(
                    text = newText,
                    selection = androidx.compose.ui.text.TextRange(cursor)
                )
            }
        }
        return new
    }

    private fun trackActivity() {
        val now = System.currentTimeMillis()
        val last = lastActivityMillis
        val start = currentPeriodStartMillis

        if (start == null || last == null) {
            // First activity
            currentPeriodStartMillis = now
            lastActivityMillis = now
        } else if (now - last > IDLE_TIMEOUT_MILLIS) {
            // Resuming after idle: finalize previous period
            totalAccumulatedMillis += (last - start)
            currentPeriodStartMillis = now
            lastActivityMillis = now
        } else {
            // Still active
            lastActivityMillis = now
        }
    }

    private fun calculateTotalMinutes(endTime: Long): Int {
        var total = totalAccumulatedMillis
        val start = currentPeriodStartMillis
        val last = lastActivityMillis

        if (start != null && last != null) {
            if (endTime - last > IDLE_TIMEOUT_MILLIS) {
                // Idle at end: only count up to last activity
                total += (last - start)
            } else {
                // Active at end: count up to end time
                total += (endTime - start)
            }
        }
        
        return (total / (60 * 1000L)).toInt()
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

    private suspend fun finalizeAndSaveTimeLog(endTime: Long): Boolean {
        val minutes = calculateTotalMinutes(endTime)
        if (minutes >= 1) {
            val result = sessionManager.saveTimeLog(minutes)
            if (result.isFailure) {
                _uiEvent.emit(UiEvent.ShowError("Time Log Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
                return false
            }
        }
        return true
    }

    fun newSession() {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            // 1. Save document
            val docResult = sessionManager.saveDocument(textFieldValue.text)
            if (docResult.isSuccess) {
                // 2. Save time log
                if (finalizeAndSaveTimeLog(endTime)) {
                    // 3. Start new session
                    sessionManager = SessionManager(getApplication())
                    textFieldValue = TextFieldValue("")
                    totalAccumulatedMillis = 0L
                    currentPeriodStartMillis = null
                    lastActivityMillis = null
                    _uiEvent.emit(UiEvent.ShowToast("New session started"))
                }
            } else {
                _uiEvent.emit(UiEvent.ShowError("Save Error: ${docResult.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    fun closeSession() {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            // 1. Save document
            val docResult = sessionManager.saveDocument(textFieldValue.text)
            if (docResult.isSuccess) {
                // 2. Save time log
                if (finalizeAndSaveTimeLog(endTime)) {
                    // 3. Close app
                    _uiEvent.emit(UiEvent.CloseApp)
                }
            } else {
                _uiEvent.emit(UiEvent.ShowError("Save Error: ${docResult.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object CloseApp : UiEvent()
    }
}
