package com.remotelamp.app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.remotelamp.app.ui.theme.RemoteLampTheme

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothController: EnhancedBluetoothController
    private lateinit var viewModel: LampControlViewModel

    // Launcher untuk request permission Bluetooth
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(this, "Bluetooth permission diperlukan!", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher untuk enable Bluetooth
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Toast.makeText(this, "Bluetooth harus diaktifkan!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi BluetoothController
        bluetoothController = EnhancedBluetoothController(this)

        // Inisialisasi ViewModel dengan factory
        viewModel = ViewModelProvider(
            this,
            LampControlViewModelFactory(bluetoothController)
        )[LampControlViewModel::class.java]

        // Cek dan request permission
        checkBluetoothPermissions()

        setContent {
            RemoteLampTheme {
                EnhancedRemoteLampApp(
                    activity = this,
                    viewModel = viewModel
                )
            }
        }
    }

    // Cek dan request permission Bluetooth
    private fun checkBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

        val allPermissionsGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
        }

        if (!allPermissionsGranted) {
            requestPermissionLauncher.launch(permissions)
        }

        checkBluetoothEnabled()
    }

    // Cek dan request untuk enable Bluetooth
    @Suppress("DEPRECATION")
    private fun checkBluetoothEnabled() {
        if (!bluetoothController.isBluetoothEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }

            enableBluetoothLauncher.launch(enableBtIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Update connection status saat app di-resume
        viewModel.updateConnectionStatus()

        // Jika connected, refresh status lampu
        if (bluetoothController.isConnected) {
            viewModel.refreshLampStatus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup akan di-handle oleh ViewModel.onCleared()
    }
}

// Composable utama aplikasi dengan navigasi
@Composable
fun EnhancedRemoteLampApp(
    activity: MainActivity,
    viewModel: LampControlViewModel
) {
    var currentScreen by remember { mutableStateOf("connect") }

    when (currentScreen) {
        "connect" -> {
            EnhancedConnectScreen(
                viewModel = viewModel,
                onNavigateToControl = { currentScreen = "control" }
            )
        }
        "control" -> {
            EnhancedControlScreen(
                viewModel = viewModel,
                onBack = { currentScreen = "connect" },
                onDisconnect = { currentScreen = "connect" }
            )
        }
    }
}

// ViewModelFactory untuk inject BluetoothController ke ViewModel
class LampControlViewModelFactory(
    private val bluetoothController: EnhancedBluetoothController
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LampControlViewModel::class.java)) {
            return LampControlViewModel(bluetoothController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

