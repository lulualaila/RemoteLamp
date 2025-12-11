package com.remotelamp.app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@Suppress("DEPRECATION")
class EnhancedBluetoothController(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    // UUID standar untuk SPP (Serial Port Profile)
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    var isConnected: Boolean = false
        private set

    var connectedDeviceName: String = ""
        private set

    //Data class untuk menyimpan status 2 lampu
    data class LampStatus(
        val lamp1: Boolean = false,
        val lamp2: Boolean = false
    )

    // Cek apakah Bluetooth tersedia di device
    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }

    // Cek apakah Bluetooth sedang aktif
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    // Mendapatkan daftar device Bluetooth yang sudah di-pair
    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDeviceInfo> {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return emptyList()
        }

        return bluetoothAdapter?.bondedDevices?.map { device ->
            BluetoothDeviceInfo(
                name = device.name ?: "Unknown Device",
                address = device.address,
                isPaired = true
            )
        } ?: emptyList()
    }

    //SCAN FOR AVAILABLE BLUETOOTH DEVICES
    @SuppressLint("MissingPermission")
    suspend fun scanForDevices(): List<BluetoothDeviceInfo> = withContext(Dispatchers.IO) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@withContext getPairedDevices() // Return paired devices if no scan permission
        }

        val discoveredDevices = mutableListOf<BluetoothDeviceInfo>()

        // Start with paired devices
        discoveredDevices.addAll(getPairedDevices())

        try {
            bluetoothAdapter?.startDiscovery()

            // Wait for discovery to find devices (simulate for now)
            // In real implementation, you'd register a BroadcastReceiver
            // for BluetoothDevice.ACTION_FOUND
            kotlinx.coroutines.delay(5000) // 5 second scan

            bluetoothAdapter?.cancelDiscovery()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext discoveredDevices.distinctBy { it.address }
    }

    //STOP DEVICE SCANNING
    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothAdapter?.cancelDiscovery()
        }
    }

    //Melakukan koneksi ke device Bluetooth
    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        try {
            disconnect()

            val device: BluetoothDevice = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                ?: return false

            connectedDeviceName = device.name ?: "Unknown Device"
            bluetoothAdapter?.cancelDiscovery()

            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothSocket?.connect()

            outputStream = bluetoothSocket?.outputStream
            inputStream = bluetoothSocket?.inputStream

            isConnected = true
            return true

        } catch (e: IOException) {
            e.printStackTrace()
            isConnected = false
            disconnect()
            return false
        }
    }

    /**
     * Mengirim perintah ke ESP32
     *
     * Command yang didukung:
     * - "ON1"    : Nyalakan Lamp 1
     * - "OFF1"   : Matikan Lamp 1
     * - "ON2"    : Nyalakan Lamp 2
     * - "OFF2"   : Matikan Lamp 2
     * - "ONALL"  : Nyalakan semua lampu
     * - "OFFALL" : Matikan semua lampu
     * - "STATUS" : Query status lampu
     */
    fun sendCommand(command: String): Boolean {
        if (!isConnected || outputStream == null) {
            return false
        }

        try {
            val commandWithNewline = "$command\n"
            outputStream?.write(commandWithNewline.toByteArray())
            outputStream?.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            isConnected = false
            return false
        }
    }

    /**
     * Fungsi reusable untuk toggle lampu individual
     *
     * @param lampId ID lampu (1 atau 2)
     * @param state true = ON, false = OFF
     */
    fun toggleLamp(lampId: Int, state: Boolean): Boolean {
        val command = if (state) "ON$lampId" else "OFF$lampId"
        return sendCommand(command)
    }

    //Fungsi untuk kontrol semua lampu sekaligus
    fun toggleAllLamps(state: Boolean): Boolean {
        val command = if (state) "ONALL" else "OFFALL"
        return sendCommand(command)
    }

    /**
     * Query status lampu dari ESP32
     * ESP32 akan mengirim response format: "STATUS:1,0"
     * Yang artinya Lamp1=ON, Lamp2=OFF
     * @return LampStatus atau null jika gagal
     */
    suspend fun getStatus(): LampStatus? = withContext(Dispatchers.IO) {
        if (!isConnected || inputStream == null) {
            return@withContext null
        }

        try {
            // Kirim command STATUS
            sendCommand("STATUS")

            // Tunggu dan baca response dari ESP32
            Thread.sleep(200) // Delay 200ms untuk ESP32 memproses

            val available = inputStream?.available() ?: 0
            if (available > 0) {
                val buffer = ByteArray(available)
                inputStream?.read(buffer)
                val response = String(buffer).trim()

                // Parse response format: "STATUS:1,0"
                if (response.startsWith("STATUS:")) {
                    val values = response.substring(7).split(",")
                    if (values.size == 2) {
                        val lamp1 = values[0].trim() == "1"
                        val lamp2 = values[1].trim() == "1"
                        return@withContext LampStatus(lamp1, lamp2)
                    }
                }
            }

            return@withContext null

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    //Disconnect dari device
    fun disconnect() {
        try {
            outputStream?.close()
            inputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream = null
            inputStream = null
            bluetoothSocket = null
            isConnected = false
            connectedDeviceName = ""
        }
    }
}

