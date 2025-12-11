/*
 * ESP32 Dual LED Controller - SOLUSI 2 (Kode Asli Anda)
 *
 * Untuk digunakan dengan aplikasi Android yang support 2 LED terpisah
 *
 * Hardware:
 * - ESP32 Dev Board
 * - 2x LED dengan resistor 220Ω
 *
 * Wiring:
 * ESP32 Pin 13 → R220Ω → LED1 (anode +) → LED1 (cathode -) → GND
 * ESP32 Pin 14 → R220Ω → LED2 (anode +) → LED2 (cathode -) → GND
 *
 * Bluetooth:
 * - Nama device: ESP32_DualLED
 * - Protocol: String dengan newline terminator
 * - Command: ON1, OFF1, ON2, OFF2
 */

#include "BluetoothSerial.h"

BluetoothSerial SerialBT;

// Pin untuk dua LED
const int ledPin1 = 13;   // LED pertama
const int ledPin2 = 14;   // LED kedua

// Konfigurasi PWM
const int pwmChannel1 = 0;
const int pwmChannel2 = 1;
const int freq = 5000;
const int resolution = 8;

String inputData = "";

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_DualLED");
  Serial.println("Bluetooth siap! Nama perangkat: ESP32_DualLED");

  // Setup PWM untuk dua LED
  ledcSetup(pwmChannel1, freq, resolution);
  ledcAttachPin(ledPin1, pwmChannel1);

  ledcSetup(pwmChannel2, freq, resolution);
  ledcAttachPin(ledPin2, pwmChannel2);

  // Matikan LED awal
  ledcWrite(pwmChannel1, 0);
  ledcWrite(pwmChannel2, 0);
}

void loop() {
  if (SerialBT.available()) {
    char c = SerialBT.read();

    if (c == '\n' || c == '\r') {
      if (inputData.length() > 0) {
        handleCommand(inputData);
        inputData = "";
      }
    } else {
      inputData += c;
    }
  }
}

void handleCommand(String cmd) {
  cmd.trim();
  cmd.toUpperCase();

  // LED 1 ON/OFF
  if (cmd == "ON1") {
    ledcWrite(pwmChannel1, 255);
    SerialBT.println("LED 1 ON");
    Serial.println("✅ LED 1 ON");
  }
  else if (cmd == "OFF1") {
    ledcWrite(pwmChannel1, 0);
    SerialBT.println("LED 1 OFF");
    Serial.println("❌ LED 1 OFF");
  }

  // LED 2 ON/OFF
  else if (cmd == "ON2") {
    ledcWrite(pwmChannel2, 255);
    SerialBT.println("LED 2 ON");
    Serial.println("✅ LED 2 ON");
  }
  else if (cmd == "OFF2") {
    ledcWrite(pwmChannel2, 0);
    SerialBT.println("LED 2 OFF");
    Serial.println("❌ LED 2 OFF");
  }

  else {
    SerialBT.println("Perintah tidak dikenal!");
    Serial.println("⚠️ Perintah tidak dikenal: " + cmd);
  }
}

