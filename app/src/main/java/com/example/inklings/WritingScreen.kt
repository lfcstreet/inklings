package com.example.inklings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inklings.ui.theme.CourierPrime

@Composable
fun WritingScreen() {
    var text by rememberSaveable { mutableStateOf("") }

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
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            textStyle = TextStyle(
                fontFamily = CourierPrime,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
