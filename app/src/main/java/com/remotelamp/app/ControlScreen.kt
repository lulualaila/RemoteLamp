package com.remotelamp.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ControlScreen(
    bluetoothController: BluetoothController,
    onBack: () -> Unit,
    onExit: () -> Unit
) {
    // State management
    var lampStatus by remember { mutableStateOf(false) } // false = OFF, true = ON
    var connectionStatus by remember { mutableStateOf("Connected") }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Cek koneksi status secara periodik
    LaunchedEffect(Unit) {
        while (true) {
            connectionStatus = if (bluetoothController.isConnected) {
                "Connected to ${bluetoothController.connectedDeviceName}"
            } else {
                "Disconnected"
            }
            delay(1000) // Cek setiap 1 detik
        }
    }

    // Background gradient berubah sesuai status lampu
    val backgroundGradient = Brush.verticalGradient(
        colors = if (lampStatus) {
            // Lampu ON - gradient kuning terang
            listOf(
                Color(0xFFFFF8E1),
                Color(0xFFFFECB3),
                Color(0xFFFFE082)
            )
        } else {
            // Lampu OFF - gradient gelap
            listOf(
                Color(0xFF0D1117),
                Color(0xFF1B2735),
                Color(0xFF263238)
            )
        }
    )

    // Animasi warna untuk icon lampu
    val lampColor by animateColorAsState(
        targetValue = if (lampStatus) Color(0xFFFFC107) else Color(0xFF455A64),
        animationSpec = tween(durationMillis = 500), label = ""
    )

    // Animasi glow untuk lampu ON
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glowSize by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (lampStatus) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // === HEADER SECTION ===
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Smart Lamp Control",
                    color = if (lampStatus) Color(0xFF1B2735) else Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Connection Status
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (bluetoothController.isConnected)
                        Color(0xFF4CAF50).copy(alpha = 0.2f)
                        else Color(0xFFFF5252).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = connectionStatus,
                        color = if (lampStatus) Color(0xFF1B2735) else Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // === LAMP VISUAL INDICATOR ===
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Glow effect saat lampu ON
                if (lampStatus) {
                    Canvas(
                        modifier = Modifier.size((220 * glowSize).dp)
                    ) {
                        drawCircle(
                            color = Color(0xFFFFC107).copy(alpha = 0.3f),
                            radius = size.width / 2
                        )
                    }
                }

                // Bulb icon
                Box(
                    modifier = Modifier
                        .size((180 * glowSize).dp)
                        .shadow(
                            elevation = if (lampStatus) 20.dp else 5.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(lampColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lampStatus) "💡" else "⚫",
                        fontSize = 80.sp
                    )
                }
            }

            // Status Text
            Text(
                text = if (lampStatus) "Lampu NYALA" else "Lampu MATI",
                color = if (lampStatus) Color(0xFF1B2735) else Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // === CONTROL BUTTONS ===
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tombol TURN ON
                Button(
                    onClick = {
                        if (bluetoothController.isConnected) {
                            val success = bluetoothController.sendCommand("1")
                            if (success) {
                                lampStatus = true
                                toastMessage = "Lampu dinyalakan"
                                showToast = true
                            } else {
                                toastMessage = "Gagal mengirim perintah"
                                showToast = true
                            }
                        } else {
                            toastMessage = "Device tidak terhubung"
                            showToast = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !lampStatus // Disable jika sudah ON
                ) {
                    Text(
                        text = "🔆 TURN ON",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol TURN OFF
                Button(
                    onClick = {
                        if (bluetoothController.isConnected) {
                            val success = bluetoothController.sendCommand("0")
                            if (success) {
                                lampStatus = false
                                toastMessage = "Lampu dimatikan"
                                showToast = true
                            } else {
                                toastMessage = "Gagal mengirim perintah"
                                showToast = true
                            }
                        } else {
                            toastMessage = "Device tidak terhubung"
                            showToast = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF455A64),
                        disabledContainerColor = Color(0xFF455A64).copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = lampStatus // Disable jika sudah OFF
                ) {
                    Text(
                        text = "🌙 TURN OFF",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === NAVIGATION BUTTONS ===
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Tombol Back
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (lampStatus) Color(0xFF1B2735) else Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("← Back", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Tombol Exit
                    OutlinedButton(
                        onClick = onExit,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (lampStatus) Color(0xFF1B2735) else Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Exit", fontSize = 16.sp)
                    }
                }
            }
        }

        // Toast notification
        if (showToast) {
            LaunchedEffect(Unit) {
                delay(2000)
                showToast = false
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = toastMessage,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

