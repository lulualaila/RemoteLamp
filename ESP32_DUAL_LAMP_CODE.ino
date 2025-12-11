#include "BluetoothSerial.h"

/**
 * ESP32 Dual Lamp Control - Enhanced Version
 *
 * Mendukung kontrol 2 LED secara individual atau sekaligus
 *
 * Perintah yang diterima:
 * - "ON1"  : Nyalakan LED 1
 * - "OFF1" : Matikan LED 1
 * - "ON2"  : Nyalakan LED 2
 * - "OFF2" : Matikan LED 2
 * - "STATUS" : Kirim status kedua LED
 *
 * Format response status: "STATUS:LED1=ON,LED2=OFF"
 */

BluetoothSerial SerialBT;

// Pin untuk dua LED
const int ledPin1 = 13;   // LED pertama (GPIO 13)
const int ledPin2 = 14;   // LED kedua (GPIO 14)

// Konfigurasi PWM untuk ESP32 versi baru (ESP32-C3, S3, etc)
// Jika menggunakan ESP32 classic, gunakan ledcSetup/ledcAttachPin
const int pwmFreq = 5000;
const int pwmResolution = 8;

// State LED
bool led1State = false;
bool led2State = false;

String inputData = "";

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_DualLED");
  Serial.println("Bluetooth siap! Nama perangkat: ESP32_DualLED");

  // Setup PWM untuk LED (compatible dengan ESP32 versi baru)
  // Untuk ESP32 classic, gunakan kode lama dengan ledcSetup
  #if defined(CONFIG_IDF_TARGET_ESP32)
    // ESP32 Classic - menggunakan cara lama
    ledcSetup(0, pwmFreq, pwmResolution);
    ledcAttachPin(ledPin1, 0);
    ledcSetup(1, pwmFreq, pwmResolution);
    ledcAttachPin(ledPin2, 1);
  #else
    // ESP32-C3, S2, S3, etc - menggunakan cara baru
    ledcAttach(ledPin1, pwmFreq, pwmResolution);
    ledcAttach(ledPin2, pwmFreq, pwmResolution);
  #endif

  // Matikan LED awal
  setLED1(false);
  setLED2(false);

  Serial.println("Setup selesai. Siap menerima perintah...");
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

/**
 * Set LED 1
 */
void setLED1(bool state) {
  led1State = state;
  #if defined(CONFIG_IDF_TARGET_ESP32)
    ledcWrite(0, state ? 255 : 0);
  #else
    ledcWrite(ledPin1, state ? 255 : 0);
  #endif
}

/**
 * Set LED 2
 */
void setLED2(bool state) {
  led2State = state;
  #if defined(CONFIG_IDF_TARGET_ESP32)
    ledcWrite(1, state ? 255 : 0);
  #else
    ledcWrite(ledPin2, state ? 255 : 0);
  #endif
}

/**
 * Kirim status ke aplikasi Android
 */
void sendStatus() {
  String status = "STATUS:LED1=";
  status += led1State ? "ON" : "OFF";
  status += ",LED2=";
  status += led2State ? "ON" : "OFF";
  SerialBT.println(status);
  Serial.println("Status sent: " + status);
}

/**
 * Handle perintah dari aplikasi Android
 */
void handleCommand(String cmd) {
  cmd.trim();
  cmd.toUpperCase();

  Serial.println("Received: " + cmd);

  // LED 1 Control
  if (cmd == "ON1") {
    setLED1(true);
    SerialBT.println("LED1:ON");
    Serial.println("LED 1 ON");
  }
  else if (cmd == "OFF1") {
    setLED1(false);
    SerialBT.println("LED1:OFF");
    Serial.println("LED 1 OFF");
  }

  // LED 2 Control
  else if (cmd == "ON2") {
    setLED2(true);
    SerialBT.println("LED2:ON");
    Serial.println("LED 2 ON");
  }
  else if (cmd == "OFF2") {
    setLED2(false);
    SerialBT.println("LED2:OFF");
    Serial.println("LED 2 OFF");
  }

  // Request Status
  else if (cmd == "STATUS") {
    sendStatus();
  }

  // Unknown Command
  else {
    SerialBT.println("ERROR:UNKNOWN_COMMAND");
    Serial.println("Perintah tidak dikenal: " + cmd);
  }
}

