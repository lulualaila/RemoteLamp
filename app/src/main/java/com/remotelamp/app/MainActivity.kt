package com.remotelamp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.remotelamp.app.ui.theme.RemoteLampTheme
import androidx.compose.material.icons.filled.PowerSettingsNew

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemoteLampTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") { LandingScreen(navController) }
                    composable("control") { ControlScreen() }
                }
            }
        }
    }
}

@Composable
fun LandingScreen(navController: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2E0B5A)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
                verticalArrangement = Arrangement.Center) {
                Text("Remote", fontSize = 48.sp, color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)
                Text("Control", fontSize = 48.sp, color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("Lamp", fontSize = 40.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(30.dp))
                Button(onClick = { navController.navigate("control") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7C4DFF))
                ) {
                    Text("Start Scan for Bluetooth", color = Color.White)
                }
            }
            // small FAB top-right
            Box(modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFB388FF))
                .clickable { /* placeholder */ }) {
            }
        }
    }
}

@Composable
fun ControlScreen() {
    var isOn by remember { mutableStateOf(true) }
    var selectedColor by remember { mutableStateOf(LampColor.Yellow) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {

        // Header card
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(color = when (selectedColor) {
                LampColor.Yellow -> Color(0xFFFFD54F)
                LampColor.Purple -> Color(0xFF8E24AA)
                LampColor.Black -> Color(0xFF000000)
            }, shape = MaterialTheme.shapes.medium)
        ) {
            Text("Bedroom\nLamp",
                color = if (selectedColor == LampColor.Black) Color.White else Color.White,
                fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(24.dp))
            // bulb illustration placeholder (circle) - near center
            Box(modifier = Modifier
                .size(72.dp)
                .align(Alignment.Center)
                .background(Color.White.copy(alpha = 0.15f), shape = CircleShape))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Controls row
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {

            // Color buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                ColorCircle(LampColor.Purple, selected = selectedColor==LampColor.Purple) { selectedColor = LampColor.Purple }
                Spacer(Modifier.width(16.dp))
                ColorCircle(LampColor.Yellow, selected = selectedColor==LampColor.Yellow) { selectedColor = LampColor.Yellow }
                Spacer(Modifier.width(16.dp))
                ColorCircle(LampColor.Black, selected = selectedColor==LampColor.Black) { selectedColor = LampColor.Black }
            }

            // Power button
            IconButton(onClick = { isOn = !isOn },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (isOn) Color(0xFF000000) else Color(0xFFBDBDBD))) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Power",
                    tint = if (isOn) Color.White else Color.DarkGray
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Small status
        Text(
            text = "Status: " + (if (isOn) "ON" else "OFF"),
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}

enum class LampColor { Yellow, Purple, Black }

@Composable
fun ColorCircle(color: LampColor, selected: Boolean, onClick: () -> Unit) {
    val bg = when(color){
        LampColor.Yellow -> Color(0xFFFFD54F)
        LampColor.Purple -> Color(0xFF8E24AA)
        LampColor.Black -> Color(0xFF000000)
    }
    Box(modifier = Modifier
        .size(if (selected) 64.dp else 48.dp)
        .clip(CircleShape)
        .background(bg)
        .clickable { onClick() }
        .padding(6.dp),
        contentAlignment = Alignment.Center) {
        if (selected) {
            // outline ring
            Box(modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)))
        }
    }
}
