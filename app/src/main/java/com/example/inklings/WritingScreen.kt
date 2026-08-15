package com.example.inklings

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inklings.ui.theme.CourierPrime

@Composable
fun WritingScreen() {
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val scrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val density = LocalDensity.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(8.dp)
            ) {
                TextButton(onClick = { /* TODO: Save */ }) {
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
                // Top spacer to allow the first line to be centered
                Spacer(modifier = Modifier.height(halfViewportHeight))

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textStyle = TextStyle(
                        fontFamily = CourierPrime,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    onTextLayout = { textLayoutResult = it }
                )

                // Bottom spacer to allow the last line to be centered
                Spacer(modifier = Modifier.height(halfViewportHeight))
            }

            // Typewriter scrolling logic
            LaunchedEffect(textFieldValue.selection, textLayoutResult) {
                val layout = textLayoutResult ?: return@LaunchedEffect
                val selection = textFieldValue.selection
                if (selection.collapsed) {
                    val cursorIndex = selection.start
                    val line = layout.getLineForOffset(cursorIndex)
                    val lineTop = layout.getLineTop(line)
                    val lineBottom = layout.getLineBottom(line)
                    
                    val lineTopPx = with(density) { lineTop }
                    val lineBottomPx = with(density) { lineBottom }
                    val halfViewportPx = with(density) { halfViewportHeight.toPx() }
                    
                    // Desired scroll position: line center should be at halfViewportPx
                    val lineCenterPx = (lineTopPx + lineBottomPx) / 2
                    val targetScrollPx = lineCenterPx // relative to BasicTextField start
                    
                    // The Column starts with a halfViewportPx spacer
                    val absoluteTargetScrollPx = targetScrollPx.toInt()
                    
                    if (scrollState.value != absoluteTargetScrollPx) {
                        scrollState.scrollTo(absoluteTargetScrollPx)
                    }
                }
            }
        }
    }
}
