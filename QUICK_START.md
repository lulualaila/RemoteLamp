# 🎯 QUICK START GUIDE - RemoteLamp App

## ✅ ERROR FIXED!

**Redeclaration: MainActivity** → SUDAH DIPERBAIKI ✅

---

## 📱 YANG AKTIF SEKARANG

### Android App
**File Utama**: `MainActivityEnhanced.kt` (class `MainActivity`)

**Fitur**:
- ✅ Kontrol Lamp 1 ON/OFF
- ✅ Kontrol Lamp 2 ON/OFF  
- ✅ Kontrol All Lamps ON/OFF
- ✅ Auto-sync status saat app dibuka
- ✅ ViewModel (MVVM architecture)

### ESP32 Code
**File**: `ESP32_DUAL_LAMP_CODE.ino`

**Perintah yang didukung**:
```
ON1  → Nyalakan LED 1
OFF1 → Matikan LED 1
ON2  → Nyalakan LED 2
OFF2 → Matikan LED 2
STATUS → Query status kedua LED
```

---

## 🚀 LANGKAH CEPAT

### 1️⃣ Upload ke ESP32
```
1. Buka ESP32_DUAL_LAMP_CODE.ino di Arduino IDE
2. Pilih Board: ESP32 Dev Module
3. Pilih Port COM yang sesuai
4. Klik Upload
5. Device Bluetooth: "ESP32_DualLED"
```

### 2️⃣ Build & Install Android App
```powershell
# Build APK
.\gradlew assembleDebug

# Install ke device
.\gradlew installDebug
```

### 3️⃣ Hubungkan & Gunakan
```
1. Pair ESP32_DualLED di Bluetooth Settings
2. Buka app "RemoteLamp"
3. Tap "Connect Device"
4. Pilih "ESP32_DualLED"
5. Kontrol lampu! 🎉
```

---

## 🔌 KONEKSI HARDWARE

```
ESP32 GPIO 13 → LED 1 → Resistor 220Ω → GND
ESP32 GPIO 14 → LED 2 → Resistor 220Ω → GND
```

---

## 📁 FILE PENTING

### ✅ GUNAKAN INI
- `MainActivityEnhanced.kt` → Main Activity
- `LampControlViewModel.kt` → Logic
- `EnhancedBluetoothController.kt` → Bluetooth
- `EnhancedConnectScreen.kt` → UI Connect
- `EnhancedControlScreen.kt` → UI Control
- `ESP32_DUAL_LAMP_CODE.ino` → ESP32

### ⚠️ LEGACY (Abaikan)
- `MainActivity.kt` (renamed to MainActivityOld)
- `BluetoothController.kt` (simple version)
- `ControlScreen.kt` (simple version)

---

## ❓ BACKEND?

**TIDAK PERLU BACKEND!**

Komunikasi langsung:
```
Android App ←→ Bluetooth ←→ ESP32 ←→ LED
```

Tidak ada HTTP/REST API yang diperlukan.

---

## 🎉 STATUS

**SIAP DIGUNAKAN!** Semua error sudah diperbaiki.

Jika ada pertanyaan, cek dokumentasi lengkap di `FIX_SUMMARY.md`

