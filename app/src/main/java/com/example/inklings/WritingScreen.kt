package com.example.inklings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inklings.ui.theme.CourierPrime
import kotlinx.coroutines.delay

// Requirement 10C: Fade behavior is modularized.
// Requirement 10A (Progressive Line Fade) is intentionally retained and available for future reuse.
// A future setting will allow the user to select between fade modes.
enum class FadeMode {
    PROGRESSIVE_LINE_FADE, // Requirement 10A
    SENTENCE_CORE_WITH_LINE_OUTER_FADE // Requirement 10C
}

@Composable
fun WritingScreen(
    viewModel: WritingViewModel = viewModel(),
    onCloseApp: () -> Unit = {}
) {
    // Requirement 12: Immersive full-screen mode is handled in MainActivity.
    // The writing area expands to use the additional space provided by hiding system bars.
    
    val textFieldValue = viewModel.textFieldValue
    val scrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    
    // Mode tracking
    var isDistractionFreeMode by rememberSaveable { mutableStateOf(true) }
    
    // Requirement 11: Toggle visibility of action buttons
    var showActionButtons by rememberSaveable { mutableStateOf(false) }
    
    // Requirement 14: Toggle visibility of settings panel
    var showSettingsPanel by rememberSaveable { mutableStateOf(false) }
    
    // Requirement 17B: Toggle visibility of project management
    var showProjectPanel by rememberSaveable { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<Project?>(null) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    
    // Requirement 18: Rename dialog
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    
    // Requirement 10C: Fade behavior is modularized.
    // Requirement 10A (Progressive Line Fade) is intentionally retained and available for future reuse.
    // A future setting will allow the user to select between fade modes.
    // Requirement 10D-FIX-02: 
    // 1. The writing editor is intentionally configured as normal sentence-based text input 
    //    so Android IMEs can provide their native capitalization behavior.
    // 2. Physical keyboard input may not receive IME sentence capitalization, so the application 
    //    provides equivalent capitalization for physical keyboard input via handlePhysicalKeyEvent.
    // 3. The double-space -> ". " transformation is a separate custom application feature.
    // Requirement 13: 
    // 1. Keyboard shortcuts (Ctrl+S, Ctrl+N, Ctrl+Q) intentionally invoke the same underlying 
    //    actions as the corresponding UI icons.
    // 2. Auto-save runs silently every 1 minute (handled in ViewModel).
    val activeFadeMode = FadeMode.SENTENCE_CORE_WITH_LINE_OUTER_FADE
    
    // State for visible character ranges and their alphas
    var visibleRanges by remember { mutableStateOf<List<Pair<IntRange, Float>>>(emptyList()) }
    
    val density = LocalDensity.current
    val context = LocalContext.current
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val isDarkTheme = isSystemInDarkTheme()

    // Requirement 17B: Get theme-aware font color from project.
    // The project derives a contrast-safe color from the user's baseFontColor.
    val projectFontColor = remember(viewModel.currentProject, isDarkTheme) {
        Color(viewModel.currentProject.getThemeAwareColor(isDarkTheme))
    }

    // Requirement 15: Timer completion state (replaces flash with red color for 2s)
    var isTimerCompletionColorActive by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.showCompletionFlash.collect {
            isTimerCompletionColorActive = true
            delay(5000)
            isTimerCompletionColorActive = false
        }
    }

    // Observe UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is WritingViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is WritingViewModel.UiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is WritingViewModel.UiEvent.CloseApp -> {
                    onCloseApp()
                }
            }
        }
    }

    // Detect transition to Browsing Mode (manual scrolling)
    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            isDistractionFreeMode = false
        }
    }

    // Visual transformation to apply calculated alphas
    val fadeTransformation = remember(isDistractionFreeMode, visibleRanges, projectFontColor, isTimerCompletionColorActive) {
        VisualTransformation { text ->
            val annotated = buildAnnotatedString {
                append(text.text)
                if (isTimerCompletionColorActive) {
                    // Requirement 15: Color entire text red on completion
                    addStyle(SpanStyle(color = Color.Red), 0, text.length)
                } else if (isDistractionFreeMode && visibleRanges.isNotEmpty()) {
                    val len = text.length
                    // Everything outside calculated ranges is invisible
                    addStyle(SpanStyle(color = Color.Transparent), 0, len)
                    
                    visibleRanges.forEach { (range, alpha) ->
                        val start = maxOf(0, range.first)
                        val end = minOf(len, range.last)
                        if (start < end) {
                            addStyle(SpanStyle(color = projectFontColor.copy(alpha = alpha)), start, end)
                        }
                    }
                } else {
                    // Normal mode / non-distraction mode: use project color
                    addStyle(SpanStyle(color = projectFontColor), 0, text.length)
                }
            }
            TransformedText(annotated, OffsetMapping.Identity)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val viewportHeight = maxHeight
            val halfViewportHeight = viewportHeight / 2

            // Main Editor Area with Custom Tap Detection for Toggling Buttons (Requirement 11)
            // Using PointerEventPass.Initial to spy on taps without blocking cursor placement.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(pass = PointerEventPass.Initial)
                            val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (up != null && !scrollState.isScrollInProgress) {
                                if (showSettingsPanel) {
                                    showSettingsPanel = false
                                } else {
                                    showActionButtons = !showActionButtons
                                }
                            }
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(halfViewportHeight))

                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            if (newValue.text != textFieldValue.text) {
                                isDistractionFreeMode = true
                                showActionButtons = false // Requirement 11: Hide on typing
                                showSettingsPanel = false // Requirement 14: Hide on typing
                            } else if (newValue.selection != textFieldValue.selection) {
                                isDistractionFreeMode = false
                            }
                            viewModel.updateText(newValue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                            .onKeyEvent { viewModel.handlePhysicalKeyEvent(it) },
                        textStyle = TextStyle(
                            fontFamily = CourierPrime,
                            fontSize = 22.sp,
                            lineHeight = 32.sp,
                            color = onSurfaceColor
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        visualTransformation = fadeTransformation,
                        onTextLayout = { layout ->
                            textLayoutResult = layout
                            
                            val selection = textFieldValue.selection
                            if (selection.collapsed) {
                                val cursorIndex = selection.start
                                val text = textFieldValue.text
                                
                                val newRanges = mutableListOf<Pair<IntRange, Float>>()
                                
                                if (activeFadeMode == FadeMode.PROGRESSIVE_LINE_FADE) {
                                    // --- Requirement 10A Logic (Preserved but Inactive) ---
                                    val currentLine = layout.getLineForOffset(cursorIndex)
                                    for (i in -5..5) {
                                        val lineIndex = currentLine + i
                                        if (lineIndex in 0 until layout.lineCount) {
                                            val distance = Math.abs(i)
                                            val alpha = when (distance) {
                                                0 -> 1.0f
                                                1 -> 0.8f
                                                2 -> 0.6f
                                                3 -> 0.4f
                                                4 -> 0.2f
                                                5 -> 0.1f
                                                else -> 0.0f
                                            }
                                            newRanges.add(IntRange(layout.getLineStart(lineIndex), layout.getLineEnd(lineIndex)) to alpha)
                                        }
                                    }
                                } else {
                                    // --- Requirement 10C Logic (Currently Active) ---
                                    val currentSentence = findSentenceRange(text, cursorIndex)
                                    val prevSentence = if (currentSentence.first > 0) findSentenceRange(text, currentSentence.first - 1) else null
                                    val nextSentence = if (currentSentence.last < text.length) findSentenceRange(text, currentSentence.last + 1) else null
                                    
                                    // Requirement 10C-FIX-01: Same-line sentence visibility behavior.
                                    val regionStart = prevSentence?.first ?: currentSentence.first
                                    val regionEnd = nextSentence?.last ?: currentSentence.last
                                    val regionStartLine = layout.getLineForOffset(regionStart)
                                    val regionEndLine = layout.getLineForOffset(minOf(regionEnd, text.length))

                                    for (line in regionStartLine..regionEndLine) {
                                        newRanges.add(IntRange(layout.getLineStart(line), layout.getLineEnd(line)) to 0.3f)
                                    }
                                    newRanges.add(currentSentence to 1.0f)
                                    
                                    for (i in 1..3) {
                                        val lineIndex = regionStartLine - i
                                        if (lineIndex >= 0) {
                                            val alpha = if (i == 1) 0.15f else if (i == 2) 0.05f else 0.0f
                                            if (alpha > 0f) {
                                                newRanges.add(IntRange(layout.getLineStart(lineIndex), layout.getLineEnd(lineIndex)) to alpha)
                                            }
                                        }
                                    }
                                    for (i in 1..3) {
                                        val lineIndex = regionEndLine + i
                                        if (lineIndex < layout.lineCount) {
                                            val alpha = if (i == 1) 0.15f else if (i == 2) 0.05f else 0.0f
                                            if (alpha > 0f) {
                                                newRanges.add(IntRange(layout.getLineStart(lineIndex), layout.getLineEnd(lineIndex)) to alpha)
                                            }
                                        }
                                    }
                                }
                                visibleRanges = newRanges
                            } else {
                                visibleRanges = emptyList() // Browsing mode: All visible
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(halfViewportHeight))
                }
            }

            // Action Button Overlay (Requirement 11 & 14)
            AnimatedVisibility(
                visible = showActionButtons,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { -it / 2 },
                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300)) { -it / 2 },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectTapGestures { } // Prevent background tap toggle
                        }
                ) {
                    ActionButton(
                        icon = Icons.Outlined.Save,
                        contentDescription = "SAVE",
                        onClick = {
                            viewModel.save()
                            showActionButtons = false
                        },
                        tint = primaryColor
                    )
                    ActionButton(
                        icon = Icons.AutoMirrored.Outlined.NoteAdd,
                        contentDescription = "NEW",
                        onClick = {
                            viewModel.newSession()
                            showActionButtons = false
                        },
                        tint = primaryColor
                    )
                    ActionButton(
                        icon = Icons.Outlined.Close,
                        contentDescription = "CLOSE",
                        onClick = {
                            viewModel.closeSession()
                            showActionButtons = false
                        },
                        tint = primaryColor
                    )
                    // Requirement 14: Settings Button
                    ActionButton(
                        icon = Icons.Outlined.Settings,
                        contentDescription = "SETTINGS",
                        onClick = {
                            showSettingsPanel = !showSettingsPanel
                        },
                        tint = primaryColor
                    )
                }
            }

            // Timer & Project Action Buttons (Requirement 15 & 17B)
            // Positioned independently on the right side center.
            AnimatedVisibility(
                visible = showActionButtons,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    TimerButton(
                        state = viewModel.timerState,
                        remainingMillis = viewModel.remainingTimeMillis,
                        totalMillis = viewModel.totalDurationMillis,
                        onToggle = { viewModel.toggleTimer() },
                        onReset = { viewModel.resetTimer() },
                        tint = primaryColor
                    )

                    ActionButton(
                        icon = Icons.Outlined.Folder,
                        contentDescription = "PROJECTS",
                        onClick = {
                            viewModel.refreshProjects()
                            showProjectPanel = true
                        },
                        tint = primaryColor
                    )

                    // Requirement 18: Rename Action
                    if (viewModel.isDocumentSaved) {
                        ActionButton(
                            icon = Icons.Outlined.Edit,
                            contentDescription = "RENAME",
                            onClick = {
                                showRenameDialog = true
                            },
                            tint = primaryColor
                        )
                    }
                }
            }

            // Settings Panel Overlay (Requirement 14 & 15)
            AnimatedVisibility(
                visible = showSettingsPanel && showActionButtons,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp) // Below the action buttons
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = primaryColor.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { } // Prevent closing when tapping inside
                        }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Typewriter Sounds",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onSurfaceColor,
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = viewModel.isSoundEnabled,
                                onCheckedChange = { viewModel.toggleSound() }
                            )
                        }
                        
                        // Requirement 15: Timer Duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Timer Duration",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onSurfaceColor,
                                fontWeight = FontWeight.Bold
                            )
                            TimerDurationSelector(
                                currentMinutes = viewModel.getTimerDuration(),
                                onMinutesSelected = { 
                                    viewModel.setTimerDuration(it)
                                    showSettingsPanel = false
                                },
                                tint = primaryColor
                            )
                        }
                    }
                }
            }

            // Project Management UI (Requirement 17B)
            if (showProjectPanel) {
                // Requirement 32: Selecting a project in this panel for editing 
                // does NOT move the current document. Moving files belongs to 17C.
                ProjectManagementDialog(
                    projects = viewModel.allProjects,
                    currentProject = viewModel.currentProject,
                    isDocumentSaved = viewModel.isDocumentSaved,
                    onProjectClick = { 
                        editingProject = it
                        showProjectPanel = false 
                    },
                    onMoveClick = {
                        viewModel.moveCurrentDocument(it)
                        showProjectPanel = false
                    },
                    onAddProject = { 
                        showCreateProjectDialog = true
                        showProjectPanel = false
                    },
                    onDismiss = { showProjectPanel = false }
                )
            }

            if (showRenameDialog) {
                RenameDialog(
                    onRename = { title ->
                        viewModel.renameCurrentDocument(title)
                        showRenameDialog = false
                    },
                    onDismiss = { showRenameDialog = false }
                )
            }

            if (showCreateProjectDialog) {
                ProjectEditDialog(
                    onSave = { name, color, isDefault ->
                        viewModel.createProject(name, color, isDefault)
                        showCreateProjectDialog = false
                    },
                    onDismiss = { showCreateProjectDialog = false }
                )
            }

            if (editingProject != null) {
                ProjectEditDialog(
                    project = editingProject,
                    onSave = { name, color, isDefault ->
                        viewModel.updateProject(name, color, isDefault)
                        editingProject = null
                    },
                    onDismiss = { editingProject = null }
                )
            }

            // Typewriter scrolling logic
            LaunchedEffect(textFieldValue.selection, textLayoutResult, isDistractionFreeMode) {
                if (!isDistractionFreeMode) return@LaunchedEffect
                
                val layout = textLayoutResult ?: return@LaunchedEffect
                val selection = textFieldValue.selection
                if (selection.collapsed) {
                    val cursorIndex = selection.start
                    val line = layout.getLineForOffset(cursorIndex)
                    val lineTop = layout.getLineTop(line)
                    val lineBottom = layout.getLineBottom(line)
                    
                    val lineTopPx = with(density) { lineTop }
                    val lineBottomPx = with(density) { lineBottom }
                    
                    val lineCenterPx = (lineTopPx + lineBottomPx) / 2
                    val targetScrollPx = lineCenterPx 
                    
                    val absoluteTargetScrollPx = targetScrollPx.toInt()
                    
                    if (scrollState.value != absoluteTargetScrollPx) {
                        scrollState.animateScrollTo(absoluteTargetScrollPx)
                    }
                }
            }
        }
    }
}

/**
 * Requirement 11: Compact icon button styled as a rounded square with an outline.
 */
@Composable
fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .border(2.dp, tint, RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = tint
        )
    }
}

/**
 * Requirement 15: Timer Action Button with circular progress and countdown text.
 */
@Composable
fun TimerButton(
    state: TimerState,
    remainingMillis: Long,
    totalMillis: Long,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    tint: Color
) {
    val minutes = (remainingMillis / 1000) / 60
    val seconds = (remainingMillis / 1000) % 60
    val timeText = "%02d:%02d".format(minutes, seconds)
    val progress = if (totalMillis > 0) remainingMillis.toFloat() / totalMillis else 1f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { onToggle() },
                onLongPress = { onReset() }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .border(2.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(44.dp),
                color = tint,
                strokeWidth = 4.dp,
                trackColor = Color.Transparent,
            )
            Icon(
                imageVector = if (state == TimerState.RUNNING) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                contentDescription = if (state == TimerState.RUNNING) "PAUSE" else "PLAY",
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = timeText,
            style = MaterialTheme.typography.labelMedium,
            color = tint,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Requirement 15: Dropdown selector for Timer Duration.
 */
@Composable
fun TimerDurationSelector(
    currentMinutes: Int,
    onMinutesSelected: (Int) -> Unit,
    tint: Color
) {
    var expanded by remember { mutableStateOf(false) }
    val durations = listOf(1, 10, 15, 30, 45, 60)

    Box {
        Row(
            modifier = Modifier
                .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currentMinutes minutes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.Settings, // Using settings icon as a generic arrow placeholder for now
                contentDescription = "Select",
                modifier = Modifier.size(16.dp),
                tint = tint
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            durations.forEach { minutes ->
                DropdownMenuItem(
                    text = { Text("$minutes minutes") },
                    onClick = {
                        onMinutesSelected(minutes)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Helper for Requirement 10C: Sentence boundaries determined by periods (.).
 */
private fun findSentenceRange(text: String, index: Int): IntRange {
    if (text.isEmpty()) return IntRange(0, 0)
    
    val start = text.lastIndexOf('.', maxOf(0, index - 1)).let {
        if (it == -1) 0 else it + 1
    }
    
    val end = text.indexOf('.', index).let {
        if (it == -1) text.length else it + 1
    }
    
    return IntRange(start, end)
}

/**
 * Requirement 17B & 17C: Project Management Panel.
 * Shows list of projects and allows moving the current document between them.
 */
@Composable
fun ProjectManagementDialog(
    projects: List<Project>,
    currentProject: Project,
    isDocumentSaved: Boolean,
    onProjectClick: (Project) -> Unit,
    onMoveClick: (Project) -> Unit,
    onAddProject: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Projects",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    ActionButton(
                        icon = Icons.Outlined.Add,
                        contentDescription = "Add Project",
                        onClick = onAddProject,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider()

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    projects.forEach { project ->
                        val isCurrent = project.name == currentProject.name
                        ProjectItem(
                            project = project,
                            isCurrent = isCurrent,
                            onMoveClick = if (!isCurrent && isDocumentSaved) {
                                { onMoveClick(project) }
                            } else null,
                            onClick = { onProjectClick(project) }
                        )
                    }
                }
                
                if (!isDocumentSaved) {
                    Text(
                        text = "Save document to enable Move",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismiss) {
                        Text("CLOSE")
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectItem(
    project: Project,
    isCurrent: Boolean,
    onMoveClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val color = try {
            Color(project.baseFontColor.toColorInt())
        } catch (_: Exception) {
            MaterialTheme.colorScheme.onSurface
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
        )

        Text(
            text = project.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (isCurrent) {
            Text(
                text = "Current",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        } else if (onMoveClick != null) {
            // Requirement 17C: Move here action
            TextButton(
                onClick = onMoveClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Move here",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        if (project.isDefault) {
            Text(
                text = "Default",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Requirement 17B Update: Dialog to create or edit a project.
 * Supports visual color picking, manual hex entry, and theme-safe previews.
 */
@Composable
fun ProjectEditDialog(
    project: Project? = null, // null means create mode
    onSave: (name: String, color: String, isDefault: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(project?.name ?: "") }
    var color by remember { mutableStateOf(project?.baseFontColor ?: "#000000") }
    var isDefault by remember { mutableStateOf(project?.isDefault ?: false) }

    var hexInput by remember { mutableStateOf(color) }
    var isHexValid by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (project == null) "New Project" else "Edit ${project.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (project == null) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Project Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Text("Font Color Selection", style = MaterialTheme.typography.titleSmall)

                // Requirement 17B Update: Full-spectrum color picker
                FullSpectrumColorPicker(
                    initialColor = try { Color(color.toColorInt()) } catch (_: Exception) { Color.Black },
                    onColorChanged = { newColor ->
                        val hex = String.format("#%06X", (0xFFFFFF and newColor.toArgb()))
                        color = hex
                        hexInput = hex
                        isHexValid = true
                    }
                )

                // Requirement 17B Update: Manual Hex Entry
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { input ->
                        hexInput = input
                        if (input.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                            color = input
                            isHexValid = true
                        } else {
                            isHexValid = false
                        }
                    },
                    label = { Text("Manual Hex (#RRGGBB)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !isHexValid,
                    supportingText = if (!isHexValid) {
                        { Text("Invalid hex format") }
                    } else null
                )

                // Requirement 17B Update: Theme Adaptation Previews
                Text("Theme Previews", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val previewProject = Project("", color, false)
                    
                    ThemePreviewBox(
                        label = "Light",
                        backgroundColor = Color.White,
                        textColor = Color(previewProject.getThemeAwareColor(false)),
                        modifier = Modifier.weight(1f)
                    )
                    ThemePreviewBox(
                        label = "Dark",
                        backgroundColor = Color.Black,
                        textColor = Color(previewProject.getThemeAwareColor(true)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Default Project", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        enabled = !(project?.isDefault ?: false)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                    TextButton(
                        onClick = { 
                            if (name.isNotBlank() && isHexValid) {
                                onSave(name, color, isDefault)
                            }
                        },
                        enabled = name.isNotBlank() && isHexValid
                    ) {
                        Text("SAVE")
                    }
                }
            }
        }
    }
}

@Composable
fun ThemePreviewBox(label: String, backgroundColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aa",
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Requirement 17B Update: A visual full-spectrum color picker.
 * Uses a Hue slider and a Saturation/Value box.
 */
@Composable
fun FullSpectrumColorPicker(
    initialColor: Color,
    onColorChanged: (Color) -> Unit
) {
    val hsv = remember {
        val hsvArr = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsvArr)
        mutableStateListOf(hsvArr[0], hsvArr[1], hsvArr[2])
    }

    // Sync if initialColor changes externally (e.g. via hex field)
    LaunchedEffect(initialColor) {
        val hsvArr = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsvArr)
        if (hsvArr[0] != hsv[0] || hsvArr[1] != hsv[1] || hsvArr[2] != hsv[2]) {
            hsv[0] = hsvArr[0]
            hsv[1] = hsvArr[1]
            hsv[2] = hsvArr[2]
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Saturation-Value Square
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .drawBehind {
                    // Draw HSV gradient
                    val hueColor = Color.hsv(hsv[0], 1f, 1f)
                    
                    // Value gradient (Black to Top)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White, Color.Transparent),
                            startY = 0f,
                            endY = size.height
                        )
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 0f,
                            endY = size.height
                        )
                    )
                    
                    // Saturation gradient (Transparent to Hue)
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White, hueColor),
                            startX = 0f,
                            endX = size.width
                        ),
                        blendMode = BlendMode.Modulate
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val s = (change.position.x / size.width).coerceIn(0f, 1f)
                        val v = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                        hsv[1] = s
                        hsv[2] = v
                        onColorChanged(Color.hsv(hsv[0], hsv[1], hsv[2]))
                    }
                    detectTapGestures { offset ->
                        val s = (offset.x / size.width).coerceIn(0f, 1f)
                        val v = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                        hsv[1] = s
                        hsv[2] = v
                        onColorChanged(Color.hsv(hsv[0], hsv[1], hsv[2]))
                    }
                }
        ) {
            // SV Selection Indicator
            val indicatorOffset = Offset(
                x = hsv[1] * 1000f, // Simplified, will be updated by draw cycle
                y = (1f - hsv[2]) * 1000f
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val x = hsv[1] * size.width
                val y = (1f - hsv[2]) * size.height
                drawCircle(
                    color = if (hsv[2] > 0.5f) Color.Black else Color.White,
                    radius = 8.dp.toPx(),
                    center = Offset(x, y),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Hue Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .drawBehind {
                    val colors = (0..360).map { Color.hsv(it.toFloat(), 1f, 1f) }
                    drawRect(brush = Brush.horizontalGradient(colors))
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        hsv[0] = (change.position.x / size.width).coerceIn(0f, 1f) * 360f
                        onColorChanged(Color.hsv(hsv[0], hsv[1], hsv[2]))
                    }
                    detectTapGestures { offset ->
                        hsv[0] = (offset.x / size.width).coerceIn(0f, 1f) * 360f
                        onColorChanged(Color.hsv(hsv[0], hsv[1], hsv[2]))
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val x = (hsv[0] / 360f) * size.width
                drawRect(
                    color = Color.White,
                    topLeft = Offset(x - 2.dp.toPx(), 0f),
                    size = Size(4.dp.toPx(), size.height)
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(x - 2.dp.toPx(), 0f),
                    size = Size(4.dp.toPx(), size.height),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun ColorSwatch(hex: String, isSelected: Boolean, onClick: () -> Unit) {
    val swatchColor = try { Color(hex.toColorInt()) } catch (_: Exception) { Color.Black }
    
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(swatchColor, CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

/**
 * Requirement 18: Dialog to rename the current document.
 */
@Composable
fun RenameDialog(
    onRename: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Rename document",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                    TextButton(
                        onClick = { if (title.isNotBlank()) onRename(title) },
                        enabled = title.isNotBlank()
                    ) {
                        Text("RENAME")
                    }
                }
            }
        }
    }
}
