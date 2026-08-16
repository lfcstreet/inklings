package com.example.inklings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.text.TextRange
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

    // Requirement 10D-FIX-02: Splitting capitalization responsibility.
    // 1. Android's IME handles normal sentence capitalization for on-screen keyboard input.
    //    (The editor is configured as normal sentence-based text input in WritingScreen.kt)
    // 2. Physical keyboard input may not receive IME sentence capitalization, so
    //    the application provides equivalent capitalization for physical keyboard input.
    // No capitalization behavior is global to Android or other applications.
    private var isPendingPhysicalCapitalization = true 
    private var expectedPhysicalCapOffset = 0

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateText(newValue: TextFieldValue) {
        val oldText = textFieldValue.text
        val newText = newValue.text

        // If text is inserted via onValueChange (indicating IME input), 
        // we set isPendingPhysicalCapitalization = false to avoid double-processing.
        // IME-provided text must not be passed through the physical-keyboard capitalization logic.
        if (newText.length > oldText.length) {
            isPendingPhysicalCapitalization = false
        }
        
        // Reset state if user deletes text, uses selection, or moves cursor manually
        if (newText.length < oldText.length || 
            !newValue.selection.collapsed ||
            (isPendingPhysicalCapitalization && newValue.selection.start != expectedPhysicalCapOffset)) {
            isPendingPhysicalCapitalization = false
        }

        // Apply double-space shortcut (Requirement 10D - custom application feature)
        val finalValue = handleDoubleSpace(newValue, textFieldValue)
        
        if (finalValue.text != textFieldValue.text) {
            trackActivity()
        }
        textFieldValue = finalValue
    }

    /**
     * Requirement 10D-FIX-02: Manual capitalization for physical keyboards.
     */
    fun handlePhysicalKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        if (!isPendingPhysicalCapitalization) return false
        
        val codePoint = event.utf16CodePoint
        if (codePoint == 0) return false
        
        val char = codePoint.toChar()
        if (char.isLetter()) {
            val selection = textFieldValue.selection
            if (selection.collapsed && selection.start == expectedPhysicalCapOffset) {
                // Manually insert capitalized character for physical keyboard
                val oldText = textFieldValue.text
                val capitalizedChar = char.uppercaseChar()
                val newText = oldText.substring(0, selection.start) + capitalizedChar + oldText.substring(selection.start)
                textFieldValue = textFieldValue.copy(
                    text = newText,
                    selection = TextRange(selection.start + 1)
                )
                isPendingPhysicalCapitalization = false
                trackActivity()
                return true // Consumed
            }
        } else if (!char.isWhitespace()) {
            // Non-alphabetic character: wait for the next letter
            // updateText will handle the insertion and keeping flag true
        }
        
        return false
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
            val charBeforeSpaces = if (cursor > 2) text[cursor - 3] else null
            
            if (charBeforeSpaces != null && charBeforeSpaces != '.' && charBeforeSpaces != ' ') {
                val newText = text.substring(0, cursor - 2) + ". " + text.substring(cursor)
                
                // Requirement 10D-FIX-02: Enable physical capitalization for the next letter
                isPendingPhysicalCapitalization = true
                expectedPhysicalCapOffset = cursor
                
                return new.copy(
                    text = newText,
                    selection = TextRange(cursor)
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
            currentPeriodStartMillis = now
            lastActivityMillis = now
        } else if (now - last > IDLE_TIMEOUT_MILLIS) {
            totalAccumulatedMillis += (last - start)
            currentPeriodStartMillis = now
            lastActivityMillis = now
        } else {
            lastActivityMillis = now
        }
    }

    private fun calculateTotalMinutes(endTime: Long): Int {
        var total = totalAccumulatedMillis
        val start = currentPeriodStartMillis
        val last = lastActivityMillis

        if (start != null && last != null) {
            if (endTime - last > IDLE_TIMEOUT_MILLIS) {
                total += (last - start)
            } else {
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
            val docResult = sessionManager.saveDocument(textFieldValue.text)
            if (docResult.isSuccess) {
                if (finalizeAndSaveTimeLog(endTime)) {
                    sessionManager = SessionManager(getApplication())
                    textFieldValue = TextFieldValue("")
                    totalAccumulatedMillis = 0L
                    currentPeriodStartMillis = null
                    lastActivityMillis = null
                    
                    // Requirement 10D-FIX-02: Reset capitalization state for new session
                    isPendingPhysicalCapitalization = true
                    expectedPhysicalCapOffset = 0
                    
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
            val docResult = sessionManager.saveDocument(textFieldValue.text)
            if (docResult.isSuccess) {
                if (finalizeAndSaveTimeLog(endTime)) {
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
