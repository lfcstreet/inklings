package com.example.inklings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class TimerState { STOPPED, RUNNING, PAUSED, COMPLETED }

class WritingViewModel(application: Application) : AndroidViewModel(application) {

    private var sessionManager = SessionManager(application)
    private val soundManager = TypewriterSoundManager(application)
    private val settingsManager = SettingsManager(application)
    private var lastSavedText = ""

    var textFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    var isSoundEnabled by mutableStateOf(settingsManager.isTypewriterSoundEnabled)
        private set

    // Timer state (Requirement 15)
    var timerState by mutableStateOf(TimerState.STOPPED)
        private set
    var remainingTimeMillis by mutableLongStateOf(settingsManager.timerDurationMinutes * 60 * 1000L)
        private set
    var totalDurationMillis by mutableLongStateOf(settingsManager.timerDurationMinutes * 60 * 1000L)
        private set
    private var timerJob: Job? = null

    // UI Events for completion flash
    private val _showCompletionFlash = MutableSharedFlow<Unit>()
    val showCompletionFlash = _showCompletionFlash.asSharedFlow()

    // Timing state (Writing Time Log)
    private var totalAccumulatedMillis = 0L
    private var currentPeriodStartMillis: Long? = null
    private var lastActivityMillis: Long? = null
    private val IDLE_TIMEOUT_MILLIS = 10 * 60 * 1000L

    // Requirement 10D-FIX-02: Splitting capitalization responsibility.
    private var isPendingPhysicalCapitalization = true 
    private var expectedPhysicalCapOffset = 0

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        // Requirement 13: Silent auto-save every 1 minute
        startAutoSaveLoop()
    }

    private fun startAutoSaveLoop() {
        viewModelScope.launch {
            while (isActive) {
                delay(60 * 1000L)
                val currentText = textFieldValue.text
                // Requirement 16: Shared save-decision logic for auto-save
                if (shouldSave(currentText) && currentText != lastSavedText) {
                    save(silent = true)
                }
            }
        }
    }

    private fun shouldSave(content: String): Boolean {
        // Requirement 16: 
        // 1. Fresh document (not saved) + empty/whitespace -> skip save to avoid creating unwanted files.
        // 2. Previously saved document -> always save to allow intentional clearing/emptying of the file.
        // Empty-content status is NOT used as the saved-state indicator.
        return sessionManager.isDocumentSaved || content.isNotBlank()
    }

    fun updateText(newValue: TextFieldValue) {
        val oldText = textFieldValue.text
        val newText = newValue.text

        // Requirement 14: Typewriter Sound Triggering
        if (isSoundEnabled && newText != oldText) {
            val selection = newValue.selection
            if (selection.collapsed) {
                val oldLen = oldText.length
                val newLen = newText.length
                
                if (newLen == oldLen + 1) {
                    // Single character insertion (Typing)
                    val cursor = selection.start
                    if (cursor > 0) {
                        val char = newText[cursor - 1]
                        if (char == ' ') {
                            soundManager.playSpaceSound()
                        } else {
                            soundManager.playKeySound()
                        }
                    }
                } else if (newLen < oldLen) {
                    // Text length decreased (Deletion)
                    // We only play sound if the user is not selecting a large block (Requirement says "Backspace")
                    // BasicTextField handles Backspace as a decrease in length.
                    // If old cursor was after some text and now it's before it, it's likely a delete.
                    // For simplicity, any length decrease in collapsed selection is treated as backspace.
                    soundManager.playBackspaceSound()
                }
            }
        }

        if (newText.length > oldText.length) {
            isPendingPhysicalCapitalization = false
        }
        
        if (newText.length < oldText.length || 
            !newValue.selection.collapsed ||
            (isPendingPhysicalCapitalization && newValue.selection.start != expectedPhysicalCapOffset)) {
            isPendingPhysicalCapitalization = false
        }

        val finalValue = handleDoubleSpace(newValue, textFieldValue)
        
        if (finalValue.text != textFieldValue.text) {
            trackActivity()
        }
        textFieldValue = finalValue
    }

    /**
     * Requirement 10D-FIX-02 & 13: Manual capitalization and shortcuts for physical keyboards.
     */
    fun handlePhysicalKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        // Requirement 13: Keyboard Shortcuts (Ctrl+S, Ctrl+N, Ctrl+Q)
        // These intentionally invoke the same underlying actions as the UI icons.
        if (event.isCtrlPressed) {
            when (event.key) {
                androidx.compose.ui.input.key.Key.S -> {
                    save(silent = true)
                    return true
                }
                androidx.compose.ui.input.key.Key.N -> {
                    newSession(silent = true)
                    return true
                }
                androidx.compose.ui.input.key.Key.Q -> {
                    closeSession(silent = true)
                    return true
                }
            }
        }

        if (!isPendingPhysicalCapitalization) return false
        
        val codePoint = event.utf16CodePoint
        if (codePoint == 0) return false
        
        val char = codePoint.toChar()
        if (char.isLetter()) {
            val selection = textFieldValue.selection
            if (selection.collapsed && selection.start == expectedPhysicalCapOffset) {
                val oldText = textFieldValue.text
                val capitalizedChar = char.uppercaseChar()
                val newText = oldText.substring(0, selection.start) + capitalizedChar + oldText.substring(selection.start)
                textFieldValue = textFieldValue.copy(
                    text = newText,
                    selection = TextRange(selection.start + 1)
                )
                if (isSoundEnabled) {
                    soundManager.playKeySound()
                }
                isPendingPhysicalCapitalization = false
                trackActivity()
                return true 
            }
        }
        
        return false
    }

    private fun handleDoubleSpace(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
        if (new.text.length != old.text.length + 1) return new
        
        val selection = new.selection
        if (!selection.collapsed) return new
        
        val cursor = selection.start
        if (cursor < 2) return new

        val text = new.text
        if (text[cursor - 1] == ' ' && text[cursor - 2] == ' ') {
            val charBeforeSpaces = if (cursor > 2) text[cursor - 3] else null
            
            if (charBeforeSpaces != null && charBeforeSpaces != '.' && charBeforeSpaces != ' ') {
                val newText = text.substring(0, cursor - 2) + ". " + text.substring(cursor)
                isPendingPhysicalCapitalization = true
                expectedPhysicalCapOffset = cursor
                return new.copy(text = newText, selection = TextRange(cursor))
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

    fun save(silent: Boolean = false, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val content = textFieldValue.text
            
            // Requirement 16: Prevent saving fresh whitespace-only documents.
            if (!shouldSave(content)) {
                onSuccess?.invoke()
                return@launch
            }

            val result = sessionManager.saveDocument(content)
            if (result.isSuccess) {
                lastSavedText = content
                if (!silent) {
                    _uiEvent.emit(UiEvent.ShowToast("Saved: ${sessionManager.sessionFileName}"))
                }
                onSuccess?.invoke()
            } else {
                _uiEvent.emit(UiEvent.ShowError("Save Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    private suspend fun finalizeAndSaveTimeLog(endTime: Long): Boolean {
        // Requirement 16: Fresh documents that are never saved because they are empty 
        // must not create log files (BAS- files).
        if (!sessionManager.isDocumentSaved) {
            return true
        }

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

    fun newSession(silent: Boolean = false) {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            val content = textFieldValue.text

            // Requirement 16: Implement same-decision logic for New session transition.
            if (!shouldSave(content)) {
                resetToNewSession(silent)
                return@launch
            }

            val docResult = sessionManager.saveDocument(content)
            if (docResult.isSuccess) {
                if (finalizeAndSaveTimeLog(endTime)) {
                    resetToNewSession(silent)
                }
            } else {
                _uiEvent.emit(UiEvent.ShowError("Save Error: ${docResult.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    private fun resetToNewSession(silent: Boolean) {
        sessionManager = SessionManager(getApplication())
        textFieldValue = TextFieldValue("")
        lastSavedText = ""
        totalAccumulatedMillis = 0L
        currentPeriodStartMillis = null
        lastActivityMillis = null
        isPendingPhysicalCapitalization = true
        expectedPhysicalCapOffset = 0
        resetTimer()
        if (!silent) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("New session started"))
            }
        }
    }

    fun closeSession(silent: Boolean = false) {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            val content = textFieldValue.text

            // Requirement 16: Implement same-decision logic for Close action.
            if (!shouldSave(content)) {
                _uiEvent.emit(UiEvent.CloseApp)
                return@launch
            }

            val docResult = sessionManager.saveDocument(content)
            if (docResult.isSuccess) {
                if (finalizeAndSaveTimeLog(endTime)) {
                    _uiEvent.emit(UiEvent.CloseApp)
                }
            } else {
                _uiEvent.emit(UiEvent.ShowError("Save Error: ${docResult.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    // Timer functions (Requirement 15)
    fun toggleTimer() {
        when (timerState) {
            TimerState.RUNNING -> pauseTimer()
            else -> startTimer()
        }
    }

    private fun startTimer() {
        if (timerState == TimerState.COMPLETED || remainingTimeMillis <= 0L) {
            resetTimer()
        }
        timerState = TimerState.RUNNING
        val initialRemaining = remainingTimeMillis
        val startTime = System.currentTimeMillis()
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && timerState == TimerState.RUNNING) {
                val elapsed = System.currentTimeMillis() - startTime
                remainingTimeMillis = (initialRemaining - elapsed).coerceAtLeast(0L)
                
                if (remainingTimeMillis <= 0L) {
                    timerState = TimerState.COMPLETED
                    _showCompletionFlash.emit(Unit)
                    break
                }
                delay(100) 
            }
        }
    }

    private fun pauseTimer() {
        timerState = TimerState.PAUSED
        timerJob?.cancel()
    }

    fun resetTimer() {
        timerState = TimerState.STOPPED
        timerJob?.cancel()
        totalDurationMillis = settingsManager.timerDurationMinutes * 60 * 1000L
        remainingTimeMillis = totalDurationMillis
    }

    fun setTimerDuration(minutes: Int) {
        settingsManager.timerDurationMinutes = minutes
        if (timerState == TimerState.STOPPED || timerState == TimerState.COMPLETED) {
            resetTimer()
        }
    }

    fun getTimerDuration(): Int = settingsManager.timerDurationMinutes

    fun toggleSound() {
        isSoundEnabled = !isSoundEnabled
        settingsManager.isTypewriterSoundEnabled = isSoundEnabled
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object CloseApp : UiEvent()
    }
}
