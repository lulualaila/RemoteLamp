#include "BluetoothSerial.h"

BluetoothSerial SerialBT;

// Pin untuk dua LED
const int ledPin1 = 13;   // LED pertama
const int ledPin2 = 14;   // LED kedua

// Konfigurasi PWM
const int pwmChannel1 = 0;
const int pwmChannel2 = 1;
const int freq = 5000;
const int resolution = 10;

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
  }
  else if (cmd == "OFF1") {
    ledcWrite(pwmChannel1, 0);
    SerialBT.println("LED 1 OFF");
  }

  // LED 2 ON/OFF
  else if (cmd == "ON2") {
    ledcWrite(pwmChannel2, 255);
    SerialBT.println("LED 2 ON");
  }
  else if (cmd == "OFF2") {
    ledcWrite(pwmChannel2, 0);
    SerialBT.println("LED 2 OFF");
  }

  else {
    SerialBT.println("Perintah tidak dikenal!");
  }
}
