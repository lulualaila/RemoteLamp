# 📱 Remote Lamp - Bluetooth Controller

Aplikasi Android sederhana untuk mengontrol Smart Lamp via Bluetooth menggunakan **Kotlin + Jetpack Compose**.

## ✨ Fitur Utama

- 🔵 **Connect via Bluetooth** - Pilih dari daftar paired devices
- 💡 **ON/OFF Control** - Kirim perintah "1" (ON) dan "0" (OFF) ke ESP32
- 🎨 **Material 3 UI** - Desain modern dengan animasi smooth
- 📊 **Real-time Status** - Monitor koneksi secara realtime

## 🚀 Quick Start

### 1. Pair ESP32 di Android Settings
```
Settings → Bluetooth → Pair dengan "ESP32_LAMP"
```

### 2. Install APK
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. Gunakan Aplikasi
1. Klik **"Connect Device"**
2. Pilih ESP32 dari list
3. Klik **"TURN ON"** atau **"TURN OFF"**

## 📂 Struktur File

```
app/src/main/java/com/remotelamp/app/
├── MainActivity.kt          # Activity utama + navigation
├── BluetoothController.kt   # Logic Bluetooth (connect, send, disconnect)
├── BluetoothScreen.kt       # Screen connect device
├── ControlScreen.kt         # Screen kontrol ON/OFF
└── ui/theme/Theme.kt        # Material 3 theme
```

## 🔧 Kode ESP32

```cpp
#include <BluetoothSerial.h>

BluetoothSerial SerialBT;
const int LAMP_PIN = 2;

void setup() {
  pinMode(LAMP_PIN, OUTPUT);
  SerialBT.begin("ESP32_LAMP");
}

void loop() {
  if (SerialBT.available()) {
    char cmd = SerialBT.read();
    digitalWrite(LAMP_PIN, cmd == '1' ? HIGH : LOW);
  }
}
```

## 📖 Dokumentasi Lengkap

Lihat file **[DOKUMENTASI_LENGKAP.md](DOKUMENTASI_LENGKAP.md)** untuk:
- Penjelasan detail setiap file
- Cara kerja Bluetooth communication
- Troubleshooting
- Tips & best practices

## 🎯 Jawaban Pertanyaan

### ❓ Apakah sudah ada backend?
**TIDAK ADA BACKEND.** Aplikasi ini adalah client-side app yang berkomunikasi langsung dengan ESP32 via Bluetooth. Tidak butuh server/internet.

```
Android App ←→ Bluetooth ←→ ESP32 ←→ LED
```

### ❓ Kenapa error?
Error yang ada hanyalah **warning IDE** (cache belum refresh). Build project **BERHASIL** ✅

File yang sudah dibuat:
- ✅ `MainActivity.kt` - Activity utama dengan permission handling
- ✅ `BluetoothController.kt` - Logic Bluetooth lengkap
- ✅ `BluetoothScreen.kt` - UI connect dengan list paired devices
- ✅ `ControlScreen.kt` - UI kontrol ON/OFF dengan animasi
- ✅ `AndroidManifest.xml` - Permission Bluetooth untuk Android 12+
- ✅ `Theme.kt` - Material 3 theme dengan warna biru

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Async**: Coroutines
- **Bluetooth**: BluetoothAdapter + BluetoothSocket
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

## 📱 Screenshots

### Connect Screen
- Tombol "Connect Device"
- List paired devices
- Status koneksi

### Control Screen
- Tombol "TURN ON" (hijau)
- Tombol "TURN OFF" (abu-abu)
- Visual indicator lampu dengan animasi

## 🎓 Cocok untuk Belajar

Project ini sangat cocok untuk pemula karena:
- ✅ Kode bersih dengan komentar lengkap
- ✅ Struktur sederhana (2 screen saja)
- ✅ Tidak ada library kompleks
- ✅ Dokumentasi detail

## 🐛 Troubleshooting

| Problem | Solusi |
|---------|--------|
| Tidak ada device | Pair ESP32 di Settings terlebih dahulu |
| Connection failed | Restart ESP32 dan coba lagi |
| Permission denied | Berikan permission Bluetooth di Settings |
| Lampu tidak respon | Cek wiring dan Serial Monitor ESP32 |

## 📦 Build Output

```
BUILD SUCCESSFUL in 21s
36 actionable tasks: 9 executed, 27 up-to-date

APK: app/build/outputs/apk/debug/app-debug.apk
```

## 👨‍💻 Author

**Nazila** - Student WMC (Web and Mobile Computing) Semester 5

---

**Happy Coding! 🎉**

Untuk pertanyaan atau masalah, cek [DOKUMENTASI_LENGKAP.md](DOKUMENTASI_LENGKAP.md)

