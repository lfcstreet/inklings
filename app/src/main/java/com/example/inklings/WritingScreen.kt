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
    
    // Requirement 10A: State for visible line ranges and their alphas
    var visibleLineRanges by remember { mutableStateOf<List<Pair<IntRange, Float>>>(emptyList()) }
    
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

    // Requirement 10A: Multi-level fading transformation with hard boundary at line 6
    val fadeTransformation = remember(isDistractionFreeMode, visibleLineRanges, onSurfaceColor) {
        VisualTransformation { text ->
            val annotated = buildAnnotatedString {
                append(text.text)
                if (isDistractionFreeMode && visibleLineRanges.isNotEmpty()) {
                    val len = text.length
                    // Everything is invisible by default
                    addStyle(SpanStyle(color = Color.Transparent), 0, len)
                    
                    // Apply alpha to specific line ranges
                    visibleLineRanges.forEach { (range, alpha) ->
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
                            val currentLine = layout.getLineForOffset(cursorIndex)
                            
                            // Calculate ranges for 5 lines above and 5 lines below
                            val newRanges = mutableListOf<Pair<IntRange, Float>>()
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
                                    if (alpha > 0f) {
                                        val start = layout.getLineStart(lineIndex)
                                        val end = layout.getLineEnd(lineIndex)
                                        newRanges.add(IntRange(start, end) to alpha)
                                    }
                                }
                            }
                            visibleLineRanges = newRanges
                        } else {
                            visibleLineRanges = emptyList() // All visible
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
