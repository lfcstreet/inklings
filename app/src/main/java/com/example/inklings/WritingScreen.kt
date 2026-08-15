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
fun WritingScreen(viewModel: WritingViewModel = viewModel()) {
    val textFieldValue = viewModel.textFieldValue
    val scrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    
    // Requirement 04: Mode tracking
    var isDistractionFreeMode by rememberSaveable { mutableStateOf(true) }
    var hiddenStartOffset by remember { mutableIntStateOf(0) }
    var hiddenEndOffset by remember { mutableIntStateOf(0) }
    
    val density = LocalDensity.current
    val context = LocalContext.current

    // Observe save results
    LaunchedEffect(Unit) {
        viewModel.saveResult.collect { result ->
            when (result) {
                is WritingViewModel.SaveResult.Success -> {
                    Toast.makeText(context, "Saved: ${result.fileName}", Toast.LENGTH_SHORT).show()
                }
                is WritingViewModel.SaveResult.Error -> {
                    Toast.makeText(context, "Save Error: ${result.message}", Toast.LENGTH_LONG).show()
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

    // Requirement 04: Visual transformation to hide text outside writing window
    val typewriterTransformation = remember(isDistractionFreeMode, hiddenStartOffset, hiddenEndOffset) {
        VisualTransformation { text ->
            val annotated = buildAnnotatedString {
                append(text.text)
                if (isDistractionFreeMode) {
                    // Hide everything before the writing window
                    if (hiddenStartOffset > 0) {
                        addStyle(
                            style = SpanStyle(color = Color.Transparent),
                            start = 0,
                            end = minOf(hiddenStartOffset, text.length)
                        )
                    }
                    // Hide everything after the writing window
                    if (hiddenEndOffset < text.length) {
                        addStyle(
                            style = SpanStyle(color = Color.Transparent),
                            start = maxOf(0, hiddenEndOffset),
                            end = text.length
                        )
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
                TextButton(onClick = { /* TODO: New */ }) {
                    Text("NEW")
                }
                TextButton(onClick = { /* TODO: Close */ }) {
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
                        // Detect transition to Distraction-Free Mode (actual editing)
                        if (newValue.text != textFieldValue.text) {
                            isDistractionFreeMode = true
                        }
                        viewModel.updateText(newValue)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textStyle = TextStyle(
                        fontFamily = CourierPrime,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    visualTransformation = typewriterTransformation,
                    onTextLayout = { layout ->
                        textLayoutResult = layout
                        
                        // Calculate writing window (1 line above, current, 1 line below)
                        val selection = textFieldValue.selection
                        if (selection.collapsed) {
                            val cursorIndex = selection.start
                            val lineIndex = layout.getLineForOffset(cursorIndex)
                            
                            // Start of writing window: 1 line above
                            val windowStartLine = maxOf(0, lineIndex - 1)
                            hiddenStartOffset = layout.getLineStart(windowStartLine)
                            
                            // End of writing window: 1 line below
                            val windowEndLine = minOf(layout.lineCount - 1, lineIndex + 1)
                            hiddenEndOffset = layout.getLineEnd(windowEndLine)
                        } else {
                            // Show all text during selection
                            hiddenStartOffset = 0
                            hiddenEndOffset = textFieldValue.text.length
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
