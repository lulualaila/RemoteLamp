# 🎛️ Remote Lamp Controller - Version 2.0 (Enhanced)

<div align="center">

![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5%2B-4285F4?logo=jetpackcompose)
![ESP32](https://img.shields.io/badge/ESP32-Bluetooth-00979D?logo=espressif)
![License](https://img.shields.io/badge/License-MIT-green)

**Aplikasi Android untuk mengontrol 2 lampu LED via ESP32 dengan Bluetooth**

[Features](#-features) • [Screenshots](#-screenshots) • [Installation](#-installation) • [Usage](#-usage) • [Architecture](#-architecture) • [Documentation](#-documentation)

</div>

---

## 🌟 Features

### ✨ Version 2.0 Highlights:

- 🎯 **Kontrol Individual** - Control Lamp 1 dan Lamp 2 secara terpisah
- ⚡ **Kontrol Semua** - Nyalakan/matikan semua lampu sekaligus
- 🔄 **Auto-Sync Status** - Status lampu selalu akurat saat app dibuka
- 🏗️ **MVVM Architecture** - Clean, maintainable, dan scalable code
- 🎨 **Material 3 Design** - Modern UI dengan smooth animations
- 📱 **Reactive UI** - Real-time updates dengan StateFlow
- 🔋 **Battery Efficient** - Optimized Bluetooth communication

### 📋 Control Options:

| Command | Description | ESP32 Pin |
|---------|-------------|-----------|
| **Lamp 1 ON/OFF** | Control LED pertama | Pin 13 |
| **Lamp 2 ON/OFF** | Control LED kedua | Pin 14 |
| **All ON/OFF** | Control semua LED | Pin 13 & 14 |
| **Refresh Status** | Query status terkini | - |

---

## 📸 Screenshots

<table>
  <tr>
    <td align="center">
      <img src="docs/screenshot_connect.png" width="250px" alt="Connect Screen"/><br/>
      <b>Connect Screen</b>
    </td>
    <td align="center">
      <img src="docs/screenshot_control.png" width="250px" alt="Control Screen"/><br/>
      <b>Control Screen</b>
    </td>
    <td align="center">
      <img src="docs/screenshot_lamps.png" width="250px" alt="Lamp Control"/><br/>
      <b>Individual Control</b>
    </td>
  </tr>
</table>

---

## 🔧 Tech Stack

### Android App:
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM (Model-View-ViewModel)
- **State Management:** StateFlow
- **Design:** Material 3
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)

### Hardware:
- **Microcontroller:** ESP32
- **Communication:** Bluetooth Classic (SPP)
- **Components:** 2x LED, 2x Resistor 220Ω

---

## 📦 Installation

### Prerequisites:
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android device with Bluetooth (API 24+)
- ESP32 development board
- Arduino IDE (for ESP32)

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/RemoteLamp.git
cd RemoteLamp
```

### Step 2: Setup ESP32

1. Open Arduino IDE
2. Install ESP32 board support:
   - File → Preferences
   - Additional Board Manager URLs: 
     ```
     https://dl.espressif.com/dl/package_esp32_index.json
     ```
   - Tools → Board → Board Manager → Search "ESP32" → Install

3. Upload code:
   - Open `ESP32_CODE_ENHANCED.ino`
   - Tools → Board → ESP32 Dev Module
   - Tools → Port → Select your ESP32 port
   - Click Upload

4. Wiring:
   ```
   ESP32 Pin 13 → Resistor 220Ω → LED1 (anode+) → GND
   ESP32 Pin 14 → Resistor 220Ω → LED2 (anode+) → GND
   ```

### Step 3: Build Android App

```bash
# Sync dependencies
./gradlew clean build

# Install to device
./gradlew installDebug
```

**Or via Android Studio:**
1. Open project in Android Studio
2. Sync Gradle
3. Connect Android device via USB
4. Run → Run 'app' (Shift+F10)

---

## 🚀 Usage

### 1. Pairing ESP32

```
📱 Android Settings
   ↓
🔵 Bluetooth → ON
   ↓
🔍 Scan Devices
   ↓
📡 Select "ESP32_DualLED"
   ↓
🔐 Pair (PIN: 1234 if asked)
```

### 2. Connect in App

```
1. Open RemoteLamp app
2. Allow Bluetooth permissions
3. Tap "Connect Device"
4. Select "ESP32_DualLED"
5. Wait for "Terhubung" status
6. Auto navigate to Control Screen
```

### 3. Control Lamps

**Individual Control:**
- Tap **Lamp 1 ON** → LED pin 13 nyala 💡
- Tap **Lamp 1 OFF** → LED pin 13 mati 🌑
- Tap **Lamp 2 ON** → LED pin 14 nyala 💡
- Tap **Lamp 2 OFF** → LED pin 14 mati 🌑

**Bulk Control:**
- Tap **NYALAKAN SEMUA** → Both LEDs ON 💡💡
- Tap **MATIKAN SEMUA** → Both LEDs OFF 🌑🌑

**Refresh Status:**
- Tap **Refresh Status** → Query latest status from ESP32

---

## 🏗️ Architecture

### MVVM Pattern

```
┌─────────────────────────────────────┐
│         UI Layer (Compose)          │
│  EnhancedConnectScreen.kt           │
│  EnhancedControlScreen.kt           │
└──────────────┬──────────────────────┘
               │ observes StateFlow
               │ calls functions
┌──────────────▼──────────────────────┐
│      ViewModel Layer (Logic)        │
│  LampControlViewModel.kt            │
│  - State management                 │
│  - Business logic                   │
└──────────────┬──────────────────────┘
               │ calls methods
┌──────────────▼──────────────────────┐
│     Data Layer (Bluetooth)          │
│  EnhancedBluetoothController.kt     │
│  - Bluetooth operations             │
│  - Hardware communication           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Hardware (ESP32 + LEDs)        │
└─────────────────────────────────────┘
```

### Key Components:

**1. LampControlViewModel**
```kotlin
- State: LampControlUiState (isConnected, lamp1Status, lamp2Status)
- Functions: toggleLamp(), toggleAllLamps(), refreshLampStatus()
- Lifecycle: Survive configuration changes
```

**2. EnhancedBluetoothController**
```kotlin
- Connection: connect(), disconnect()
- Commands: toggleLamp(), toggleAllLamps(), sendCommand()
- Status: getStatus() → LampStatus(lamp1, lamp2)
```

**3. UI Screens**
```kotlin
- EnhancedConnectScreen: Device pairing & connection
- EnhancedControlScreen: Lamp control interface
```

---

## 📡 Protocol

### Bluetooth Commands:

| Command | Format | Response | Description |
|---------|--------|----------|-------------|
| ON1 | `"ON1\n"` | `"LED 1 ON"` | Turn on Lamp 1 |
| OFF1 | `"OFF1\n"` | `"LED 1 OFF"` | Turn off Lamp 1 |
| ON2 | `"ON2\n"` | `"LED 2 ON"` | Turn on Lamp 2 |
| OFF2 | `"OFF2\n"` | `"LED 2 OFF"` | Turn off Lamp 2 |
| ONALL | `"ONALL\n"` | `"ALL LEDS ON"` | Turn on all lamps |
| OFFALL | `"OFFALL\n"` | `"ALL LEDS OFF"` | Turn off all lamps |
| STATUS | `"STATUS\n"` | `"STATUS:1,0"` | Query lamp status |

### Status Format:

Response: `"STATUS:X,Y"`
- `X` = Lamp 1 status (1=ON, 0=OFF)
- `Y` = Lamp 2 status (1=ON, 0=OFF)

**Examples:**
- `STATUS:1,0` → Lamp1=ON, Lamp2=OFF
- `STATUS:0,1` → Lamp1=OFF, Lamp2=ON
- `STATUS:1,1` → Both ON
- `STATUS:0,0` → Both OFF

---

## 📁 Project Structure

```
RemoteLamp/
├── app/
│   ├── src/main/
│   │   ├── java/com/remotelamp/app/
│   │   │   ├── MainActivity.kt                  # Main activity
│   │   │   ├── EnhancedBluetoothController.kt  # Bluetooth logic
│   │   │   ├── LampControlViewModel.kt         # ViewModel
│   │   │   ├── EnhancedConnectScreen.kt        # Connect UI
│   │   │   ├── EnhancedControlScreen.kt        # Control UI
│   │   │   └── ui/theme/Theme.kt               # Material 3 theme
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   └── build.gradle.kts
│
├── ESP32_CODE_ENHANCED.ino              # ESP32 firmware
├── DOKUMENTASI_ENHANCED.md              # Full documentation
├── QUICK_IMPLEMENTATION_GUIDE.md        # Quick start guide
├── VERSION_COMPARISON.md                # V1.0 vs V2.0
├── README.md                            # This file
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 📖 Documentation

### Complete Guides:

1. **[DOKUMENTASI_ENHANCED.md](DOKUMENTASI_ENHANCED.md)**
   - Detailed code explanation
   - Architecture deep dive
   - Protocol specification
   - Troubleshooting guide

2. **[QUICK_IMPLEMENTATION_GUIDE.md](QUICK_IMPLEMENTATION_GUIDE.md)**
   - 10-minute setup guide
   - Step-by-step instructions
   - Testing checklist

3. **[VERSION_COMPARISON.md](VERSION_COMPARISON.md)**
   - V1.0 vs V2.0 comparison
   - Migration guide
   - Feature comparison

---

## 🔍 Troubleshooting

### ❌ Cannot connect to ESP32

**Solutions:**
1. Check ESP32 is powered on
2. Verify Bluetooth pairing in Settings
3. Restart ESP32
4. Re-pair device

### ❌ Status not syncing

**Solutions:**
1. Check ESP32 code is `ESP32_CODE_ENHANCED.ino`
2. Verify Serial Monitor shows "STATUS:X,Y"
3. Check `MainActivity.onResume()` calls `refreshLampStatus()`

### ❌ Build error: Cannot resolve ViewModel

**Solutions:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies

# Or in Android Studio:
# File → Invalidate Caches / Restart
```

More troubleshooting: [DOKUMENTASI_ENHANCED.md#troubleshooting](DOKUMENTASI_ENHANCED.md#troubleshooting)

---

## 🎯 Future Enhancements

- [ ] Brightness control (PWM)
- [ ] RGB LED support
- [ ] Schedule/Timer functionality
- [ ] Save last connected device
- [ ] Multiple ESP32 support
- [ ] WiFi mode (HTTP API)
- [ ] Voice command integration
- [ ] Widget support

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Nazila**
- 📚 Universitas: [Your University]
- 📧 Email: [Your Email]
- 💼 LinkedIn: [Your LinkedIn]
- 🐙 GitHub: [@yourusername](https://github.com/yourusername)

---

## 🙏 Acknowledgments

- ESP32 Bluetooth library by Espressif
- Jetpack Compose by Google
- Material Design 3 guidelines
- Android Architecture Components

---

## 📊 Stats

![Code Size](https://img.shields.io/github/languages/code-size/yourusername/RemoteLamp)
![Repo Size](https://img.shields.io/github/repo-size/yourusername/RemoteLamp)
![Last Commit](https://img.shields.io/github/last-commit/yourusername/RemoteLamp)
![Issues](https://img.shields.io/github/issues/yourusername/RemoteLamp)

---

## ⭐ Star History

If you find this project useful, please give it a star ⭐

---

<div align="center">

**Made with ❤️ for WMC (Web and Mobile Computing) Course**

**Version 2.0 - November 2025**

[⬆ Back to Top](#-remote-lamp-controller---version-20-enhanced)

</div>

