# 📱 Aplikasi Remote Lamp - Bluetooth Controller

Aplikasi Android sederhana untuk mengontrol Smart Lamp via Bluetooth menggunakan Kotlin + Jetpack Compose.

## ✨ Fitur

- ✅ **Koneksi Bluetooth** - Connect ke ESP32/Arduino via Bluetooth
- ✅ **Kontrol ON/OFF** - Tombol sederhana untuk nyalakan/matikan lampu
- ✅ **UI Modern** - Material 3 Design dengan animasi smooth
- ✅ **Real-time Status** - Monitor status koneksi secara realtime
- ✅ **User Friendly** - Interface sederhana dan mudah digunakan

## 🏗️ Struktur Project

```
RemoteLamp/
├── app/src/main/
│   ├── AndroidManifest.xml          # Konfigurasi permission & activity
│   └── java/com/remotelamp/app/
│       ├── MainActivity.kt          # Activity utama & navigation
│       ├── BluetoothController.kt   # Logic Bluetooth (connect, send, disconnect)
│       ├── BluetoothScreen.kt       # Screen untuk connect device
│       ├── ControlScreen.kt         # Screen untuk kontrol ON/OFF
│       └── ui/theme/
│           ├── Theme.kt             # Material 3 theme
│           └── Color.kt             # Color palette
└── build.gradle.kts
```

## 📋 Penjelasan File-File Penting

### 1. **AndroidManifest.xml**
```xml
<!-- Permission Bluetooth untuk Android 12+ -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

<!-- Permission Bluetooth untuk Android 11 ke bawah -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

**Fungsi:**
- Mendefinisikan permission yang diperlukan untuk akses Bluetooth
- Permission berbeda untuk Android 12+ (API 31+) dan versi sebelumnya

---

### 2. **MainActivity.kt**

**Fungsi Utama:**
- ✅ Request permission Bluetooth saat aplikasi dibuka
- ✅ Cek apakah Bluetooth enabled
- ✅ Mengelola navigasi antar screen (Connect → Control)
- ✅ Mengelola lifecycle BluetoothController

**Alur Kerja:**
```
onCreate()
  ↓
checkBluetoothPermissions()
  ↓
checkBluetoothEnabled()
  ↓
Tampilkan ConnectScreen
  ↓
Setelah connected → Tampilkan ControlScreen
```

**Code Penting:**
```kotlin
// Inisialisasi BluetoothController
bluetoothController = BluetoothController(this)

// Request permission untuk Android 12+
private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    // Handle permission result
}
```

---

### 3. **BluetoothController.kt**

**Class ini adalah JANTUNG dari aplikasi!**

#### Fungsi-fungsi Utama:

**a) `isBluetoothAvailable()` & `isBluetoothEnabled()`**
```kotlin
fun isBluetoothAvailable(): Boolean
fun isBluetoothEnabled(): Boolean
```
Cek apakah Bluetooth tersedia dan aktif di device.

**b) `getPairedDevices()`**
```kotlin
fun getPairedDevices(): List<BluetoothDevice>
```
Mendapatkan daftar device yang sudah di-pair (tidak perlu scan/discovery).

**c) `connect(deviceAddress: String)`**
```kotlin
fun connect(deviceAddress: String): Boolean
```
**Ini fungsi paling penting!**

Alur koneksi:
1. Dapatkan BluetoothDevice berdasarkan MAC address
2. Cancel discovery untuk hemat resource
3. Buat RFCOMM socket dengan UUID SPP standar (`00001101-0000-1000-8000-00805F9B34FB`)
4. Connect ke device (blocking call)
5. Simpan OutputStream untuk kirim data

**d) `sendCommand(command: String)`**
```kotlin
fun sendCommand(command: String): Boolean
```
Kirim perintah ke ESP32:
- `"1"` → Nyalakan lampu
- `"0"` → Matikan lampu

**e) `disconnect()`**
```kotlin
fun disconnect()
```
Tutup koneksi Bluetooth dengan aman.

#### UUID SPP (Serial Port Profile)
```kotlin
private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
```
- UUID standar untuk komunikasi serial via Bluetooth
- **WAJIB sama** dengan UUID yang digunakan di kode ESP32/Arduino

---

### 4. **BluetoothScreen.kt** (ConnectScreen)

**Fungsi:**
- Menampilkan UI untuk koneksi Bluetooth
- List paired devices yang bisa dipilih
- Menampilkan status koneksi

**Alur Kerja:**
```
User klik "Connect Device"
  ↓
Panggil bluetoothController.getPairedDevices()
  ↓
Tampilkan list device di dialog
  ↓
User pilih device
  ↓
Panggil bluetoothController.connect(device.address) di background thread
  ↓
Jika berhasil → pindah ke ControlScreen
```

**Code Penting:**
```kotlin
// Koneksi di background thread agar UI tidak freeze
scope.launch {
    val success = withContext(Dispatchers.IO) {
        bluetoothController.connect(device.address)
    }
    
    if (success) {
        onConnected() // Pindah ke ControlScreen
    }
}
```

---

### 5. **ControlScreen.kt**

**Fungsi:**
- UI untuk kontrol lampu ON/OFF
- Visual indicator status lampu (animasi)
- Monitor status koneksi realtime

**Alur Kerja Tombol ON:**
```
User klik "TURN ON"
  ↓
Cek apakah masih connected
  ↓
Panggil bluetoothController.sendCommand("1")
  ↓
Jika success → Update UI (lampu nyala)
```

**Code Penting:**
```kotlin
// Tombol TURN ON
Button(
    onClick = {
        if (bluetoothController.isConnected) {
            val success = bluetoothController.sendCommand("1")
            if (success) {
                lampStatus = true // Update UI
            }
        }
    }
)
```

**Fitur UI:**
- Background gradient berubah sesuai status lampu (terang saat ON, gelap saat OFF)
- Animasi glow pada icon lampu saat nyala
- Tombol otomatis disabled sesuai kondisi (ON disabled saat sudah nyala)

---

## 🔌 Cara Kerja Komunikasi Bluetooth

### 1. **Pairing (Manual di Android Settings)**
```
Android Settings → Bluetooth → Pair ESP32
```

### 2. **Koneksi dari Aplikasi**
```kotlin
// 1. Get paired device
val devices = bluetoothController.getPairedDevices()

// 2. Connect using MAC address
val success = bluetoothController.connect("98:D3:41:F6:32:1B")

// 3. Send command
bluetoothController.sendCommand("1") // ON
bluetoothController.sendCommand("0") // OFF
```

### 3. **Protocol Komunikasi**
```
Android App  ----Bluetooth---->  ESP32
              "1" (ON)
              "0" (OFF)
```

---

## 🎨 UI/UX Design

### Color Scheme (Material 3)
- **Primary**: `#4285F4` (Biru Google)
- **Secondary**: `#FFC107` (Kuning untuk lampu)
- **Tertiary**: `#4CAF50` (Hijau untuk status)
- **Background**: `#0D1117` (Dark)

### Animasi
1. **ConnectScreen**: 
   - Animasi glow pada icon Bluetooth
   
2. **ControlScreen**:
   - Animasi perubahan warna background (ON/OFF)
   - Animasi glow pada bulb icon saat lampu nyala
   - Smooth transition dengan `animateColorAsState`

---

## 📱 Cara Menggunakan Aplikasi

### Step 1: Pair ESP32 di Android Settings
1. Buka **Settings → Bluetooth**
2. Nyalakan Bluetooth
3. Cari device ESP32 (contoh: "ESP32_LAMP")
4. Pair dengan device

### Step 2: Buka Aplikasi
1. Aplikasi akan auto-request permission Bluetooth
2. Izinkan semua permission yang diminta

### Step 3: Connect ke Device
1. Klik tombol **"Connect Device"**
2. Pilih ESP32 dari daftar paired devices
3. Tunggu hingga status "Connected"

### Step 4: Kontrol Lampu
1. Klik **"🔆 TURN ON"** untuk nyalakan lampu
2. Klik **"🌙 TURN OFF"** untuk matikan lampu
3. Lihat perubahan visual di UI

---

## 🔧 Kode ESP32/Arduino

Untuk ESP32, gunakan kode berikut:

```cpp
#include <BluetoothSerial.h>

BluetoothSerial SerialBT;

const int LAMP_PIN = 2; // Pin LED di ESP32

void setup() {
  Serial.begin(115200);
  pinMode(LAMP_PIN, OUTPUT);
  
  // Inisialisasi Bluetooth dengan nama "ESP32_LAMP"
  SerialBT.begin("ESP32_LAMP");
  Serial.println("Bluetooth Device is Ready to Pair");
}

void loop() {
  // Cek jika ada data dari Bluetooth
  if (SerialBT.available()) {
    char command = SerialBT.read();
    
    if (command == '1') {
      // Nyalakan lampu
      digitalWrite(LAMP_PIN, HIGH);
      Serial.println("Lamp ON");
      SerialBT.println("Lamp turned ON");
    }
    else if (command == '0') {
      // Matikan lampu
      digitalWrite(LAMP_PIN, LOW);
      Serial.println("Lamp OFF");
      SerialBT.println("Lamp turned OFF");
    }
  }
  
  delay(20);
}
```

**Wiring ESP32:**
```
ESP32 Pin 2 → Resistor 220Ω → LED → GND
```

---

## 🐛 Troubleshooting

### Problem 1: "Tidak ada device paired"
**Solusi:**
- Pastikan ESP32 sudah di-pair di Android Settings terlebih dahulu
- Restart Bluetooth di Android
- Restart ESP32

### Problem 2: "Connection failed"
**Solusi:**
- Pastikan ESP32 dalam keadaan ON
- Cek apakah ESP32 tidak sedang terkoneksi ke device lain
- Coba unpair dan pair ulang
- Pastikan UUID di ESP32 sama (`00001101-0000-1000-8000-00805F9B34FB`)

### Problem 3: "Permission denied"
**Solusi:**
- Buka Settings → Apps → RemoteLamp → Permissions
- Berikan permission Bluetooth manually

### Problem 4: Lampu tidak merespon
**Solusi:**
- Cek Serial Monitor ESP32 apakah menerima data
- Pastikan kabel LED terhubung dengan benar
- Cek apakah command yang dikirim benar ("1" atau "0")

---

## 🚀 Build & Run

### Build APK
```bash
cd "RemoteLamp"
./gradlew assembleDebug
```

APK akan tersimpan di:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Install ke Android Device
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📚 Dependencies yang Digunakan

```kotlin
// Jetpack Compose
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")
implementation("androidx.activity:activity-compose:1.9.3")

// Coroutines untuk async operation
kotlinx.coroutines
```

---

## 🎓 Konsep yang Dipelajari

### 1. **Bluetooth Classic Communication**
- Pairing & bonding
- RFCOMM socket
- SPP (Serial Port Profile)
- MAC address

### 2. **Android Permissions**
- Runtime permissions
- Permission untuk Android 12+
- ActivityResultContracts API

### 3. **Jetpack Compose**
- State management (`remember`, `mutableStateOf`)
- Navigation tanpa NavController
- Recomposition
- Side effects (`LaunchedEffect`)

### 4. **Coroutines**
- Background thread dengan `Dispatchers.IO`
- `suspend` functions
- `launch` dan `withContext`

### 5. **Material Design 3**
- Color scheme
- Typography
- Elevation & shadow
- Animations

---

## 💡 Tips & Best Practices

1. **Selalu cek koneksi sebelum kirim data**
   ```kotlin
   if (bluetoothController.isConnected) {
       bluetoothController.sendCommand("1")
   }
   ```

2. **Lakukan koneksi di background thread**
   ```kotlin
   scope.launch {
       withContext(Dispatchers.IO) {
           bluetoothController.connect(address)
       }
   }
   ```

3. **Disconnect saat aplikasi ditutup**
   ```kotlin
   override fun onDestroy() {
       super.onDestroy()
       bluetoothController.disconnect()
   }
   ```

4. **Handle SecurityException untuk permission**
   ```kotlin
   try {
       device.name
   } catch (e: SecurityException) {
       "Unknown Device"
   }
   ```

---

## 📝 Kesimpulan

### Backend?
**TIDAK ADA BACKEND** dalam aplikasi ini. Ini adalah aplikasi client-side yang berkomunikasi langsung dengan ESP32 via Bluetooth. 

Alur data:
```
Android App <--Bluetooth--> ESP32 <--GPIO--> LED
```

### Kelebihan Aplikasi Ini:
✅ Sederhana dan mudah dipahami pemula
✅ Kode bersih dengan komentar lengkap
✅ UI modern dengan Material 3
✅ Error handling yang baik
✅ Tidak butuh internet/server

### Pengembangan Selanjutnya:
- Tambah slider brightness
- Tambah color picker untuk RGB LED
- Save device address (SharedPreferences)
- Multiple device support
- Schedule/timer on/off

---

## 👨‍💻 Dibuat oleh
Nazila - November 2024
Untuk tugas WMC (Web and Mobile Computing)

---

## 📄 Lisensi
Free to use untuk keperluan belajar.

---

**Happy Coding! 🎉**

