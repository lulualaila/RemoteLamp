# ⚡ QUICK REFERENCE - ESP32 & ANDROID REMOTELAMP

## 📱 APLIKASI ANDROID

### Device Name
```
ESP32_LAMP
```

### Commands
| Button | Command | Hasil |
|--------|---------|-------|
| TURN ON | `'1'` | Kedua LED nyala 💡💡 |
| TURN OFF | `'0'` | Kedua LED mati |

### APK Location
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔧 ESP32 CODE

### File
```
ESP32_CODE_SOLUSI1.ino
```

### Device Name
```cpp
SerialBT.begin("ESP32_LAMP");
```

### Pin Configuration
```cpp
const int ledPin1 = 13;  // LED pertama
const int ledPin2 = 14;  // LED kedua
```

### Wiring Diagram
```
ESP32                LED 1              GND
Pin 13 ──→ R220Ω ──→ LED+ ──→ LED- ──→ ⏚

ESP32                LED 2              GND
Pin 14 ──→ R220Ω ──→ LED+ ──→ LED- ──→ ⏚
```

### Command Handler
```cpp
if (command == '1') {
  ledcWrite(pwmChannel1, 255);  // ON
  ledcWrite(pwmChannel2, 255);  // ON
}
else if (command == '0') {
  ledcWrite(pwmChannel1, 0);    // OFF
  ledcWrite(pwmChannel2, 0);    // OFF
}
```

---

## 🚀 UPLOAD STEPS

### 1. Arduino IDE Setup
```
Board: ESP32 Dev Module
Upload Speed: 115200
Port: [Your ESP32 Port]
```

### 2. Upload
```
Sketch → Upload
atau Ctrl+U
```

### 3. Verify Serial Monitor
```
Baud Rate: 115200

Expected Output:
✅ Bluetooth Initialized
📱 Device Name: ESP32_LAMP
📡 Ready to receive commands!
```

---

## 📲 PAIRING STEPS

### Android Settings
```
1. Settings → Bluetooth
2. Unpair "ESP32_DualLED" (device lama)
3. Pair "ESP32_LAMP" (device baru)
4. PIN: 1234 (jika diminta)
```

---

## 🧪 TESTING STEPS

### 1. Connect
```
App: Connect Device → Select "ESP32_LAMP"
Status: "Connected to ESP32_LAMP"
```

### 2. Test ON
```
App: Klik "TURN ON"
LED: 💡💡 (kedua LED nyala)
Serial: 💡 LAMP ON
Toast: "Lampu dinyalakan"
```

### 3. Test OFF
```
App: Klik "TURN OFF"
LED: ⚫⚫ (kedua LED mati)
Serial: 🌙 LAMP OFF
Toast: "Lampu dimatikan"
```

---

## 🐛 QUICK TROUBLESHOOTING

| Problem | Quick Fix |
|---------|-----------|
| Upload error | Hold BOOT saat upload |
| No Bluetooth | Restart ESP32 |
| Can't pair | Unpair lalu pair ulang |
| LED no light | Cek wiring & polaritas |
| Wrong command | Cek Serial Monitor output |

---

## 📊 PROTOCOL SUMMARY

### Bluetooth
```
Type: Classic Bluetooth (SPP)
UUID: 00001101-0000-1000-8000-00805F9B34FB
Protocol: Single character
Encoding: ASCII
```

### Communication Flow
```
Android App          ESP32
    |                  |
    |--- connect ----->|
    |<-- accepted -----|
    |                  |
    |------ '1' ------>| LED ON 💡
    |<-- "Lamp ON" ----|
    |                  |
    |------ '0' ------>| LED OFF
    |<-- "Lamp OFF" ---|
```

---

## 📁 FILE LOCATIONS

### ESP32 Code
```
RemoteLamp/ESP32_CODE_SOLUSI1.ino
```

### Android APK
```
RemoteLamp/app/build/outputs/apk/debug/app-debug.apk
```

### Documentation
```
RemoteLamp/PANDUAN_UPLOAD_ESP32.md
RemoteLamp/DOKUMENTASI_LENGKAP.md
RemoteLamp/STATUS_FINAL.md
```

---

## ⚙️ HARDWARE SPECS

### ESP32
```
Board: ESP32 Dev Module
Voltage: 3.3V logic, 5V power
Bluetooth: Classic + BLE
GPIO Pins: 13, 14
```

### LED
```
Quantity: 2x
Resistor: 220Ω each
Voltage: 2-3V (standard LED)
Current: ~15-20mA per LED
```

---

## 💻 SERIAL MONITOR OUTPUTS

### Boot
```
===================================
  ESP32 DUAL LED CONTROLLER
  Compatible with RemoteLamp App
===================================
✅ Bluetooth Initialized
📱 Device Name: ESP32_LAMP
⏳ Waiting for connection...
===================================
```

### Command Received
```
📥 Received command: '1'
━━━━━━━━━━━━━━━━━━━━━━━━━━
💡 LAMP ON
✅ LED 1 (Pin 13): ON
✅ LED 2 (Pin 14): ON
━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🎯 SUCCESS INDICATORS

### ESP32
- ✅ Blue LED on ESP32 board blinking (Bluetooth active)
- ✅ Serial Monitor shows command received
- ✅ Both external LEDs respond to commands

### Android App
- ✅ Status shows "Connected to ESP32_LAMP"
- ✅ Tombol TURN ON/OFF berfungsi
- ✅ Toast notification muncul
- ✅ UI background berubah sesuai status

---

## 🔗 USEFUL LINKS

### Arduino IDE
```
https://www.arduino.cc/en/software
```

### ESP32 Board Manager URL
```
https://dl.espressif.com/dl/package_esp32_index.json
```

### Driver CH340
```
https://sparks.gogo.co.nz/ch340.html
```

---

## 📞 EMERGENCY CHECKLIST

Jika semua tidak berfungsi:

- [ ] Restart ESP32
- [ ] Restart Android device
- [ ] Re-upload ESP32 code
- [ ] Unpair & pair ulang
- [ ] Cek wiring dengan multimeter
- [ ] Test LED dengan kode sederhana
- [ ] Cek power supply ESP32 (min 500mA)
- [ ] Baca PANDUAN_UPLOAD_ESP32.md

---

**Print atau bookmark halaman ini untuk referensi cepat!** 📌

Last Updated: 14 November 2025

