package com.remotelamp.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

/**
 * DualLampControlScreen
 * Screen untuk kontrol 2 LED secara terpisah
 *
 * Fitur:
 * - Control LED 1 (ON1/OFF1)
 * - Control LED 2 (ON2/OFF2)
 * - Visual indicator untuk masing-masing LED
 * - Real-time connection monitoring
 *
 * Command protocol:
 * - "ON1" → LED 1 ON
 * - "OFF1" → LED 1 OFF
 * - "ON2" → LED 2 ON
 * - "OFF2" → LED 2 OFF
 */
@Composable
fun DualLampControlScreen(
    bluetoothController: BluetoothController,
    onBack: () -> Unit,
    onExit: () -> Unit
) {
    // State untuk kedua lampu
    var lamp1Status by remember { mutableStateOf(false) } // false = OFF, true = ON
    var lamp2Status by remember { mutableStateOf(false) }
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
            delay(1000)
        }
    }

    // Background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D1117),
            Color(0xFF1B2735),
            Color(0xFF263238)
        )
    )

    // Animasi warna untuk lampu 1
    val lamp1Color by animateColorAsState(
        targetValue = if (lamp1Status) Color(0xFFFFC107) else Color(0xFF455A64),
        animationSpec = tween(durationMillis = 500), label = ""
    )

    // Animasi warna untuk lampu 2
    val lamp2Color by animateColorAsState(
        targetValue = if (lamp2Status) Color(0xFF4285F4) else Color(0xFF455A64),
        animationSpec = tween(durationMillis = 500), label = ""
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
                    text = "Dual Lamp Control",
                    color = Color.White,
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
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // === LAMP INDICATORS ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Lamp 1 Indicator
                LampIndicator(
                    lampNumber = 1,
                    isOn = lamp1Status,
                    color = lamp1Color
                )

                // Lamp 2 Indicator
                LampIndicator(
                    lampNumber = 2,
                    isOn = lamp2Status,
                    color = lamp2Color
                )
            }

            // === CONTROL SECTION ===
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // === LAMP 1 CONTROLS ===
                Text(
                    text = "Lamp 1 (Yellow)",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lamp 1 - TURN ON
                    Button(
                        onClick = {
                            if (bluetoothController.isConnected) {
                                val success = bluetoothController.sendCommand("ON1\n")
                                if (success) {
                                    lamp1Status = true
                                    toastMessage = "Lamp 1 turned ON"
                                    showToast = true
                                } else {
                                    toastMessage = "Failed to send command"
                                    showToast = true
                                }
                            } else {
                                toastMessage = "Device not connected"
                                showToast = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107),
                            disabledContainerColor = Color(0xFFFFC107).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !lamp1Status
                    ) {
                        Text(
                            text = "ON",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Lamp 1 - TURN OFF
                    Button(
                        onClick = {
                            if (bluetoothController.isConnected) {
                                val success = bluetoothController.sendCommand("OFF1\n")
                                if (success) {
                                    lamp1Status = false
                                    toastMessage = "Lamp 1 turned OFF"
                                    showToast = true
                                } else {
                                    toastMessage = "Failed to send command"
                                    showToast = true
                                }
                            } else {
                                toastMessage = "Device not connected"
                                showToast = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF455A64),
                            disabledContainerColor = Color(0xFF455A64).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = lamp1Status
                    ) {
                        Text(
                            text = "OFF",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === LAMP 2 CONTROLS ===
                Text(
                    text = "Lamp 2 (Blue)",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lamp 2 - TURN ON
                    Button(
                        onClick = {
                            if (bluetoothController.isConnected) {
                                val success = bluetoothController.sendCommand("ON2\n")
                                if (success) {
                                    lamp2Status = true
                                    toastMessage = "Lamp 2 turned ON"
                                    showToast = true
                                } else {
                                    toastMessage = "Failed to send command"
                                    showToast = true
                                }
                            } else {
                                toastMessage = "Device not connected"
                                showToast = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4285F4),
                            disabledContainerColor = Color(0xFF4285F4).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !lamp2Status
                    ) {
                        Text(
                            text = "ON",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Lamp 2 - TURN OFF
                    Button(
                        onClick = {
                            if (bluetoothController.isConnected) {
                                val success = bluetoothController.sendCommand("OFF2\n")
                                if (success) {
                                    lamp2Status = false
                                    toastMessage = "Lamp 2 turned OFF"
                                    showToast = true
                                } else {
                                    toastMessage = "Failed to send command"
                                    showToast = true
                                }
                            } else {
                                toastMessage = "Device not connected"
                                showToast = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF455A64),
                            disabledContainerColor = Color(0xFF455A64).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = lamp2Status
                    ) {
                        Text(
                            text = "OFF",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === NAVIGATION BUTTONS ===
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("← Back", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = onExit,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
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

/**
 * LampIndicator
 * Visual indicator untuk status lampu (ON/OFF)
 */
@Composable
fun LampIndicator(
    lampNumber: Int,
    isOn: Boolean,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(
                    elevation = if (isOn) 20.dp else 5.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isOn) "💡" else "⚫",
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isOn) "ON" else "OFF",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Lamp $lampNumber",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

