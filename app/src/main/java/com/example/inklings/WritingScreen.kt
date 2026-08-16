package com.example.inklings

import android.widget.Toast
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inklings.ui.theme.CourierPrime

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
    val textFieldValue = viewModel.textFieldValue
    val scrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    
    // Mode tracking
    var isDistractionFreeMode by rememberSaveable { mutableStateOf(true) }
    
    // Current active fade mode (Requirement 10C is currently active)
    val activeFadeMode = FadeMode.SENTENCE_CORE_WITH_LINE_OUTER_FADE
    
    // State for visible character ranges and their alphas
    var visibleRanges by remember { mutableStateOf<List<Pair<IntRange, Float>>>(emptyList()) }
    
    val density = LocalDensity.current
    val context = LocalContext.current
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

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
    val fadeTransformation = remember(isDistractionFreeMode, visibleRanges, onSurfaceColor) {
        VisualTransformation { text ->
            val annotated = buildAnnotatedString {
                append(text.text)
                if (isDistractionFreeMode && visibleRanges.isNotEmpty()) {
                    val len = text.length
                    // Everything outside calculated ranges is invisible
                    addStyle(SpanStyle(color = Color.Transparent), 0, len)
                    
                    visibleRanges.forEach { (range, alpha) ->
                        val start = maxOf(0, range.first)
                        val end = minOf(len, range.last)
                        if (start < end) {
                            addStyle(SpanStyle(color = onSurfaceColor.copy(alpha = alpha)), start, end)
                        }
                    }
                }
            }
            TransformedText(annotated, OffsetMapping.Identity)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(8.dp)
            ) {
                TextButton(onClick = { viewModel.save() }) {
                    Text("SAVE")
                }
                TextButton(onClick = { viewModel.newSession() }) {
                    Text("NEW")
                }
                TextButton(onClick = { viewModel.closeSession() }) {
                    Text("CLOSE")
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val viewportHeight = maxHeight
            val halfViewportHeight = viewportHeight / 2

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
                        } else if (newValue.selection != textFieldValue.selection) {
                            isDistractionFreeMode = false
                        }
                        viewModel.updateText(newValue)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    textStyle = TextStyle(
                        fontFamily = CourierPrime,
                        fontSize = 22.sp,
                        lineHeight = 32.sp,
                        color = onSurfaceColor
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
                                // 1. Identify central three sentences
                                val currentSentence = findSentenceRange(text, cursorIndex)
                                val prevSentence = if (currentSentence.first > 0) findSentenceRange(text, currentSentence.first - 1) else null
                                val nextSentence = if (currentSentence.last < text.length) findSentenceRange(text, currentSentence.last + 1) else null
                                
                                // Requirement 10C-FIX-01: Same-line sentence visibility behavior.
                                // The central region is primarily sentence-based (30% / 100% / 30% visibility).
                                // To prevent unnatural visual discontinuities, any text from another sentence that
                                // shares a physical display line with a 30% sentence is also allowed to remain at 30%.
                                // Text outside this multi-line region continues to use the physical line-based outer fade.
                                val regionStart = prevSentence?.first ?: currentSentence.first
                                val regionEnd = nextSentence?.last ?: currentSentence.last
                                val regionStartLine = layout.getLineForOffset(regionStart)
                                val regionEndLine = layout.getLineForOffset(minOf(regionEnd, text.length))

                                // Core region (30% by default, including same-line text)
                                for (line in regionStartLine..regionEndLine) {
                                    newRanges.add(IntRange(layout.getLineStart(line), layout.getLineEnd(line)) to 0.3f)
                                }

                                // Current sentence (100% override)
                                newRanges.add(currentSentence to 1.0f)
                                
                                // 2. Outer Line-Based Fade
                                // Fade distance measured in physical lines away from the core region boundary.
                                // Fade above the region
                                for (i in 1..3) {
                                    val lineIndex = regionStartLine - i
                                    if (lineIndex >= 0) {
                                        val alpha = if (i == 1) 0.15f else if (i == 2) 0.05f else 0.0f
                                        if (alpha > 0f) {
                                            newRanges.add(IntRange(layout.getLineStart(lineIndex), layout.getLineEnd(lineIndex)) to alpha)
                                        }
                                    }
                                }
                                
                                // Fade below the region
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

            // Typewriter scrolling logic (only in Distraction-Free Mode)
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
