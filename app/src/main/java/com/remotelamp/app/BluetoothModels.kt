package com.remotelamp.app

data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val isPaired: Boolean = false
)

data class LampStatus(
    val lamp1: Boolean,
    val lamp2: Boolean
)
