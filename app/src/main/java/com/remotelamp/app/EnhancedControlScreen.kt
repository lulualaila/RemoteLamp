package com.remotelamp.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.remotelamp.app.ui.theme.ErrorRed
import com.remotelamp.app.ui.theme.LampOffSilver
import com.remotelamp.app.ui.theme.LampOnGolden
import com.remotelamp.app.ui.theme.SuccessGreen

@Composable
fun EnhancedControlScreen(
    viewModel: LampControlViewModel,
    onBack: () -> Unit,
    onDisconnect: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto refresh status
    LaunchedEffect(Unit) {
        viewModel.refreshLampStatus()
    }

    Scaffold(
        topBar = {
            UltraModernTopBar(
                deviceName = uiState.deviceName,
                isConnected = uiState.isConnected,
                onBack = onBack,
                onDisconnect = {
                    viewModel.disconnect()
                    onDisconnect()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                HeroLampIllustration(
                    lamp1On = uiState.lamp1Status,
                    lamp2On = uiState.lamp2Status
                )

                Spacer(modifier = Modifier.height(36.dp))


                Text(
                    text = "Smart Lamp Controller",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Control your home lighting with ease",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                PremiumLampCard(
                    lampNumber = 1,
                    isOn = uiState.lamp1Status,
                    onToggle = { viewModel.toggleLamp1(it) },
                    isConnected = uiState.isConnected
                )

                Spacer(modifier = Modifier.height(18.dp))

                PremiumLampCard(
                    lampNumber = 2,
                    isOn = uiState.lamp2Status,
                    onToggle = { viewModel.toggleLamp2(it) },
                    isConnected = uiState.isConnected
                )

                Spacer(modifier = Modifier.height(26.dp))

                AllLampsPremiumCard(
                    allOn = uiState.lamp1Status && uiState.lamp2Status,
                    onToggle = { viewModel.toggleAllLamps(it) },
                    isConnected = uiState.isConnected
                )

                Spacer(modifier = Modifier.height(18.dp))

                SubtleRefreshButton(
                    onClick = { viewModel.refreshLampStatus() },
                    isEnabled = uiState.isConnected
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            // Loading overlay
            if (uiState.isLoading) {
                ModernLoadingOverlay()
            }

            // Error snackbar
            uiState.errorMessage?.let { error ->
                ModernErrorSnackbar(
                    message = error,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltraModernTopBar(
    deviceName: String,
    isConnected: Boolean,
    onBack: () -> Unit,
    onDisconnect: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isConnected) deviceName else "Disconnected",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) SuccessGreen else ErrorRed)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = if (isConnected) "Connected" else "Offline",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(
                onClick = onDisconnect,
                enabled = isConnected
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Disconnect",
                    tint = if (isConnected) ErrorRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        },
        colors = topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun HeroLampIllustration(
    lamp1On: Boolean,
    lamp2On: Boolean
) {
    val activeCount = listOf(lamp1On, lamp2On).count { it }
    
    val heroColor by animateColorAsState(
        targetValue = when (activeCount) {
            2 -> LampOnGolden.copy(alpha = 0.18f)
            1 -> LampOnGolden.copy(alpha = 0.10f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        },
        animationSpec = tween(600), label = "heroColor"
    )

    // Glow breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "heroGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (activeCount > 0) 0.7f else 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glowAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = heroColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Glow effect
            if (activeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(
                            LampOnGolden.copy(alpha = glowAlpha)
                        )
                )
            }

            // Two animated lamp icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedLampIconHero(isOn = lamp1On)
                AnimatedLampIconHero(isOn = lamp2On)
            }
        }
    }
}

@Composable
fun AnimatedLampIconHero(isOn: Boolean) {
    val lampScale by animateFloatAsState(
        targetValue = if (isOn) 1.12f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "lampScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isOn) LampOnGolden else LampOffSilver,
        animationSpec = tween(350), label = "lampColor"
    )

    Icon(
        imageVector = Icons.Filled.Lightbulb,
        contentDescription = null,
        modifier = Modifier
            .size(80.dp)
            .scale(lampScale),
        tint = iconColor
    )
}

@Composable
fun PremiumLampCard(
    lampNumber: Int,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    isConnected: Boolean
) {
    val cardColor by animateColorAsState(
        targetValue = if (isOn)
            LampOnGolden.copy(alpha = 0.12f)
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(350), label = "cardColor"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isOn) 10.dp else 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon & Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Icon container
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(
                            if (isOn) LampOnGolden.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surface
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = if (isOn) LampOnGolden else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Text info
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Lamp $lampNumber",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    PremiumStatusBadge(isOn = isOn)
                }
            }

            // Right: Modern switch
            UltraModernSwitch(
                checked = isOn,
                onCheckedChange = onToggle,
                enabled = isConnected
            )
        }
    }
}

@Composable
fun PremiumStatusBadge(isOn: Boolean) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isOn) SuccessGreen.copy(alpha = 0.16f) else Color.Transparent,
        border = BorderStroke(
            width = 1.2.dp,
            color = if (isOn) SuccessGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isOn) SuccessGreen else LampOffSilver)
            )
            Text(
                text = if (isOn) "Active" else "Inactive",
                style = MaterialTheme.typography.labelMedium,
                color = if (isOn) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun UltraModernSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = SuccessGreen,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = MaterialTheme.colorScheme.outline,
            disabledCheckedThumbColor = Color.White.copy(alpha = 0.5f),
            disabledCheckedTrackColor = SuccessGreen.copy(alpha = 0.3f),
            disabledUncheckedThumbColor = Color.White.copy(alpha = 0.5f),
            disabledUncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )
}

@Composable
fun AllLampsPremiumCard(
    allOn: Boolean,
    onToggle: (Boolean) -> Unit,
    isConnected: Boolean
) {
    val gradientColors = if (allOn) {
        listOf(
            LampOnGolden.copy(alpha = 0.22f),
            SuccessGreen.copy(alpha = 0.18f)
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant
        )
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (allOn) 14.dp else 5.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(gradientColors)
                )
                .padding(26.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                if (allOn) Color.White.copy(alpha = 0.92f)
                                else MaterialTheme.colorScheme.surface
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = if (allOn) LampOnGolden else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Text
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            text = "All Lamps",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (allOn) "All lights are on" else "Control all at once",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }
                }

                // Right: Switch
                UltraModernSwitch(
                    checked = allOn,
                    onCheckedChange = onToggle,
                    enabled = isConnected
                )
            }
        }
    }
}

@Composable
fun SubtleRefreshButton(
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Refresh Status",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ModernLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.5.dp
                )
                Text(
                    text = "Processing...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BoxScope.ModernErrorSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(18.dp),
        shape = RoundedCornerShape(18.dp),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        action = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", fontWeight = FontWeight.Bold)
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

