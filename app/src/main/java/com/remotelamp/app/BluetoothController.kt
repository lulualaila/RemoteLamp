package com.remotelamp.app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.OutputStream
import java.util.*

@Suppress("DEPRECATION")
class BluetoothController(private val context: Context) {

    // BluetoothAdapter untuk mengakses Bluetooth hardware
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    // Socket untuk komunikasi dengan device
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    // UUID standar untuk SPP (Serial Port Profile)
    // UUID ini harus sama dengan yang digunakan di ESP32
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // Status koneksi
    var isConnected: Boolean = false
        private set

    var connectedDeviceName: String = ""
        private set

    //Cek apakah Bluetooth tersedia di device
    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }

    //Cek apakah Bluetooth sedang aktif
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    // Mendapatkan daftar device Bluetooth yang sudah di-pair
    // Memerlukan permission BLUETOOTH_CONNECT di Android 12+

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDevice> {
        // Cek permission terlebih dahulu
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return emptyList()
        }

        return bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }

    // Melakukan koneksi ke device Bluetooth berdasarkan MAC address
    // @param deviceAddress MAC address device (format: XX:XX:XX:XX:XX:XX)
    // @return true jika koneksi berhasil, false jika gagal

    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String): Boolean {
        // Cek permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        try {
            // Disconnect koneksi sebelumnya jika ada
            disconnect()

            // Dapatkan device berdasarkan MAC address
            val device: BluetoothDevice = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                ?: return false

            // Simpan nama device
            connectedDeviceName = device.name ?: "Unknown Device"

            // Cancel discovery untuk menghemat resource
            bluetoothAdapter?.cancelDiscovery()

            // Buat socket RFCOMM menggunakan UUID SPP
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)

            // Lakukan koneksi (blocking call)
            bluetoothSocket?.connect()

            // Dapatkan OutputStream untuk mengirim data
            outputStream = bluetoothSocket?.outputStream

            isConnected = true
            return true

        } catch (e: IOException) {
            e.printStackTrace()
            isConnected = false
            disconnect() // Cleanup jika gagal
            return false
        }
    }

    //Mengirim perintah ke ESP32 via Bluetooth
    // @param command String perintah ("1" untuk ON, "0" untuk OFF)
    // @return true jika berhasil kirim, false jika gagal

    fun sendCommand(command: String): Boolean {
        if (!isConnected || outputStream == null) {
            return false
        }

        try {
            // Kirim command sebagai byte array
            outputStream?.write(command.toByteArray())
            outputStream?.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            // Jika error, berarti koneksi terputus
            isConnected = false
            return false
        }
    }

    //Memutus koneksi Bluetooth
    fun disconnect() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream = null
            bluetoothSocket = null
            isConnected = false
            connectedDeviceName = ""
        }
    }
}
