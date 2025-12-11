# 📋 PANDUAN UPLOAD & TEST ESP32

## 🎯 YANG BERUBAH DARI KODE ANDA:

| Aspek | Kode Lama Anda | Kode Baru |
|-------|----------------|-----------|
| **Device Name** | `ESP32_DualLED` | `ESP32_LAMP` |
| **Command** | `ON1`, `OFF1`, `ON2`, `OFF2` | `'1'`, `'0'` |
| **Protocol** | String dengan newline | Single character |
| **Control** | LED terpisah | Kedua LED bersamaan |

---

## 🚀 CARA UPLOAD KODE KE ESP32

### STEP 1: Buka Arduino IDE

1. Buka Arduino IDE
2. File → New Sketch

### STEP 2: Copy Kode

1. Copy **SELURUH** kode dari file `ESP32_FINAL_CODE.ino`
2. Paste ke Arduino IDE (replace semua kode yang ada)

### STEP 3: Konfigurasi Board

**Tools Menu:**
```
Board: "ESP32 Dev Module"
Upload Speed: 115200
CPU Frequency: 240MHz (WiFi/BT)
Flash Frequency: 80MHz
Flash Mode: QIO
Flash Size: 4MB (32Mb)
Partition Scheme: Default 4MB with spiffs (1.2MB APP/1.5MB SPIFFS)
Core Debug Level: None
```

### STEP 4: Select Port

```
Tools → Port → [pilih port ESP32 Anda]
```

**Windows:** COM3, COM4, dst.
**Mac/Linux:** /dev/ttyUSB0, /dev/cu.usbserial-xxx

### STEP 5: Upload

1. Klik tombol **Upload** (→) atau Sketch → Upload
2. Tunggu hingga muncul "Done uploading"

**Jika error "Failed to connect":**
- Hold tombol BOOT di ESP32
- Klik Upload
- Release BOOT setelah "Connecting..."

---

## 🔍 CEK SERIAL MONITOR

### STEP 1: Buka Serial Monitor

```
Tools → Serial Monitor
atau Ctrl+Shift+M
```

### STEP 2: Set Baud Rate

```
Baud Rate: 115200
```

### STEP 3: Output yang Diharapkan

```
===================================
  ESP32 DUAL LED CONTROLLER
  Compatible with RemoteLamp App
===================================
✅ Bluetooth Initialized
📱 Device Name: ESP32_LAMP
⏳ Waiting for connection...
===================================

✅ LED 1 initialized (Pin 13) - OFF
✅ LED 2 initialized (Pin 14) - OFF

📡 Ready to receive commands!
```

**Jika muncul output di atas, ESP32 siap digunakan!** ✅

---

## 📱 CARA PAIRING DI ANDROID

### STEP 1: Unpair Device Lama

1. Buka **Settings** di Android
2. Ke menu **Bluetooth**
3. Cari **"ESP32_DualLED"** (device lama)
4. Klik icon ⚙️ atau ⓘ
5. Klik **"Unpair"** atau **"Forget"**

### STEP 2: Pair Device Baru

1. Masih di menu Bluetooth
2. Pastikan Bluetooth **ON**
3. Cari **"ESP32_LAMP"** (nama baru)
4. Klik **"Pair"**
5. Jika diminta PIN, masukkan: **1234** atau **0000**

**Setelah paired, akan muncul status "Paired" atau "Connected".**

---

## 🧪 CARA TEST DENGAN APLIKASI

### STEP 1: Buka Aplikasi RemoteLamp

1. Buka aplikasi **RemoteLamp** di Android
2. Jika diminta permission Bluetooth → **Allow**

### STEP 2: Connect ke ESP32

1. Klik tombol **"Connect Device"**
2. Dialog akan muncul dengan daftar paired devices
3. Pilih **"ESP32_LAMP"**
4. Tunggu hingga status **"Connected to ESP32_LAMP"**

### STEP 3: Test TURN ON

1. Klik tombol **"🔆 TURN ON"**
2. ✅ Kedua LED harus **NYALA** 💡💡
3. ✅ Background app berubah **TERANG**
4. ✅ Toast: "Lampu dinyalakan"

**Cek Serial Monitor ESP32:**
```
📥 Received command: '1'
━━━━━━━━━━━━━━━━━━━━━━━━━━
💡 LAMP ON
✅ LED 1 (Pin 13): ON
✅ LED 2 (Pin 14): ON
━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### STEP 4: Test TURN OFF

1. Klik tombol **"🌙 TURN OFF"**
2. ✅ Kedua LED harus **MATI**
3. ✅ Background app berubah **GELAP**
4. ✅ Toast: "Lampu dimatikan"

**Cek Serial Monitor ESP32:**
```
📥 Received command: '0'
━━━━━━━━━━━━━━━━━━━━━━━━━━
🌙 LAMP OFF
❌ LED 1 (Pin 13): OFF
❌ LED 2 (Pin 14): OFF
━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## ✅ CHECKLIST TESTING

- [ ] ESP32 upload berhasil
- [ ] Serial Monitor menampilkan "Bluetooth Initialized"
- [ ] Device "ESP32_LAMP" muncul di Bluetooth scan
- [ ] Berhasil pair di Android
- [ ] Aplikasi RemoteLamp bisa connect
- [ ] Tombol TURN ON → Kedua LED nyala
- [ ] Tombol TURN OFF → Kedua LED mati
- [ ] Serial Monitor menampilkan command yang diterima
- [ ] Tidak ada error di Serial Monitor
- [ ] Koneksi stabil (tidak disconnect)

---

## 🐛 TROUBLESHOOTING

### ❌ Problem: Upload error "Failed to connect to ESP32"

**Solusi:**
1. Cek kabel USB (gunakan kabel data, bukan charge-only)
2. Hold tombol **BOOT** saat upload
3. Install driver CH340/CP2102 jika belum
4. Coba port USB yang lain
5. Restart ESP32 dan Arduino IDE

---

### ❌ Problem: Serial Monitor tidak tampil apa-apa

**Solusi:**
1. Cek baud rate: harus **115200**
2. Restart ESP32
3. Klik tombol **EN** di ESP32
4. Close dan buka ulang Serial Monitor

---

### ❌ Problem: Bluetooth "ESP32_LAMP" tidak muncul di scan

**Solusi:**
1. Restart ESP32
2. Cek Serial Monitor: apakah ada error "Bluetooth not enabled"?
3. Pastikan ESP32 yang digunakan support Bluetooth (bukan ESP32-C3)
4. Nyalakan ulang Bluetooth di Android
5. Coba scan ulang

---

### ❌ Problem: Aplikasi connect tapi LED tidak nyala

**Solusi:**

**1. Cek Serial Monitor:**
```
Apakah muncul "📥 Received command: '1'"?
```

- Jika **YA** tapi LED tidak nyala → Problem di wiring
- Jika **TIDAK** → Problem di Bluetooth connection

**2. Cek Wiring LED:**
```
ESP32 Pin 13 → R220Ω → LED+ → LED- → GND
ESP32 Pin 14 → R220Ω → LED+ → LED- → GND
```

**3. Test LED dengan kode sederhana:**
```cpp
void loop() {
  digitalWrite(13, HIGH);
  delay(1000);
  digitalWrite(13, LOW);
  delay(1000);
}
```

Jika LED berkedip → Wiring OK, problem di Bluetooth
Jika LED tidak berkedip → Wiring salah

**4. Cek Polaritas LED:**
- Kaki panjang LED = Anode (+) → ke resistor
- Kaki pendek LED = Cathode (-) → ke GND

---

### ❌ Problem: Command diterima tapi wrong command

**Serial Monitor menampilkan:**
```
⚠️  WARNING: Unknown command!
   Received: 'O' (ASCII: 79)
   Expected: '1' (ON) or '0' (OFF)
```

**Penyebab:**
Aplikasi Android mengirim karakter yang salah.

**Solusi:**
1. Pastikan aplikasi kirim **character '1'** bukan string "1"
2. Cek kode Android di `ControlScreen.kt`:
   ```kotlin
   bluetoothController.sendCommand("1") // BENAR
   ```
   Bukan:
   ```kotlin
   bluetoothController.sendCommand("ON\n") // SALAH
   ```

---

### ❌ Problem: Koneksi sering disconnect

**Solusi:**
1. Cek power supply ESP32: minimal 500mA
2. Jangan terlalu jauh dari Android (max 10 meter)
3. Hindari interference dari WiFi 2.4GHz
4. Pastikan ESP32 tidak overheat
5. Gunakan power supply yang stabil (bukan dari USB laptop)

---

## 📊 EXPECTED BEHAVIOR

### Scenario 1: Normal Operation

```
User Action          Android App              ESP32                  LED
────────────────────────────────────────────────────────────────────────
Klik "Connect"    → Send connection req    → Accept connection    → -
                  ← Status: "Connected"     ← -                    ← -

Klik "TURN ON"    → Send '1'               → Receive '1'          → 💡💡
                  ← Show toast "ON"         ← Print "LAMP ON"     ← ON

Klik "TURN OFF"   → Send '0'               → Receive '0'          → ⚫⚫
                  ← Show toast "OFF"        ← Print "LAMP OFF"    ← OFF

Klik "Back"       → Disconnect              → Close connection     → ⚫⚫
```

### Scenario 2: Error Handling

```
Action                Result
────────────────────────────────────────────────────────
ESP32 off          → App: "Connection failed"
Wrong device       → App: "Connection failed"
Command error      → Serial: "Unknown command"
LED disconnected   → Command OK, but LED not light
```

---

## 📋 FINAL CHECKLIST

### Hardware:
- [ ] ESP32 Dev Board
- [ ] 2x LED
- [ ] 2x Resistor 220Ω
- [ ] Kabel jumper
- [ ] Power supply (USB atau 5V)

### Software:
- [ ] Arduino IDE installed
- [ ] ESP32 board package installed
- [ ] Driver USB (CH340/CP2102) installed

### Wiring:
- [ ] Pin 13 → R220Ω → LED1+
- [ ] LED1- → GND
- [ ] Pin 14 → R220Ω → LED2+
- [ ] LED2- → GND

### Upload:
- [ ] Kode ter-upload tanpa error
- [ ] Serial Monitor menampilkan output yang benar

### Bluetooth:
- [ ] ESP32_LAMP muncul di scan
- [ ] Berhasil pair di Android
- [ ] Koneksi stabil

### Testing:
- [ ] TURN ON → Kedua LED nyala
- [ ] TURN OFF → Kedua LED mati
- [ ] Serial Monitor menampilkan command
- [ ] Tidak ada error message

---

## 🎉 JIKA SEMUA CHECKLIST ✅

**Selamat! Sistem Anda sudah berfungsi dengan sempurna!** 🎊

Anda sekarang punya:
- ✅ Aplikasi Android yang modern
- ✅ ESP32 dengan 2 LED yang berfungsi
- ✅ Komunikasi Bluetooth yang stabil
- ✅ Dokumentasi lengkap

---

## 📞 NEXT STEPS (OPSIONAL)

### 1. Tambah LED Lebih Banyak
Tinggal tambah pin dan PWM channel:
```cpp
const int ledPin3 = 27;
const int pwmChannel3 = 2;
```

### 2. Tambah Brightness Control
Modifikasi command untuk terima nilai 0-255:
```cpp
if (command == 'B') {
  int brightness = SerialBT.parseInt();
  ledcWrite(pwmChannel1, brightness);
}
```

### 3. Tambah RGB LED
Gunakan 3 PWM channel untuk R, G, B:
```cpp
ledcWrite(redChannel, red);
ledcWrite(greenChannel, green);
ledcWrite(blueChannel, blue);
```

---

**Semua file dokumentasi tersedia di folder project Anda!**

Good luck dan selamat mencoba! 🚀

