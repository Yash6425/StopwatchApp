package com.example.myapplication

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PremiumStopwatch()
        }
    }
}

@Composable
fun PremiumStopwatch() {

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    var time by remember { mutableStateOf(0L) }
    var running by remember { mutableStateOf(false) }
    var laps by remember { mutableStateOf(listOf<String>()) }

    var inputSeconds by remember { mutableStateOf("") }
    var alertDuration by remember { mutableStateOf<Long?>(null) }
    var ringtone by remember { mutableStateOf<Ringtone?>(null) }

    LaunchedEffect(running) {
        while (running) {
            delay(10)
            time += 10

            alertDuration?.let {
                if (time >= it) {
                    running = false

                    val alarmUri =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ringtone = RingtoneManager.getRingtone(context, alarmUri)
                    ringtone?.play()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createWaveform(
                                longArrayOf(0, 500, 300, 500),
                                0
                            )
                        )
                    } else {
                        vibrator.vibrate(1000)
                    }
                }
            }
        }
    }

    val minutes = (time / 60000)
    val seconds = (time / 1000) % 60
    val millis = (time % 1000) / 10
    val formatted = String.format("%02d:%02d.%02d", minutes, seconds, millis)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F0C29),
                        Color(0xFF302B63),
                        Color(0xFF24243E)
                    )
                )
            )
    ) {

        ParticleBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Stopwatch Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(250.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Magenta,
                                Color.Cyan,
                                Color.Blue,
                                Color.Magenta
                            )
                        ),
                        style = Stroke(width = 20f)
                    )
                }

                Text(
                    text = formatted,
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // 🔥 WHITE ALERT INPUT SECTION
            OutlinedTextField(
                value = inputSeconds,
                onValueChange = { inputSeconds = it },
                label = {
                    Text(
                        "Set Alert Duration (seconds)",
                        color = Color.White
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            FancyButton("Set Alert", Color.Cyan) {
                inputSeconds.toLongOrNull()?.let {
                    alertDuration = it * 1000
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {

                FancyButton("Start", Color.Green) { running = true }

                FancyButton("Stop", Color.Red) { running = false }

                FancyButton("Lap", Color.Blue) {
                    if (running) laps = laps + formatted
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            FancyButton("Reset", Color.Magenta) {
                running = false
                time = 0
                laps = emptyList()
                ringtone?.stop()
                vibrator.cancel()
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Lap History", color = Color.White)

            LazyColumn(modifier = Modifier.height(120.dp)) {
                itemsIndexed(laps) { index, lap ->
                    Text(
                        text = "Lap ${index + 1}: $lap",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FancyButton(text: String, color: Color, onClick: () -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val buttonColor = if (pressed) Color.White else color
    val textColor = if (pressed) color else Color.Black

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(text, color = textColor)
    }
}

@Composable
fun ParticleBackground() {

    val infiniteTransition = rememberInfiniteTransition()

    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 15f + 10f
            )
        }
    }

    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val newY = (particle.y + animatedValue) % 1f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.6f),
                        Color.Cyan.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                radius = particle.radius,
                center = Offset(
                    particle.x * size.width,
                    newY * size.height
                )
            )
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float
)