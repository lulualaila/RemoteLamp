package com.remotelamp.app

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@Composable
fun EnhancedConnectScreen(
    viewModel: LampControlViewModel,
    onNavigateToControl: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isConnected) {
        if (uiState.isConnected && !uiState.showConnectionDialog) {
            delay(500)
            onNavigateToControl()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glowScale by infiniteTransition.animateFloat(
        1f, 1.2f,
        infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1117), Color(0xFF1B2735))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Smart Dual Lamp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Controller",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF90CAF9)
            )

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(glowScale)
                    .background(
                        Color(0xFF4285F4).copy(alpha = 0.28f),
                        RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Bluetooth,
                    contentDescription = null,
                    tint = Color(0xFF4285F4),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color(0xFF1E2933))
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        if (uiState.isConnected) Icons.Filled.CheckCircle else Icons.AutoMirrored.Filled.BluetoothSearching,
                        null,
                        tint = if (uiState.isConnected) Color(0xFF4CAF50) else Color(0xFF90CAF9),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            if (uiState.isConnected) "Connected" else "Not Connected",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (uiState.isConnected) {
                            Text(
                                uiState.deviceName,
                                color = Color(0xFF4CAF50),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.setShowConnectionDialog(true) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4285F4)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Bluetooth, null, Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    if (uiState.isConnected) "Change Device" else "Connect Device",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            if (uiState.isConnected) {
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onNavigateToControl,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Go to Control", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (uiState.showConnectionDialog) {
            DeviceSelectionDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowConnectionDialog(false) }
            )
        }

        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(Modifier.height(16.dp))
                    Text("Connecting...", color = Color.White)
                }
            }
        }

        uiState.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = Color(0xFFD32F2F),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK", color = Color.White)
                    }
                }
            ) {
                Text(error, color = Color.White)
            }
        }
    }
}

@Composable
fun DeviceSelectionDialog(
    viewModel: LampControlViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startScan()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(Color(0xFF1E2933))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Device", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    IconButton(onClick = {
                        viewModel.stopScan()
                        onDismiss()
                    }) {
                        Icon(Icons.Filled.Close, null, tint = Color.White)
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (uiState.isScanning) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF4285F4)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Scanning...", color = Color(0xFF90CAF9))
                    }

                    Spacer(Modifier.height(18.dp))
                }

                if (uiState.availableDevices.isEmpty() && !uiState.isScanning) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(Icons.Filled.BluetoothDisabled, null, tint = Color(0xFF757575), modifier = Modifier.size(46.dp))

                        Spacer(Modifier.height(16.dp))

                        Text("No devices found", color = Color.White)
                        Text("Make sure device is in pairing mode", color = Color(0xFF90CAF9), fontSize = 12.sp)

                        Spacer(Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.startScan() },
                            colors = ButtonDefaults.buttonColors(Color(0xFF4285F4))
                        ) {
                            Text("Scan Again")
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(uiState.availableDevices) { device ->
                            ModernDeviceItem(
                                device = device,
                                isConnecting = uiState.isConnecting,
                                onClick = { viewModel.connectToDevice(device) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernDeviceItem(
    device: BluetoothDeviceInfo,
    isConnecting: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(enabled = !isConnecting, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            if (device.isPaired) Color(0xFF263238) else Color(0xFF37474F)
        )
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Bluetooth,
                null,
                tint = if (device.isPaired) Color(0xFF4CAF50) else Color(0xFF4285F4),
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(device.name.ifEmpty { "Unknown Device" }, color = Color.White, fontWeight = FontWeight.Bold)
                Text(device.address, color = Color(0xFF90CAF9), fontSize = 12.sp)
                if (device.isPaired) {
                    Text("Paired", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF4285F4)
                )
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    null,
                    tint = Color(0xFF90CAF9),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
