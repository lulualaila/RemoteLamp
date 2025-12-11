#include "BluetoothSerial.h"

// Cek apakah Bluetooth tersedia
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run make menuconfig to enable it
#endif

BluetoothSerial SerialBT;

// KONFIGURASI PIN
const int ledPin1 = 13;   // LED pertama (Pin GPIO 13)
const int ledPin2 = 14;   // LED kedua (Pin GPIO 14)

// KONFIGURASI PWM (untuk API baru ESP32 Core 3.x)
const int pwmFreq = 5000;      // Frequency 5kHz
const int pwmResolution = 8;   // 8-bit resolution (0-255)

// SETUP - Dijalankan sekali saat ESP32 boot
void setup() {
  // Inisialisasi Serial Monitor untuk debugging
  Serial.begin(115200);

  // Cetak header
  Serial.println("\n\n===================================");
  Serial.println("  ESP32 DUAL LED CONTROLLER");
  Serial.println("  Compatible with RemoteLamp App");
  Serial.println("===================================");

  // Inisialisasi Bluetooth dengan nama "ESP32_LAMP"
  // Nama ini harus sama dengan yang dicari di aplikasi Android
  SerialBT.begin("ESP32_LAMP");

  Serial.println("✅ Bluetooth Initialized");
  Serial.println("📱 Device Name: ESP32_LAMP");
  Serial.println("⏳ Waiting for connection...");
  Serial.println("===================================\n");

  // Setup PWM untuk LED menggunakan API baru (ESP32 Core 3.x)
  // Syntax baru: ledcAttach(pin, freq, resolution)
  ledcAttach(ledPin1, pwmFreq, pwmResolution);
  ledcAttach(ledPin2, pwmFreq, pwmResolution);

  // Matikan kedua LED saat startup
  // Syntax baru: ledcWrite(pin, value) - langsung pakai pin number
  ledcWrite(ledPin1, 0);
  ledcWrite(ledPin2, 0);

  Serial.println("✅ LED 1 initialized (Pin 13) - OFF");
  Serial.println("✅ LED 2 initialized (Pin 14) - OFF");
  Serial.println("\n📡 Ready to receive commands!\n");
}

// LOOP
void loop() {
  // Cek apakah ada data dari Bluetooth
  if (SerialBT.available()) {
    char command = SerialBT.read();

    // Cetak command yang diterima untuk debugging
    Serial.print("📥 Received command: '");
    Serial.print(command);
    Serial.println("'");

    // COMMAND '1' = TURN ON
    if (command == '1') {
      // Nyalakan kedua LED dengan brightness penuh (255)
      ledcWrite(ledPin1, 255);
      ledcWrite(ledPin2, 255);

      // Feedback ke Serial Monitor
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
      Serial.println("💡 LAMP ON");
      Serial.println("✅ LED 1 (Pin 13): ON");
      Serial.println("✅ LED 2 (Pin 14): ON");
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

      // Feedback ke aplikasi Android via Bluetooth
      SerialBT.println("Lamp turned ON");
    }

    // COMMAND '0' = TURN OFF
    else if (command == '0') {
      // Matikan kedua LED
      ledcWrite(ledPin1, 0);
      ledcWrite(ledPin2, 0);

      // Feedback ke Serial Monitor
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
      Serial.println("🌙 LAMP OFF");
      Serial.println("❌ LED 1 (Pin 13): OFF");
      Serial.println("❌ LED 2 (Pin 14): OFF");
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

      // Feedback ke aplikasi Android via Bluetooth
      SerialBT.println("Lamp turned OFF");
    }

    // COMMAND TIDAK DIKENAL
    else {
      Serial.println("⚠  WARNING: Unknown command!");
      Serial.print("   Received: '");
      Serial.print(command);
      Serial.print("' (ASCII: ");
      Serial.print((int)command);
      Serial.println(")");
      Serial.println("   Expected: '1' (ON) or '0' (OFF)\n");

      // Feedback ke aplikasi Android
      SerialBT.println("Unknown command");
    }
  }

  // Small delay untuk stability
  delay(20);
}
