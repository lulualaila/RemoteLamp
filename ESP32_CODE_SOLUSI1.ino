/*
 * ===================================================================
 * ESP32 DUAL LED CONTROLLER - COMPATIBLE DENGAN APLIKASI REMOTELAMP
 * ===================================================================
 *
 * Kode ini disesuaikan dengan aplikasi Android RemoteLamp yang sudah dibuat.
 * Kedua LED akan dikontrol bersamaan (ON/OFF bersamaan).
 *
 * HARDWARE:
 * - ESP32 Dev Board
 * - 2x LED dengan resistor 220Ω
 *
 * WIRING:
 * ESP32 Pin 13 → Resistor 220Ω → LED1 (anode +) → LED1 (cathode -) → GND
 * ESP32 Pin 14 → Resistor 220Ω → LED2 (anode +) → LED2 (cathode -) → GND
 *
 * BLUETOOTH:
 * - Nama Device: ESP32_LAMP (disesuaikan dengan aplikasi Android)
 * - Protocol: Single character command
 * - Command: '1' = ON (kedua LED nyala)
 *            '0' = OFF (kedua LED mati)
 *
 * CARA UPLOAD:
 * 1. Buka Arduino IDE
 * 2. File → Open → Pilih file ini
 * 3. Tools → Board → ESP32 Dev Module
 * 4. Tools → Port → [Pilih port ESP32 Anda]
 * 5. Klik Upload (→)
 *
 * CARA PAIRING:
 * 1. Android Settings → Bluetooth → Nyalakan
 * 2. Cari "ESP32_LAMP"
 * 3. Klik Pair (PIN: 1234 jika diminta)
 *
 * CARA TEST:
 * 1. Buka aplikasi RemoteLamp
 * 2. Klik "Connect Device" → Pilih "ESP32_LAMP"
 * 3. Klik "TURN ON" → Kedua LED nyala 💡💡
 * 4. Klik "TURN OFF" → Kedua LED mati
 *
 * Dibuat oleh: Nazila
 * Tanggal: 14 November 2025
 * Untuk: Tugas WMC (Web and Mobile Computing)
 * ===================================================================
 */

#include "BluetoothSerial.h"

// Cek apakah Bluetooth tersedia
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to enable it
#endif

BluetoothSerial SerialBT;

// ===================================================================
// KONFIGURASI PIN
// ===================================================================
const int ledPin1 = 13;   // LED pertama (Pin GPIO 13)
const int ledPin2 = 14;   // LED kedua (Pin GPIO 14)

// ===================================================================
// KONFIGURASI PWM
// ===================================================================
// Menggunakan PWM untuk kontrol brightness (jika diperlukan nanti)
const int pwmChannel1 = 0;     // PWM channel untuk LED 1
const int pwmChannel2 = 1;     // PWM channel untuk LED 2
const int pwmFreq = 5000;      // Frequency 5kHz
const int pwmResolution = 8;   // 8-bit resolution (0-255)

// ===================================================================
// SETUP - Dijalankan sekali saat ESP32 boot
// ===================================================================
void setup() {
  // Inisialisasi Serial Monitor untuk debugging
  Serial.begin(115200);

  // Cetak header
  Serial.println("\n\n===================================");
  Serial.println("  ESP32 DUAL LED CONTROLLER");
  Serial.println("  Compatible with RemoteLamp App");
  Serial.println("===================================");

  // Inisialisasi Bluetooth dengan nama "ESP32_LAMP"
  // PENTING: Nama ini harus sama dengan yang dicari di aplikasi Android
  SerialBT.begin("ESP32_LAMP");

  Serial.println("✅ Bluetooth Initialized");
  Serial.println("📱 Device Name: ESP32_LAMP");
  Serial.println("⏳ Waiting for connection...");
  Serial.println("===================================\n");

  // Setup PWM untuk LED 1
  ledcSetup(pwmChannel1, pwmFreq, pwmResolution);
  ledcAttachPin(ledPin1, pwmChannel1);

  // Setup PWM untuk LED 2
  ledcSetup(pwmChannel2, pwmFreq, pwmResolution);
  ledcAttachPin(ledPin2, pwmChannel2);

  // Matikan kedua LED saat startup
  ledcWrite(pwmChannel1, 0);
  ledcWrite(pwmChannel2, 0);

  Serial.println("✅ LED 1 initialized (Pin 13) - OFF");
  Serial.println("✅ LED 2 initialized (Pin 14) - OFF");
  Serial.println("\n📡 Ready to receive commands!\n");
}

// ===================================================================
// LOOP - Dijalankan berulang-ulang
// ===================================================================
void loop() {
  // Cek apakah ada data dari Bluetooth
  if (SerialBT.available()) {
    char command = SerialBT.read();

    // Cetak command yang diterima untuk debugging
    Serial.print("📥 Received command: '");
    Serial.print(command);
    Serial.println("'");

    // ===============================================================
    // COMMAND '1' = TURN ON
    // ===============================================================
    if (command == '1') {
      // Nyalakan kedua LED dengan brightness penuh (255)
      ledcWrite(pwmChannel1, 255);
      ledcWrite(pwmChannel2, 255);

      // Feedback ke Serial Monitor
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
      Serial.println("💡 LAMP ON");
      Serial.println("✅ LED 1 (Pin 13): ON");
      Serial.println("✅ LED 2 (Pin 14): ON");
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

      // Feedback ke aplikasi Android via Bluetooth
      SerialBT.println("Lamp turned ON");
    }

    // ===============================================================
    // COMMAND '0' = TURN OFF
    // ===============================================================
    else if (command == '0') {
      // Matikan kedua LED
      ledcWrite(pwmChannel1, 0);
      ledcWrite(pwmChannel2, 0);

      // Feedback ke Serial Monitor
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
      Serial.println("🌙 LAMP OFF");
      Serial.println("❌ LED 1 (Pin 13): OFF");
      Serial.println("❌ LED 2 (Pin 14): OFF");
      Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

      // Feedback ke aplikasi Android via Bluetooth
      SerialBT.println("Lamp turned OFF");
    }

    // ===============================================================
    // COMMAND TIDAK DIKENAL
    // ===============================================================
    else {
      Serial.println("⚠️  WARNING: Unknown command!");
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

/*
 * ===================================================================
 * TROUBLESHOOTING
 * ===================================================================
 *
 * Problem: LED tidak menyala
 * Solusi:
 * - Cek wiring (pin, resistor, ground)
 * - Cek polaritas LED (kaki panjang = anode/+)
 * - Cek Serial Monitor apakah command diterima
 *
 * Problem: Bluetooth tidak terdeteksi
 * Solusi:
 * - Restart ESP32
 * - Cek Serial Monitor untuk error
 * - Pastikan Bluetooth enabled di Android
 *
 * Problem: Connection failed
 * Solusi:
 * - Unpair dan pair ulang
 * - Restart ESP32 dan Android
 * - Cek jarak (max 10 meter)
 *
 * Untuk panduan lengkap, lihat file:
 * PANDUAN_UPLOAD_ESP32.md
 *
 * ===================================================================
 */

