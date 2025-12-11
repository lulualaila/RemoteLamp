# 🔧 CARA IMPLEMENTASI SOLUSI 2 - DUAL LED CONTROL

## 📋 Overview

Solusi ini memungkinkan Anda mengontrol 2 LED secara **terpisah** dari aplikasi Android.

**Yang Berubah:**
- ✅ Aplikasi Android: Tambah screen baru + update navigation
- ❌ ESP32: Tetap pakai kode asli Anda (tidak perlu ubah)

---

## 🚀 LANGKAH-LANGKAH IMPLEMENTASI

### STEP 1: Copy File DualLampControlScreen.kt

File `DualLampControlScreen.kt` sudah dibuat. Copy ke folder project:

```
app/src/main/java/com/remotelamp/app/DualLampControlScreen.kt
```

### STEP 2: Update MainActivity.kt

Tambahkan route untuk DualLampControlScreen:

**CARA 1: Replace ControlScreen dengan DualLampControlScreen**

Buka `MainActivity.kt`, ganti pada bagian navigation:

```kotlin
// SEBELUM:
"control" -> {
    ControlScreen(
        bluetoothController = bluetoothController,
        onBack = { ... },
        onExit = { ... }
    )
}

// SESUDAH:
"control" -> {
    DualLampControlScreen(
        bluetoothController = bluetoothController,
        onBack = { 
            bluetoothController.disconnect()
            currentScreen = "connect" 
        },
        onExit = { 
            bluetoothController.disconnect()
            activity.finish() 
        }
    )
}
```

**CARA 2: Tambah pilihan di ConnectScreen**

Atau bisa buat pilihan mode: Single Lamp / Dual Lamp

---

### STEP 3: Update BluetoothScreen.kt (Opsional)

Jika mau, ubah nama device yang dicari dari "ESP32_LAMP" ke "ESP32_DualLED".

Atau biarkan user pilih dari list paired devices (sudah support).

---

### STEP 4: Upload Kode ESP32

Upload `ESP32_CODE_SOLUSI2.ino` (kode asli Anda) ke ESP32.

**Arduino IDE:**
1. Buka `ESP32_CODE_SOLUSI2.ino`
2. Select Board: ESP32 Dev Module
3. Select Port: (port ESP32 Anda)
4. Upload

---

### STEP 5: Pair Device

1. Android Settings → Bluetooth
2. Unpair "ESP32_LAMP" (jika ada)
3. Pair "ESP32_DualLED"

---

### STEP 6: Build & Run

```bash
cd "C:\NAZILA\KULIAH\SEM 5\WMC\RemoteLamp (1)\RemoteLamp"
.\gradlew assembleDebug
```

Atau klik Run di Android Studio.

---

## 📱 TAMPILAN APLIKASI (SOLUSI 2)

### Screen: DualLampControlScreen

```
┌────────────────────────────────────────┐
│      Dual Lamp Control                 │
│   Connected to ESP32_DualLED           │
├────────────────────────────────────────┤
│                                        │
│   ┌───────────┐    ┌───────────┐      │
│   │    💡     │    │    💡     │      │
│   │    ON     │    │    OFF    │      │
│   │  Lamp 1   │    │  Lamp 2   │      │
│   └───────────┘    └───────────┘      │
│                                        │
├────────────────────────────────────────┤
│     Lamp 1 (Yellow)                    │
│   ┌──────────┐  ┌──────────┐          │
│   │    ON    │  │   OFF    │          │
│   └──────────┘  └──────────┘          │
│                                        │
│     Lamp 2 (Blue)                      │
│   ┌──────────┐  ┌──────────┐          │
│   │    ON    │  │   OFF    │          │
│   └──────────┘  └──────────┘          │
│                                        │
│   [  Back  ]    [  Exit  ]             │
└────────────────────────────────────────┘
```

---

## 🎨 FITUR UI

### Lamp 1 (Yellow)
- **ON Button**: Kuning (#FFC107)
  - Kirim command: "ON1\n"
  - Disabled saat lampu sudah ON
- **OFF Button**: Abu-abu (#455A64)
  - Kirim command: "OFF1\n"
  - Disabled saat lampu sudah OFF

### Lamp 2 (Blue)
- **ON Button**: Biru (#4285F4)
  - Kirim command: "ON2\n"
  - Disabled saat lampu sudah ON
- **OFF Button**: Abu-abu (#455A64)
  - Kirim command: "OFF2\n"
  - Disabled saat lampu sudah OFF

### Visual Indicators
- 2 bulb icons di atas (status masing-masing lampu)
- Glow effect saat lampu ON
- Color change animation (smooth 500ms)

---

## 🔧 CODE CHANGES DETAIL

### File yang Ditambahkan:
1. ✅ `DualLampControlScreen.kt` (file baru)

### File yang Diupdate:
2. ✅ `MainActivity.kt` (tambah route untuk dual lamp)

### File yang Tidak Berubah:
- ❌ `BluetoothController.kt` (sudah support kirim string apapun)
- ❌ `BluetoothScreen.kt` (sudah support list paired devices)
- ❌ `Theme.kt` (tetap sama)

---

## 📝 UPDATE MainActivity.kt

Buka file `MainActivity.kt`, cari bagian navigation `when (currentScreen)`, lalu:

**OPSI A: Replace ControlScreen**

```kotlin
@Composable
fun RemoteLampApp(
    activity: MainActivity,
    bluetoothController: BluetoothController
) {
    var currentScreen by remember { mutableStateOf("connect") }
    
    when (currentScreen) {
        "connect" -> {
            ConnectScreen(
                bluetoothController = bluetoothController,
                onConnected = { currentScreen = "control" },
                onExit = { activity.finish() }
            )
        }
        
        "control" -> {
            DualLampControlScreen(  // ← GANTI INI
                bluetoothController = bluetoothController,
                onBack = { 
                    bluetoothController.disconnect()
                    currentScreen = "connect" 
                },
                onExit = { 
                    bluetoothController.disconnect()
                    activity.finish() 
                }
            )
        }
    }
}
```

**OPSI B: Tambah Menu Pilihan**

Atau buat screen selection menu:

```kotlin
when (currentScreen) {
    "connect" -> ConnectScreen(...)
    
    "menu" -> MenuScreen(
        onSingleLamp = { currentScreen = "single" },
        onDualLamp = { currentScreen = "dual" }
    )
    
    "single" -> ControlScreen(...)  // 1 lampu
    
    "dual" -> DualLampControlScreen(...)  // 2 lampu
}
```

---

## 🧪 TESTING

### Test Case 1: LED 1 ON
1. Klik "Lamp 1 - ON"
2. ✅ LED 1 (pin 13) nyala
3. ✅ LED 2 tetap sesuai status sebelumnya
4. ✅ Toast: "Lamp 1 turned ON"
5. ✅ Visual indicator berubah

### Test Case 2: LED 2 ON
1. Klik "Lamp 2 - ON"
2. ✅ LED 2 (pin 14) nyala
3. ✅ LED 1 tetap sesuai status sebelumnya
4. ✅ Toast: "Lamp 2 turned ON"
5. ✅ Visual indicator berubah

### Test Case 3: Independent Control
1. Nyalakan LED 1
2. Nyalakan LED 2 (kedua LED nyala)
3. Matikan LED 1 (hanya LED 2 yang nyala)
4. Matikan LED 2 (kedua LED mati)
✅ Semua berfungsi terpisah

---

## 📊 PERBANDINGAN

| Aspek | ControlScreen (Lama) | DualLampControlScreen (Baru) |
|-------|----------------------|------------------------------|
| **Jumlah Lampu** | 1 lampu | 2 lampu |
| **Command** | "1", "0" | "ON1", "OFF1", "ON2", "OFF2" |
| **Control** | Bersamaan | Terpisah |
| **Tombol** | 2 tombol (ON/OFF) | 4 tombol (ON/OFF x2) |
| **Visual** | 1 bulb indicator | 2 bulb indicators |
| **Complexity** | Sederhana | Medium |

---

## 🐛 TROUBLESHOOTING

### ❌ Error: "Unresolved reference DualLampControlScreen"
**Solusi:**
1. Pastikan file `DualLampControlScreen.kt` ada di folder yang benar
2. Sync project: File → Sync Project with Gradle Files
3. Rebuild: Build → Rebuild Project

### ❌ LED tidak merespon
**Solusi:**
1. Cek Serial Monitor ESP32:
   ```
   ✅ LED 1 ON
   ```
2. Pastikan command dikirim dengan newline: `"ON1\n"`
3. Cek apakah ESP32 terima command dengan benar

### ❌ Kedua LED nyala bersamaan
**Solusi:**
- Ini bug di ESP32 code
- Cek apakah kode ESP32 menggunakan `ledcWrite` dengan channel yang berbeda
- Pastikan pin 13 dan 14 terhubung ke LED yang berbeda

---

## 💡 PENGEMBANGAN LANJUTAN

### 1. Tambah Brightness Slider
```kotlin
Slider(
    value = brightness1,
    onValueChange = { 
        brightness1 = it
        sendCommand("B1${it.toInt()}\n") // B10-B1255
    },
    valueRange = 0f..255f
)
```

### 2. Save State (SharedPreferences)
```kotlin
// Save
prefs.edit().putBoolean("lamp1_status", true).apply()

// Load
val lamp1Status = prefs.getBoolean("lamp1_status", false)
```

### 3. Schedule/Timer
```kotlin
AlarmManager → sendCommand("ON1\n") at 18:00
```

---

## ✅ CHECKLIST IMPLEMENTASI

- [ ] Copy `DualLampControlScreen.kt` ke project
- [ ] Update `MainActivity.kt` (tambah route)
- [ ] Upload `ESP32_CODE_SOLUSI2.ino` ke ESP32
- [ ] Pair "ESP32_DualLED" di Android
- [ ] Build & install APK
- [ ] Test LED 1 ON/OFF
- [ ] Test LED 2 ON/OFF
- [ ] Test independent control

---

## 🎉 HASIL AKHIR

Setelah implementasi SOLUSI 2:

✅ Aplikasi support 2 LED terpisah
✅ Bisa control LED 1 dan LED 2 secara independen
✅ Visual indicator untuk masing-masing LED
✅ Toast notification untuk feedback
✅ Smooth animations
✅ Real-time connection monitoring

---

**Selamat mencoba! Jika ada pertanyaan, silakan tanya. 😊**

