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

// Status lampu (untuk tracking)
bool lamp1Status = false;
bool lamp2Status = false;

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

  Serial.println("Setup complete!");
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

  Serial.print("Command received: ");
  Serial.println(cmd);

  // LAMP 1 CONTROL
  if (cmd == "ON1") {
    ledcWrite(pwmChannel1, 255);
    lamp1Status = true;
    SerialBT.println("LED 1 ON");
    Serial.println("LED 1 ON");
  }
  else if (cmd == "OFF1") {
    ledcWrite(pwmChannel1, 0);
    lamp1Status = false;
    SerialBT.println("LED 1 OFF");
    Serial.println("LED 1 OFF");
  }

  // LAMP 2 CONTROL
  else if (cmd == "ON2") {
    ledcWrite(pwmChannel2, 255);
    lamp2Status = true;
    SerialBT.println("LED 2 ON");
    Serial.println("LED 2 ON");
  }
  else if (cmd == "OFF2") {
    ledcWrite(pwmChannel2, 0);
    lamp2Status = false;
    SerialBT.println("LED 2 OFF");
    Serial.println("LED 2 OFF");
  }

  // ALL LAMPS CONTROL
  else if (cmd == "ONALL") {
    ledcWrite(pwmChannel1, 255);
    ledcWrite(pwmChannel2, 255);
    lamp1Status = true;
    lamp2Status = true;
    SerialBT.println("ALL LEDS ON");
    Serial.println("ALL LEDS ON");
  }
  else if (cmd == "OFFALL") {
    ledcWrite(pwmChannel1, 0);
    ledcWrite(pwmChannel2, 0);
    lamp1Status = false;
    lamp2Status = false;
    SerialBT.println("ALL LEDS OFF");
    Serial.println("ALL LEDS OFF");
  }

  // STATUS QUERY
  else if (cmd == "STATUS") {
    // Format response: "STATUS:1,0"
    // Artinya: Lamp1=ON(1), Lamp2=OFF(0)
    String statusResponse = "STATUS:";
    statusResponse += lamp1Status ? "1" : "0";
    statusResponse += ",";
    statusResponse += lamp2Status ? "1" : "0";

    SerialBT.println(statusResponse);
    Serial.println(statusResponse);
  }

  else {
    SerialBT.println("Perintah tidak dikenal!");
    Serial.println("Perintah tidak dikenal!");
  }
}

