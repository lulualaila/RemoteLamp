package com.remotelamp.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LampControlViewModel(
    private val bluetoothController: EnhancedBluetoothController
) : ViewModel() {

    // State untuk UI
    private val _uiState = MutableStateFlow(LampControlUiState())
    val uiState: StateFlow<LampControlUiState> = _uiState.asStateFlow()

    // Data class untuk menyimpan semua state UI
    // Enhanced untuk mendukung ultra modern screens
    data class LampControlUiState(
        val isConnected: Boolean = false,
        val deviceName: String = "",
        val lamp1Status: Boolean = false,
        val lamp2Status: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val showConnectionDialog: Boolean = false,
        // ✨ New properties for modern UI ✨
        val isScanning: Boolean = false,
        val isConnecting: Boolean = false,
        val connectingDeviceName: String? = null,
        val availableDevices: List<BluetoothDeviceInfo> = emptyList()
    )

    init {
        // Cek koneksi saat ViewModel dibuat
        updateConnectionStatus()
    }

    // Update status koneksi
    fun updateConnectionStatus() {
        _uiState.value = _uiState.value.copy(
            isConnected = bluetoothController.isConnected,
            deviceName = bluetoothController.connectedDeviceName
        )
    }

    // Connect ke device Bluetooth
    // Enhanced untuk modern UI dengan connecting state
    fun connectToDevice(device: BluetoothDeviceInfo) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isConnecting = true,
                connectingDeviceName = device.name,
                errorMessage = null
            )

            val success = withContext(Dispatchers.IO) {
                bluetoothController.connect(device.address)
            }

            if (success) {
                _uiState.value = _uiState.value.copy(
                    isConnected = true,
                    deviceName = bluetoothController.connectedDeviceName,
                    isConnecting = false,
                    connectingDeviceName = null,
                    showConnectionDialog = false
                )
                // Ambil status awal lampu setelah connect
                refreshLampStatus()
            } else {
                _uiState.value = _uiState.value.copy(
                    isConnected = false,
                    isConnecting = false,
                    connectingDeviceName = null,
                    errorMessage = "Gagal terhubung ke ${device.name}"
                )
            }
        }
    }

    // START BLUETOOTH DEVICE SCANNING
    fun startScan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                availableDevices = emptyList(),
                errorMessage = null
            )

            try {
                val devices = withContext(Dispatchers.IO) {
                    bluetoothController.scanForDevices()
                }

                _uiState.value = _uiState.value.copy(
                    availableDevices = devices,
                    isScanning = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    errorMessage = "Gagal melakukan scan: ${e.message}"
                )
            }
        }
    }

    // STOP BLUETOOTH DEVICE SCANNING
    fun stopScan() {
        bluetoothController.stopScan()
        _uiState.value = _uiState.value.copy(isScanning = false)
    }

    // Disconnect dari device
    fun disconnect() {
        bluetoothController.disconnect()
        _uiState.value = _uiState.value.copy(
            isConnected = false,
            deviceName = "",
            lamp1Status = false,
            lamp2Status = false
        )
    }

    // Toggle Lamp 1
    fun toggleLamp1(state: Boolean) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                bluetoothController.toggleLamp(1, state)
            }

            if (success) {
                _uiState.value = _uiState.value.copy(lamp1Status = state)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Gagal mengirim perintah ke Lamp 1"
                )
            }
        }
    }

    // Toggle Lamp 2
    fun toggleLamp2(state: Boolean) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                bluetoothController.toggleLamp(2, state)
            }

            if (success) {
                _uiState.value = _uiState.value.copy(lamp2Status = state)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Gagal mengirim perintah ke Lamp 2"
                )
            }
        }
    }

    // Toggle semua lampu sekaligus
    fun toggleAllLamps(state: Boolean) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                bluetoothController.toggleAllLamps(state)
            }

            if (success) {
                _uiState.value = _uiState.value.copy(
                    lamp1Status = state,
                    lamp2Status = state
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Gagal mengirim perintah ke semua lampu"
                )
            }
        }
    }

    // Refresh status lampu dari ESP32
    // Dipanggil saat app dibuka kembali atau setelah connect
    fun refreshLampStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val status = bluetoothController.getStatus()

            if (status != null) {
                _uiState.value = _uiState.value.copy(
                    lamp1Status = status.lamp1,
                    lamp2Status = status.lamp2,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal mengambil status lampu"
                )
            }
        }
    }

    // Show/hide connection dialog
    fun setShowConnectionDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showConnectionDialog = show)
    }

    // Clear error message
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Get paired devices
    fun getPairedDevices() = bluetoothController.getPairedDevices()

    // Cleanup saat ViewModel di-destroy
    override fun onCleared() {
        super.onCleared()
        bluetoothController.disconnect()
    }
}

