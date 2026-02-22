package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopwatchApp()
        }
    }
}

@Composable
fun StopwatchApp() {

    var seconds by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    val time = String.format("%02d:%02d:%02d", hours, minutes, secs)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = time, fontSize = 40.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Button(onClick = { running = true }) {
                Text("Start")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(onClick = { running = false }) {
                Text("Stop")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(onClick = {
                running = false
                seconds = 0
            }) {
                Text("Reset")
            }
        }
    }
}